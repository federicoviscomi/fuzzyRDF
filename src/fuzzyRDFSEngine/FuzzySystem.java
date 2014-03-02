/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzyRDFSEngine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import queryInterpreter.LexicalErrorException;
import queryInterpreter.SyntacticErrorException;

import tnorms.TnormFunction;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.reasoner.rulesys.Builtin;
import com.hp.hpl.jena.tdb.base.file.Location;

/**
 * 
 * @author feffo
 */
public interface FuzzySystem {

	public static enum StorageTypes {

		MAIN_MEMORY, FILE, DATABASE
	};

	public void close() throws SQLException;

	public void write(PrintWriter out, String language) throws SQLException;

	/** la proprieta' di grado da usare negli statement fuzzy */
	public static final Property degree = ResourceFactory.createProperty(
			"http://gaia.isti.cnr.it/~straccia#", "degree");
	public static final String TDB_ROOT = "tdbRoot";

	/**
	 * Elimina tutte le triple
	 */
	public void removeAll() throws SQLException;

	/**
	 * Legge dal file di nome <param>inputFileName</param> un grafo RDF scritto
	 * nel linguaggio specificato da <param>lang</param> e lo aggiunge al
	 * modello. C'e' una severa limitazione sulle triple che e' possibile
	 * aggiungere nel sistema. Il predicato che e' in altre parole la seconda
	 * componente delle triple deve essere tra le seguenti:
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
	public void read(String inputFileName, String lang)
			throws FileNotFoundException, SQLException;

	public void setTnorm(TnormFunction tnorm);

	public double getTotalStatementsCount();

	public long size();

	public ResultSet submitQuery(String string) throws SQLException;

	public ResultSet submitQuery(BufferedReader in) throws IOException,
			LexicalErrorException, SyntacticErrorException, SQLException;

	public void writeFromModel(PrintWriter fileOut, String string);

}
