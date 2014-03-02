/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzyRDFSEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import queryInterpreter.LexicalErrorException;
import queryInterpreter.SyntacticErrorException;
import queryInterpreter.TargetCodeGenerator;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import commandParser.ParserAndExecuterEngine.InfLevel;
import fuzzyRDFSEngine.DebugInfo.DebugLevels;

/**
 * 
 * @author feffo
 */
public class FuzzySystemMonetdb extends AbstractFuzzySystem {

	private MonetDBWrapper monetdbWrapper;

	public FuzzySystemMonetdb(String database, String user, String password,
			int infLevel) throws ClassNotFoundException, SQLException,
			MissingRulesDirectoryException {
		this(database, user, password, "//localhost/", infLevel);
	}

	public FuzzySystemMonetdb(String database, String user, String password,
			String databaseLocation, int infLevel)
			throws ClassNotFoundException, SQLException {
		monetdbWrapper = new MonetDBWrapper(database, user, password,
				databaseLocation);
		if (infLevel == InfLevel.ALL_INF.ordinal())
			model = new GenericModel(ModelFactory.createInfModel(reasoner,
					TDBFactory.createModel(database + user + ".dir.fuzzy")));
		else
			model = new GenericModel(TDBFactory.createModel(database + user
					+ ".dir.fuzzy"));
		// TODO carica i dati dal database
		// ottiene l'elenco di tutte le tabelle user-defined
		// per ogni tabella con nome t
		// per ogni tripla (s, o, d) in t
		// aggiunge al modello la reificazione dello statement (s, t, o)[d]
	}

	/**
	 * Elimina tutte le triple
	 */
	public void removeAll() throws SQLException {
		model.removeAll();
		monetdbWrapper.clearDatabase();
	}

	public void close() throws SQLException {
		if (model != null) {
			model.close();
			model = null;
		}
		if (monetdbWrapper != null) {
			monetdbWrapper.close();
			monetdbWrapper = null;
		}
	}

	/**
	 * Legge dal file di nome <param>inputFileName</param> un grafo RDF scritto
	 * nel linguaggio specificato da <param>lang</param> e lo aggiunge al
	 * modello. C'e' una severa limitazione sulle triple che e' possibile
	 * aggiungere nel sistema. I predicati in altre parole la seconda componente
	 * delle triple deve essere tra le seguenti:
	 * <ul>
	 * <li>rdf:type e in questo caso la terza componente della tripla deve
	 * essere rdf:Statement
	 * <li>rdf:subject
	 * <li>rdf:predicate
	 * <li>rdf:object
	 * <li>fuzzy:degree
	 * </ul>
	 * detto diversamente, si possono aggiungere solo reificazioni di
	 * affermazioni.
	 * 
	 * 
	 * @param inputFileName
	 *            il file che contiene il grafo RDF da aggiungere al modell
	 * @param lang
	 *            il linguaggio nel quale e' codificato all'interno del file il
	 *            grafo RDF da aggiungere
	 */
	@Override
	public void read(String inputFileName, String lang)
			throws FileNotFoundException, SQLException {
		super.read(inputFileName, lang);
		// TODO usare un meccanismo incrementale !!!!
		monetdbWrapper.clearDatabase();
		monetdbWrapper.addAll(model);
	}

	/**
	 * Scrive sull'output <param>out</param> tutto il contenuto del modello
	 * 
	 * @param out
	 *            l'output sul quale scrivere
	 * @param lang
	 *            il linguaggio di output
	 * @throws SQLException
	 */
	public void write(PrintWriter out, String lang) throws SQLException {
		// model.write(out, lang);
		monetdbWrapper.write(out);
	}

	public MonetDBWrapper getWrapper() {
		return monetdbWrapper;
	}

	public ResultSet submitQuery(String string) throws SQLException {
		return monetdbWrapper.submitQuery(string);
	}

	@Override
	public ResultSet submitQuery(BufferedReader in) throws IOException,
			LexicalErrorException, SyntacticErrorException, SQLException {
		File temp = File.createTempFile("temporary", ".sql");
		TargetCodeGenerator tcg = new TargetCodeGenerator(in, monetdbWrapper,
				temp, monetdbWrapper.predicateToTableNameMap);
		if (!tcg.generateNextStatement())
			return null;
		FileReader tempReader = new FileReader(temp);
		char[] cbuf = new char[(int) temp.length()];
		tempReader.read(cbuf);
		DebugInfo.setDebugLevel(DebugLevels.ALLWRAPPEROPERATION, true);
		DebugInfo.setDebugLevel(DebugLevels.DATABASE, true);
		String query = new String(cbuf);
		System.out.println(query);
		System.out.flush();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return monetdbWrapper.submitQuery(query);
	}

	public void printAllUserTables() throws SQLException {
		monetdbWrapper.printAllUserTablesName();
	}

	public ResultSet submitSQLQuery(String string) throws SQLException {
		return monetdbWrapper.submitQuery(string);
	}

}
