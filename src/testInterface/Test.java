package testInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import queryInterpreter.LexicalErrorException;
import queryInterpreter.SyntacticErrorException;

import com.hp.hpl.jena.rdf.model.impl.RDFDefaultErrorHandler;

import commandParser.ParserAndExecuterEngine;
import commandParser.ParserAndExecuterEngine.InfLevel;
import fuzzyRDFSEngine.FuzzySystem;
import fuzzyRDFSEngine.FuzzySystemMainMemory;
import fuzzyRDFSEngine.FuzzySystemMonetdb;
import fuzzyRDFSEngine.MissingRulesDirectoryException;
import fuzzyRDFSEngine.SQLUtils;

public class Test {

	private static final String PREFIX = "test";

	public static void main(String[] args) throws Exception {
		testTranslator();
		// testT1();
	}

	static void testT1() throws ClassNotFoundException, SQLException,
			MissingRulesDirectoryException, FileNotFoundException {
		FuzzySystemMonetdb fs = new FuzzySystemMonetdb("database", "monetdb",
				"monetdb", InfLevel.ALL_INF.ordinal());
		fs.read("file1.xml", null);
		PrintWriter out = new PrintWriter(System.out);
		fs.writeFromModel(out, null);
		out.flush();
		fs.printAllUserTables();
		ResultSet queryRes = fs.submitSQLQuery("SELECT * FROM rdftype");
		SQLUtils.printAll(queryRes, System.out);
	}

	private static void testTranslator() throws ClassNotFoundException,
			SQLException, MissingRulesDirectoryException, IOException,
			LexicalErrorException, SyntacticErrorException {
		FuzzySystemMonetdb fs = new FuzzySystemMonetdb("database", "monetdb",
				"monetdb", InfLevel.ALL_INF.ordinal());
		fs.read("file1.xml", null);
		PrintWriter out = new PrintWriter(System.out);
		fs.writeFromModel(out, "N3");
		// fs.write(out, null);
		out.flush();
		if (true)
			return;
		fs.printAllUserTables();
		BufferedReader in = new BufferedReader(new FileReader("file1"));
		ResultSet queryRes;
		while ((queryRes = fs.submitQuery(in)) != null)
			SQLUtils.printAll(queryRes, System.out);
		in.close();
	}

	static void test2() throws MissingRulesDirectoryException, SQLException {
		FuzzySystem memInf = new FuzzySystemMainMemory(InfLevel.ALL_INF
				.ordinal());
		memInf.close();
	}

	static void wholeDirTest() throws IOException {
		long testStartTime = System.currentTimeMillis();
		File dir = new File("/home/feffo/rdfTestFile/");
		if (!dir.exists() || !dir.isDirectory())
			throw new Error();
		PrintWriter out = new PrintWriter(new FileWriter("testWhole"), true);
		String[] list = dir.list();
		int max = 1;
		for (int i = 0; i < dir.length() && i < max; i++) {
			try {
				String[] fileNames = create_testsfile_mod1(list[i],
						"testFileCommand", dir.getAbsolutePath());
				canonical_test(out, fileNames);
			} catch (OutOfMemoryError e) {
				System.gc();
				out.println("\n");
				e.printStackTrace(out);
				out.println("\n" + list[i] + " OutOfMemoryError");
			}
			System.err.println(" " + i + " ok. elapsed time "
					+ (System.currentTimeMillis() - testStartTime));
		}
		out.close();
	}

	static void wholeDirTestMemNoinf() throws IOException {
		long testStartTime = System.currentTimeMillis();
		File dir = new File("/home/feffo/rdfTestFile/");
		if (!dir.exists() || !dir.isDirectory())
			throw new Error();
		PrintWriter out = new PrintWriter(new FileWriter("testWhole"), true);
		String[] list = dir.list();
		int max = 100;
		for (int i = 0; i < dir.length() && i < max; i++) {
			try {
				String[] fileNames = create_testfile_memnoinf(list[i],
						"testFileCommand", dir.getAbsolutePath());
				canonical_test(out, fileNames);
			} catch (OutOfMemoryError e) {
				System.gc();
				out.println("\n");
				e.printStackTrace(out);
				out.println("\n" + list[i] + " OutOfMemoryError");
			}
			System.err.println(" " + i + " ok. elapsed time "
					+ (System.currentTimeMillis() - testStartTime));
		}
		out.close();
	}

	static void testBig1() {
		String[] fileNames = create_testsfile_mod1("index.rdf",
				"testFileCommand", "testRDFXMLdir/");
		canonical_test(fileNames);
	}

	static void testBig2() {
		String[] fileNames = create_testsfile_mod1(
				"genes_with_differing_annotations.rdf", "testFileCommand",
				"testRDFXMLdir/");
		canonical_test(fileNames);
	}

	static String[] create_testsfile_mod1(String fileName, String testFileDir,
			String fileDir) {
		try {
			fileDir = fileDir + File.separatorChar;
			String[] fileNames = new String[3];
			PrintWriter out;

			fileNames[0] = testFileDir + "/" + PREFIX + fileName + "Noinf";
			out = new PrintWriter(fileNames[0]);
			out.println("init mem noinf");
			out.println("add " + fileDir + fileName);
			// out.println("show out");
			out.println("exit");
			out.close();

			fileNames[1] = testFileDir + "/" + PREFIX + fileName + "Degree1";
			out = new PrintWriter(fileNames[1]);
			out.println("init mem allinf");
			out.println("add " + fileDir + fileName);
			// out.println("show out");
			out.println("exit");
			out.close();

			fileNames[2] = testFileDir + "/" + PREFIX + fileName
					+ "RandomReification";
			out = new PrintWriter(fileNames[2]);
			out.println("init mem allinf");
			out.println("rewrite " + fileDir + fileName);
			out.println("add " + fileDir + fileName + ".rwd");
			// out.println("show out");
			out.println("exit");
			out.close();
			return fileNames;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}

	static String[] create_testfile_memnoinf(String fileName,
			String testFileDir, String fileDir) {
		try {
			fileDir = fileDir + File.separatorChar;
			String[] fileNames = new String[1];
			PrintWriter out;

			fileNames[0] = testFileDir + "/" + PREFIX + fileName + "Noinf";
			out = new PrintWriter(fileNames[0]);
			out.println("init mem noinf");
			out.println("add " + fileDir + fileName);
			// out.println("show out");
			out.println("exit");
			out.close();

			return fileNames;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return null;
	}

	static void testRewrite() {
		canonical_test("testSmallRandomReification");
	}

	static void canonical_test(String... fileNames) {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(
					"wholeTestOut"), true);
			for (String file : fileNames) {
				BufferedReader in = new BufferedReader(new FileReader(file));
				Thread thread = new Thread(new ParserAndExecuterEngine(in, out,
						true));
				thread.start();
				thread.join();
				out.flush();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	static void canonical_test(PrintWriter out, String... fileNames) {
		try {
			for (String file : fileNames) {
				BufferedReader in = new BufferedReader(new FileReader(file));
				ParserAndExecuterEngine pee = new ParserAndExecuterEngine(in,
						out, true);
				pee.run();
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
