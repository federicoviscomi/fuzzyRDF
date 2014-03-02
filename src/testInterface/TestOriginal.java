package testInterface;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;

import fuzzyRDFSEngine.AbstractFuzzySystem;

public class TestOriginal {
	private static final int MAX = 150;

	public static void main(String[] args) throws IOException {
		test(1111);
	}

	static void test(int testMask) throws IOException {
		long testStartTime = System.currentTimeMillis();
		File dir = new File("/home/feffo/rdfTestFile/");
		if (!dir.exists() || !dir.isDirectory())
			throw new Error();
		PrintWriter outNoinf = new PrintWriter(new FileWriter(
				"noinfOutOriginal"), true);
		PrintWriter outNoinfNomem = new PrintWriter(new FileWriter(
				"noinfNomemOutOriginal"), true);
		PrintWriter outInf = new PrintWriter(new FileWriter("infOutOriginal"),
				true);
		PrintWriter outInfNomem = new PrintWriter(new FileWriter(
				"infNomemOutOriginal"), true);
		outNoinf.format("| %50.50s   %50.50s   %50.50s\n", "file name",
				"milliseconds", "statements count");
		outNoinfNomem.format("| %50.50s   %50.50s   %50.50s\n", "file name",
				"milliseconds", "statements count");
		outInf.format("| %50.50s   %50.50s   %50.50s\n", "file name",
				"milliseconds", "statements count");
		String[] list = dir.list();
		for (int i = 0; i < list.length && i < MAX; i++) {
			if ((testMask % 10) / 1 != 0) {
				try {
					testASingleFileNoinfTDBNomem(dir.getAbsolutePath()
							+ File.separatorChar + list[i], outNoinfNomem);
				} catch (OutOfMemoryError e) {
					System.gc();
					outNoinfNomem.println(list[i] + " OutOfMemoryError");
				}
			}
			if ((testMask % 100) / 10 != 0) {
				try {
					testASingleFileInfTDB(dir.getAbsolutePath()
							+ File.separatorChar + list[i], outInf);
				} catch (OutOfMemoryError e) {
					System.gc();
					outInf.println(list[i] + " OutOfMemoryError");
				}
			}
			if ((testMask % 1000) / 100 != 0) {
				try {
					testASingleFileInfTDBNomem(dir.getAbsolutePath()
							+ File.separatorChar + list[i], outInfNomem);
				} catch (OutOfMemoryError e) {
					System.gc();
					outInfNomem.println(list[i] + " OutOfMemoryError");
				}
			}
			if ((testMask % 10000) / 1000 != 0) {
				try {
					testASingleFileNoinfTDB(dir.getAbsolutePath()
							+ File.separatorChar + list[i], outNoinf);
				} catch (OutOfMemoryError e) {
					System.gc();
					outNoinf.println(list[i] + " OutOfMemoryError");
				}
			}
			System.err.println(" " + i + " ok. elapsed time "
					+ (System.currentTimeMillis() - testStartTime));
		}
		outNoinf.close();
		outInf.close();
		outNoinfNomem.close();
	}

	public static void testASingleFileInfTDB(String fileName, PrintWriter out) {
		InfModel infModel = ModelFactory.createInfModel(
				AbstractFuzzySystem.defaultAllReasoner, TDBFactory
						.createModel());
		double startTime = System.currentTimeMillis();
		infModel.read("file:" + fileName, null);
		double endTime = System.currentTimeMillis();
		double elapsedTime = endTime - startTime;
		out.format("| %50.50s   %30.30g   %40d\n", fileName, elapsedTime,
				infModel.size());
		out.flush();
		infModel.removeAll();
		infModel.close();
	}

	public static void testASingleFileInfTDBNomem(String fileName,
			PrintWriter out) {
		File dir = new File(
				"/home/feffo/workspace/testBigfileDefaultTDBJena/infnomem");
		for (File entry : dir.listFiles())
			if (!entry.delete()) {
				System.err.println("ERROR: unable to delete "
						+ entry.getAbsolutePath());
				System.exit(-1);
			}
		InfModel infModel = ModelFactory
				.createInfModel(
						AbstractFuzzySystem.defaultAllReasoner,
						TDBFactory
								.createModel("/home/feffo/workspace/testBigfileDefaultTDBJena/infnomem"));
		double startTime = System.currentTimeMillis();
		infModel.read("file:" + fileName, null);
		double endTime = System.currentTimeMillis();
		double elapsedTime = endTime - startTime;
		out.format("| %50.50s   %30.30g   %40d\n", fileName, elapsedTime,
				infModel.size());
		out.flush();
		infModel.removeAll();
		infModel.close();
	}

	public static void testASingleFileNoinfTDB(String fileName, PrintWriter out) {
		Model tdbModel = TDBFactory.createModel();
		double startTime = System.currentTimeMillis();
		tdbModel.read("file:" + fileName, null);
		double endTime = System.currentTimeMillis();
		double elapsedTime = endTime - startTime;
		out.format("| %50.50s   %25.25g   %50d\n", fileName, elapsedTime,
				tdbModel.size());
		out.flush();
		tdbModel.removeAll();
		tdbModel.close();
	}

	public static void testASingleFileNoinfTDBNomem(String fileName,
			PrintWriter out) {
		File dir = new File(
				"/home/feffo/workspace/testBigfileDefaultTDBJena/noinfnomem");
		for (File entry : dir.listFiles())
			if (!entry.delete()) {
				System.err.println("ERROR: unable to delete "
						+ entry.getAbsolutePath());
				System.exit(-1);
			}
		Model tdbModel = TDBFactory
				.createModel("/home/feffo/workspace/testBigfileDefaultTDBJena/noinfnomem");
		double startTime = System.currentTimeMillis();
		tdbModel.read("file:" + fileName, null);
		double endTime = System.currentTimeMillis();
		double elapsedTime = endTime - startTime;
		out.format("| %50.50s   %30.30g   %40d\n", fileName, elapsedTime,
				tdbModel.size());
		out.flush();
		tdbModel.removeAll();
		tdbModel.close();
	}

}
