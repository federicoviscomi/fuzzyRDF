package testInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import com.hp.hpl.jena.rdf.model.ModelFactory;

import commandParser.ParserAndExecuterEngine.InfLevel;

import fuzzyRDFSEngine.AbstractFuzzySystem;
import fuzzyRDFSEngine.FuzzySystemFile;
import fuzzyRDFSEngine.FuzzySystemMainMemory;

public class TestNew {
	private static final int MAX = 150;
	private static PrintWriter err;
	static {
		try {
			err = new PrintWriter(new FileWriter("errNew"), true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Throwable {
		try {
			test(1111);
		} catch (Throwable w) {
			err.close();
			w.printStackTrace();
		}
	}

	static void test(int testMask) throws IOException {
		long testStartTime = System.currentTimeMillis();
		File dir = new File("/home/feffo/rdfTestFile/");
		if (!dir.exists() || !dir.isDirectory())
			throw new Error();
		PrintWriter outNoinf = new PrintWriter(new FileWriter("noinfOutNew"),
				true);
		PrintWriter outNoinfNomem = new PrintWriter(new FileWriter(
				"noinfNomemOutNew"), true);
		PrintWriter outInf = new PrintWriter(new FileWriter("infOutNew"), true);
		PrintWriter outInfNomem = new PrintWriter(new FileWriter(
				"infNomemOutNew"), true);
		outNoinf.format("| %50.50s   %50.50s   %50.50s\n", "file name",
				"milliseconds", "statements count");
		outNoinfNomem.format("| %50.50s   %50.50s   %50.50s\n", "file name",
				"milliseconds", "statements count");
		outInf.format("| %50.50s   %50.50s   %50.50s\n", "file name",
				"milliseconds", "statements count");
		String[] list = dir.list();
		for (int i = 0; i < list.length && i < MAX; i++) {
			try {
				err.println(" " + i + " start a");
				if ((testMask % 10) / 1 != 0) {
					err.println(" " + i + " start a a ");
					String a = dir.getAbsolutePath() + File.separatorChar
							+ list[i];
					err.println(" " + i + " start a b ");
					testASingleFileNoinfTDBNomem(a, outNoinfNomem);
					err.println(" " + i + " start a c");
				}
				err.println(" " + i + " start b");
			} catch (OutOfMemoryError e) {
				outNoinfNomem.println(list[i] + " OutOfMemoryError");
				err.println(" " + i + " start OutOfMemoryError");
			}
			try {
				err.println(" " + i + " 2 a");
				if ((testMask % 100) / 10 != 0) {
					testASingleFileInfTDB(dir.getAbsolutePath()
							+ File.separatorChar + list[i], outInf);
				}
				err.println(" " + i + " 2 b");
			} catch (OutOfMemoryError e) {
				outInf.println(list[i] + " OutOfMemoryError");
				err.println(" " + i + " 2 OutOfMemoryError");
			}
			try {
				err.println(" " + i + " 3 a");
				if ((testMask % 1000) / 100 != 0) {
					testASingleFileInfTDBNomem(dir.getAbsolutePath()
							+ File.separatorChar + list[i], outInfNomem);
				}
				err.println(" " + i + " 3 b");
			} catch (OutOfMemoryError e) {
				outInfNomem.println(list[i] + " OutOfMemoryError");
				err.println(" " + i + " 3 OutOfMemoryError");
			}
			try {
				err.println(" " + i + " 4 a");
				if ((testMask % 10000) / 1000 != 0) {
					testASingleFileNoinfTDB(dir.getAbsolutePath()
							+ File.separatorChar + list[i], outNoinf);
				}
				err.println(" " + i + " 4 b");
			} catch (OutOfMemoryError e) {
				outNoinf.println(list[i] + " OutOfMemoryError");
				err.println(" " + i + " 4 OutOfMemoryError");
			}
			err.println(" " + i + " ok. elapsed time "
					+ (System.currentTimeMillis() - testStartTime));
		}
		outNoinf.close();
		outNoinfNomem.close();
		outInf.close();
		outInfNomem.close();
	}

	public static void testASingleFileInfTDB(String fileName, PrintWriter out) {
		try {
			FuzzySystemMainMemory infModel = null;
			try {
				infModel = new FuzzySystemMainMemory(InfLevel.ALL_INF.ordinal());
				double startTime = System.currentTimeMillis();
				infModel.read("file:" + fileName, null);
				double endTime = System.currentTimeMillis();
				double elapsedTime = endTime - startTime;
				out.format("| %50.50s   %30.30g   %40d\n", fileName,
						elapsedTime, infModel.size());
				out.flush();
				infModel.removeAll();
				infModel.close();
				infModel = null;
			} catch (OutOfMemoryError e) {
				try {
					if (infModel != null) {
						infModel.removeAll();
						infModel.close();
						infModel = null;
					}
				} catch (OutOfMemoryError e2) {
					if (infModel != null) {
						infModel.close();
						infModel = null;
					}
					throw e2;
				}
				throw e;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			} finally {
				if (infModel != null) {
					infModel.removeAll();
					infModel.close();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static void testASingleFileInfTDBNomem(String fileName,
			PrintWriter out) {
		try {
			FuzzySystemFile infModel = null;
			try {
				File dir = new File(
						"/home/feffo/workspace/testBigfileDefaultTDBJena/infnomem");
				for (File entry : dir.listFiles())
					if (!entry.delete()) {
						err.println("ERROR: unable to delete "
								+ entry.getAbsolutePath());
						System.exit(-1);
					}
				infModel = new FuzzySystemFile(
						"/home/feffo/workspace/testBigfileDefaultTDBJena/infnomem",
						InfLevel.ALL_INF.ordinal());
				double startTime = System.currentTimeMillis();
				infModel.read("file:" + fileName, null);
				double endTime = System.currentTimeMillis();
				double elapsedTime = endTime - startTime;
				out.format("| %50.50s   %30.30g   %40d\n", fileName,
						elapsedTime, infModel.size());
				out.flush();
				infModel.removeAll();
				infModel.close();
			} catch (OutOfMemoryError e) {
				try {
					if (infModel != null) {
						infModel.removeAll();
						infModel.close();
						infModel = null;
					}
				} catch (OutOfMemoryError e2) {
					if (infModel != null) {
						infModel.close();
						infModel = null;
					}
					throw e2;
				}
				throw e;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static void testASingleFileNoinfTDB(String fileName, PrintWriter out) {
		try {
			FuzzySystemMainMemory infModel = null;
			try {
				infModel = new FuzzySystemMainMemory(InfLevel.NO_INF.ordinal());
				double startTime = System.currentTimeMillis();
				infModel.read("file:" + fileName, null);
				double endTime = System.currentTimeMillis();
				double elapsedTime = endTime - startTime;
				out.format("| %50.50s   %25.25g   %50d\n", fileName,
						elapsedTime, infModel.size());
				out.flush();
				infModel.removeAll();
				infModel.close();
			} catch (OutOfMemoryError e) {
				try {
					if (infModel != null) {
						infModel.removeAll();
						infModel.close();
						infModel = null;
					}
				} catch (OutOfMemoryError e2) {
					if (infModel != null) {
						infModel.close();
						infModel = null;
					}
					throw e2;
				}
				throw e;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static void testASingleFileNoinfTDBNomem(String fileName,
			PrintWriter out) {
		try {
			FuzzySystemFile infModel = null;
			try {
				err.println("testASingleFileNoinfTDBNomem 1");
				File dir = new File(
						"/home/feffo/workspace/testBigfileDefaultTDBJena/noinfnomem");
				err.println("testASingleFileNoinfTDBNomem 2");
				for (File entry : dir.listFiles())
					if (!entry.delete()) {
						err.println("ERROR: unable to delete "
								+ entry.getAbsolutePath());
						System.exit(-1);
					}
				err.println("testASingleFileNoinfTDBNomem 3");
				infModel = new FuzzySystemFile(
						"/home/feffo/workspace/testBigfileDefaultTDBJena/noinfnomem",
						InfLevel.NO_INF.ordinal());

				err.println("testASingleFileNoinfTDBNomem 4");
				double startTime = System.currentTimeMillis();
				err.println("testASingleFileNoinfTDBNomem 5");
				infModel.read("file:" + fileName, null);
				err.println("testASingleFileNoinfTDBNomem 6");
				double endTime = System.currentTimeMillis();
				err.println("testASingleFileNoinfTDBNomem 7");
				double elapsedTime = endTime - startTime;
				err.println("testASingleFileNoinfTDBNomem 8");
				out.format("| %50.50s   %30.30g   %40d\n", fileName,
						elapsedTime, infModel.size());
				err.println("testASingleFileNoinfTDBNomem 9");
				out.flush();
				err.println("testASingleFileNoinfTDBNomem 10");
				err.println("testASingleFileNoinfTDBNomem 14");
				infModel.removeAll();
				err.println("testASingleFileNoinfTDBNomem 15");
				infModel.close();
				err.println("testASingleFileNoinfTDBNomem 16");
			} catch (OutOfMemoryError e) {
				try {
					if (infModel != null) {
						infModel.removeAll();
						infModel.close();
						infModel = null;
					}
				} catch (OutOfMemoryError e2) {
					if (infModel != null) {
						infModel.close();
						infModel = null;
					}
					throw e2;
				}
				throw e;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
