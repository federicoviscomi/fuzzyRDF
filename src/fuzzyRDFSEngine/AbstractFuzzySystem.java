/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzyRDFSEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.omg.CORBA_2_3.portable.OutputStream;

import queryInterpreter.LexicalErrorException;
import queryInterpreter.SyntacticErrorException;

import tnorms.GenericTnormFunctor;
import tnorms.TnormFunction;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.BuiltinRegistry;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;

import commandParser.ParserAndExecuterEngine.InfLevel;
import fuzzyRDFSEngine.DebugInfo.DebugLevels;

/**
 * 
 * @author feffo
 */
public abstract class AbstractFuzzySystem implements FuzzySystem {

	public static GenericRuleReasoner defaultReificationOnlyReasoner;

	public static GenericRuleReasoner defaultAllReasoner;

	private static PrintWriter log;

	static {
		BuiltinRegistry.theRegistry.register(GenericTnormFunctor.builtin);
		BuiltinRegistry.theRegistry.register(HasANotWeakerStatement.builtin);

		defaultReificationOnlyReasoner = new GenericRuleReasoner(Rule
				.parseRules(""));
		File rulesDir = new File("rules_reification_only");
		if (!rulesDir.exists() || !rulesDir.isDirectory()) {
			System.err.println("ERROR: missing rule directory");
			System.exit(-1);
		}
		String[] list = rulesDir.list();
		for (String entry : list) {
			defaultReificationOnlyReasoner.addRules(Rule
					.rulesFromURL("file:rules_reification_only/" + entry));
		}
		// defaultReificationOnlyReasoner.setMode(GenericRuleReasoner.FORWARD_RETE);

		defaultAllReasoner = new GenericRuleReasoner(Rule.parseRules(""));
		rulesDir = new File("rules");
		if (!rulesDir.exists() || !rulesDir.isDirectory()) {
			System.err.println("ERROR: missing rule directory");
			System.exit(-1);
		}
		list = rulesDir.list();
		for (String entry : list) {
			defaultAllReasoner.addRules(Rule
					.rulesFromURL("file:rules/" + entry));
		}
		try {
			log = new PrintWriter(new FileWriter("fuzzy_systems_log"), true);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	GenericRuleReasoner reasoner = defaultAllReasoner;

	GenericModel model;

	/**
	 * Viene eseguito prima della terminazione del programma. Si occupa
	 * semplicemente di chiudere la connessione col database.
	 */
	private class ShutDownHook extends Thread {

		@Override
		public void run() {
			try {
				close();
				log.close();
			} catch (SQLException ex) {
				Logger.getLogger(AbstractFuzzySystem.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		}
	}

	AbstractFuzzySystem() {
		log.println(" created ");
		Runtime.getRuntime().addShutdownHook(new ShutDownHook());
	}

	public void read(String inputFileName, String lang)
			throws FileNotFoundException, SQLException {
		InputStream in = FileManager.get().open(inputFileName);
		if (in == null) {
			throw new FileNotFoundException("File: " + inputFileName
					+ " not found");
		}
		/** legge le triple RDF dal input specificato e ne calcola la chiusura */
		/**
		 * Predefined values for lang are "RDF/XML", "N-TRIPLE", "TURTLE" (or
		 * "TTL") and "N3". null represents the default language, "RDF/XML".
		 * "RDF/XML-ABBREV" is a synonym for "RDF/XML".
		 */
		model.read(in, lang);
	}

	public void removeAll() throws SQLException {
		model.removeAll();
	}

	public void close() throws SQLException {
		if (model != null)
			model.close();
		log.println(" closed ");
	}

	public void write(PrintWriter out, String language) throws SQLException {
		model.write(out, language);
	}

	public void setTnorm(TnormFunction tnorm) {
		GenericTnormFunctor tnormImpl = (GenericTnormFunctor) BuiltinRegistry.theRegistry
				.getImplementation("tnorm");
		tnormImpl.setTnorm(tnorm);
	}

	public double getTotalStatementsCount() {
		return model.getTotalStatementsCount();
	}

	private static class UnicIdGenerator {
		private static long nextId = 0;

		public String nextId() {
			return "ID" + nextId;
		}

		public void incr() {
			nextId++;
		}
	}

	public static void rewrite(String inputFileName, String lang)
			throws FileNotFoundException {
		// TODO should be in a test package
		InputStream in = FileManager.get().open(inputFileName);
		if (in == null) {
			throw new FileNotFoundException("File: " + inputFileName
					+ " not found");
		}
		Model tdbModelSource = TDBFactory.createModel();
		Model tdbModelDest = TDBFactory.createModel();
		tdbModelSource.read(in, lang);
		StmtIterator list = tdbModelSource.listStatements();
		Random random = new Random();
		UnicIdGenerator idGen = new UnicIdGenerator();
		while (list.hasNext()) {
			Statement statement = list.nextStatement();
			if (DebugInfo.isDebugLevelSet(DebugLevels.REWRITE))
				System.err.println("read: " + statement
						+ "\n\tis part of a reification? "
						+ MonetDBWrapper.isPartOfAReification(statement));
			if (!MonetDBWrapper.isPartOfAReification(statement)) {
				Resource id = tdbModelDest.createResource(FuzzySystem.degree
						.getNameSpace()
						+ idGen.nextId());
				tdbModelDest.add(tdbModelDest.createStatement(id, RDF.type,
						RDF.Statement));
				// ^^http://www.w3.org/2001/XMLSchema#decimal
				double randomDegree = random.nextDouble();
				long a = (long) (randomDegree * 10000);
				randomDegree = a / 10000.0;
				tdbModelDest.add(tdbModelDest.createStatement(id,
						FuzzySystem.degree, tdbModelDest
								.createLiteral(randomDegree + "")));
				tdbModelDest.add(tdbModelDest.createStatement(id, RDF.subject,
						statement.getSubject()));
				tdbModelDest.add(tdbModelDest.createStatement(id,
						RDF.predicate, statement.getPredicate()));
				tdbModelDest.add(tdbModelDest.createStatement(id, RDF.object,
						statement.getObject()));
				idGen.incr();
			} else {
				tdbModelDest.add(statement);
			}

		}
		tdbModelSource.removeAll();
		tdbModelSource.close();
		FileOutputStream out = new FileOutputStream(inputFileName + ".rwd");
		tdbModelDest.write(out);
		tdbModelDest.removeAll();
		tdbModelDest.close();
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public java.sql.ResultSet submitQuery(BufferedReader in)
			throws IOException, LexicalErrorException, SyntacticErrorException,
			SQLException {
		StringBuilder query = new StringBuilder();
		String nextLine;
		while ((nextLine = in.readLine()) != null) {
			if (nextLine.trim().equals(""))
				break;
			query.append(nextLine + " \n ");
		}
		return this.submitQuery(query.toString());
	}

	public long size() {
		return model.size();
	}

	public java.sql.ResultSet submitQuery(String query) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void writeFromModel(PrintWriter out, String language) {
		model.write(out, language);
	}

}
