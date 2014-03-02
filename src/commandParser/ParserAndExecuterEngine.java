package commandParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import queryInterpreter.TargetCodeGenerator;

import tnorms.GenericTnormFunctor;
import fuzzyRDFSEngine.AbstractFuzzySystem;
import fuzzyRDFSEngine.FuzzySystem;
import fuzzyRDFSEngine.FuzzySystemFile;
import fuzzyRDFSEngine.FuzzySystemMainMemory;
import fuzzyRDFSEngine.FuzzySystemMonetdb;
import fuzzyRDFSEngine.SQLUtils;

/**
 * 
 * Un interprete minimale di comandi. Il linguaggio interpretato contiene un
 * comando per riga e ha la seguente grammatica espressa nella forma di
 * Backus-Naur:
 * 
 * <pre>
 * &lt;PROGRAM&gt; :== &lt;COMMAND&gt;\n | &lt;COMMAND&gt;&lt;PROGRAM&gt;
 * &lt;COMMAND&gt; :== rewrite FILENAME | settnorm &lt;SETTNORM_ARG&gt;| init &lt;SSO_ARG&gt; [noinf | allinf] | add FILENAME | exit | show [FILENAME]| help | crntdir | clear
 * &lt;SSO_ARG&gt; :== mem | db [DATABASE_NAME USER_NAME PASSWORD [DATABASE_PATH]] | file FILENAME
 * &lt;SETTNORM_ARG&gt; :== goedel | product | lucasiewicz | drastic | nilpotent | hamacher | list
 * </pre>
 * 
 * </p></p> I comandi hanno la seguente semantica: </p>
 * <TABLE border="1">
 * <TR align="center">
 * <TD>sintassi</TD>
 * <TD>semantica</TD>
 * </TR>
 * <TR>
 * <TD>settnorm tnorm</TD>
 * <TD>imposta tnorm come funzione usata dal sistema per il calcolo della norma
 * triangolare</TD>
 * </TR>
 * <TR>
 * <TD>sso</TD>
 * <TD>imposta l'opzione di memorizzazione delle triple del modello</TD>
 * </TR>
 * <TR>
 * <TD>add FILENAME</TD>
 * <TD>FILENAME e' un file che contiene un grafo RDF scritto in xml, tale
 * comando aggiunge il grafo nel modello</TD>
 * </TR>
 * <TR>
 * <TD>exit</TD>
 * <TD>rilascia eventuali risorse acquisite ad esempio una connessione con un
 * database e termina il programma</TD>
 * </TR>
 * <TR>
 * <TD>show [FILENAME]</TD>
 * <TD>mostra il contenuto di tutto il modello sull'output di default del
 * processo oppure sul file di nome FILENAME. In luogo di questo comando si
 * dovrebbero usare le query</TD>
 * </TR>
 * <TR>
 * <TD>help</TD>
 * <TD>visualizza un messaggio che contiene i possibili comandi e il relativo
 * significato</TD>
 * </TR>
 * <TR>
 * <TD>crntdir</TD>
 * <TD>visualizza la direcotory corrente del processo. serve solo per debug</TD>
 * </TR>
 * <TR>
 * <TD>rewrite FILENAME</TD>
 * <TD>legge un grafo dal file FILENAME, e scrive nel file FILENAME.rwd lo
 * stesso grafo ma in forma reificata e nel quale ogni statement ha un grado
 * casuale. Serve solo per fare dei test</TD>
 * </TR>
 * </TABLE>
 * </p></p>
 * 
 * 
 * @author Federico Viscomi 412006 viscomi@cli.di.unipi.it
 * 
 */
public class ParserAndExecuterEngine implements Runnable {

	/** WARNING: put new enum item only from the beginning */
	private static enum CommandCode {
		SHOW_FROM_MODEL, QUERY, REWRITE, SET_TNORM, INIT, ADD_GRAPH, EXIT, SHOW_MODEL_CONTENT, HELP, CURRENT_DIRECTORY, CLEAR, COMMAND_NOT_FOUND, EMPTY_COMMAND
	}

	public static enum InfLevel {
		NO_INF, ALL_INF
	}

	private static enum SetStorageOptionArguementCode {
		MAIN_MEMORY, MONETDB, FILE
	}

	/** FILE deve essere l'ultimo */
	private static enum TnormArgumentCode {
		GOEDEL, PRODUCT, LUCASIEWICZ, DRASTIC, NILPOTENT, HAMACHER, LIST
	}

	private final BufferedReader in;

	private final PrintWriter out;

	private String inputLine;

	private StringTokenizer parser;

	private FuzzySystem fuzzySystem;

	private static final String[] initStorageOptionArgumentArray = { "mem",
			"db", "file" };

	private static final String[] initInferenceOptionArgumentArray = { "noinf",
			"allinf" };

	private static final String[] tnormArgumentArray = { "goedel", "product",
			"lucasiewicz", "drastic", "nilpotent", "hamacher", "list" };

	private static final String[] commandArray = { "showm", "query", "rewrite",
			"settnorm", "init", "add", "exit", "show", "help", "crntdir",
			"clear" };

	private static final String[] commandDescription = {
			"show model content without reading from database in any case",
			"submit a query to the database",
			"legge il file specificato e scrive una altro file al cui nome e' aggiunto il suffisso .rwd e tale da contenere lo stesso grafo del file di partenza ma alle triple non reificate del grafo di partenza sostituisce triple reificate e con gradi casuali",
			"set a tnorm: goedel, product, lucasiewicz, drastic, nilpotent, hamacher, list",
			"initialize the system by settin a storage option ad an inference level. "
					+ "\n sso mem. sso db DATABASENAME USERNAME PASSWORD [DATABASELOCATION]. sso file FILENAME."
					+ "\n use noinf | allinf at the end to set an inference level",
			"add an RDF graph to the current model. add RDFXML_ABSOLUTEFILEPATH",
			"close the system and exit", "show the whole model content",
			"print this help",
			"print the absolute path of the current directory",
			"clear all data in the model" };

	private static final int MISSING_ARGUMENT = -2;

	private static final int ARGUMENT_NOT_VALID = -1;

	private final boolean printCommandMode;

	public ParserAndExecuterEngine(BufferedReader in, PrintStream out,
			boolean printCommandMode) {
		this.in = in;
		this.printCommandMode = printCommandMode;
		this.out = new PrintWriter(out);
	}

	public ParserAndExecuterEngine(BufferedReader in, PrintWriter out,
			boolean printCommandMode) {
		this.in = in;
		this.out = out;
		this.printCommandMode = printCommandMode;
	}

	public ParserAndExecuterEngine(InputStream in, PrintStream out,
			boolean printCommandMode) {
		this.printCommandMode = printCommandMode;
		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = new PrintWriter(out);
	}

	private void execute_add() {
		if (fuzzySystem == null) {
			out.println("\n WARNING: initialize the system first!!");
		} else {
			if (parser.hasMoreTokens()) {
				String inputFileName = parser.nextToken().trim();
				// inputFileName = System.getProperty("user.dir") + "/"
				// + inputFileName;
				try {
					long start = System.currentTimeMillis();
					fuzzySystem.read(inputFileName, "");
					out.print("\ngraph added in "
							+ (System.currentTimeMillis() - start)
							+ " milliseconds");
					double count = fuzzySystem.getTotalStatementsCount();
					out.format(", whole model statements count: %-40.0f\n",
							count);
				} catch (FileNotFoundException e) {
					out.println("\nWARNING: unable to find file: \n\t"
							+ inputFileName);
				} catch (SQLException e) {
					e.printStackTrace();
					return;
				}
			} else {
				out.println("\nSYNTAX ERROR: missing argument ");
			}
		}
	}

	private void execute_clear() {
		if (fuzzySystem == null) {
			out.println("\n WARNING: initialize the system first!!");
		} else {
			try {
				fuzzySystem.removeAll();
				out.println("\nmodel cleared\n");
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	private void execute_commandNoFound() {
		out.println("\ncommand not found");
		out.flush();
	}

	private void execute_currentDirectory() {
		out.println(System.getProperty("user.dir"));
		out.flush();
	}

	private void execute_exit() {
		try {
			if (fuzzySystem != null)
				fuzzySystem.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		out.println("\n bye bye");
		out.flush();
	}

	private void execute_help() {
		out
				.println("--------------------------------------------------------------------------------------------------------------------------");
		out.format("| %15.15s | %100.100s | \n", "COMMAND", "DESCRIPTION");
		out
				.println("-------------------------------------------------------------------------------------------------------------------------");
		for (int i = 0; i < commandArray.length; i++) {
			out.format("| %15.15s | %100.100s | \n", commandArray[i],
					commandDescription[i]);
		}
		out
				.println("--------------------------------------------------------------------------------------------------------------------------");
	}

	private void execute_init() {
		int argument = parseArgument(CommandCode.INIT.ordinal());
		int infLevel = InfLevel.ALL_INF.ordinal();
		if (parser.hasMoreTokens()) {
			String infLevelToken = parser.nextToken();
			for (infLevel = 0; infLevel < initInferenceOptionArgumentArray.length; infLevel++)
				if (initInferenceOptionArgumentArray[infLevel]
						.equals(infLevelToken))
					break;
			if (infLevel >= initInferenceOptionArgumentArray.length) {
				out.println("\n inference level not valid " + infLevelToken
						+ "\nusing default: allinference");
				infLevel = InfLevel.ALL_INF.ordinal();
			}
		}
		if (argument == SetStorageOptionArguementCode.MAIN_MEMORY.ordinal()) {
			try {
				if (fuzzySystem != null)
					fuzzySystem.close();
				fuzzySystem = new FuzzySystemMainMemory(infLevel);
				out.print("\n storage option set: main memory");
				if (infLevel == InfLevel.ALL_INF.ordinal())
					out.println(", inference level: all inference rule");
				else if (infLevel == InfLevel.NO_INF.ordinal())
					out.println(", inference level: no inference");
				else
					throw new Error();
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		} else if (argument == SetStorageOptionArguementCode.MONETDB.ordinal()) {
			try {
				if (fuzzySystem != null)
					fuzzySystem.close();
				String databaseName = "database";
				String userName = "monetdb";
				String password = "monetdb";
				String databaseLocation = "//localhost/";
				if (parser.hasMoreTokens()) {
					databaseName = parser.nextToken().trim();
					userName = parser.nextToken().trim();
					password = parser.nextToken().trim();
					if (parser.hasMoreTokens())
						databaseLocation = parser.nextToken().trim();
				}
				fuzzySystem = new FuzzySystemMonetdb(databaseName, userName,
						password, databaseLocation, infLevel);
				out.println("\n storage option set: monetdb [database name="
						+ databaseName + ", user name=" + userName
						+ ", password=" + password + ", database location="
						+ databaseLocation + "]");
				if (infLevel == InfLevel.ALL_INF.ordinal())
					out.println(", inference level: all inference rule");
				else if (infLevel == InfLevel.NO_INF.ordinal())
					out.println(", inference level: no inference");
				else
					throw new Error();
				out.flush();
			} catch (NoSuchElementException e) {
				out.println("\n missing argument/s");
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		} else if (argument == SetStorageOptionArguementCode.FILE.ordinal()) {
			try {
				if (fuzzySystem != null)
					fuzzySystem.close();
				String rootName;
				if (parser.hasMoreTokens())
					rootName = parser.nextToken().trim();
				else
					rootName = "bibidi";
				fuzzySystem = new FuzzySystemFile(rootName, infLevel);
				out.println("\n storage option set: file [root name="
						+ rootName + "]");
				if (infLevel == InfLevel.ALL_INF.ordinal())
					out.println(", inference level: all inference rule");
				else if (infLevel == InfLevel.NO_INF.ordinal())
					out.println(", inference level: no inference");
				else
					throw new Error();
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		} else if (argument == MISSING_ARGUMENT) {
			out.println("\nmissing argument for command");
			out.flush();
		} else if (argument == ARGUMENT_NOT_VALID) {
			out.println("\nstorage option not valid");
			out.flush();
		}
	}

	private void execute_query() {
		if (fuzzySystem == null) {
			out.println("\n WARNING: initialize the system first!!");
		} else {
			try {
				if (!(fuzzySystem instanceof FuzzySystemMonetdb))
					out
							.println("\n WARNING: cannot submit query in a non database mode");
				ResultSet res = fuzzySystem.submitQuery(in);
				SQLUtils.printAll(res, out);
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
	}

	private void execute_rewrite() {
		if (fuzzySystem == null) {
			out.println("\n WARNING: initialize the system first!!");
		} else {
			if (parser.hasMoreTokens()) {
				String inputFileName = parser.nextToken().trim();
				try {
					long start = System.currentTimeMillis();
					AbstractFuzzySystem.rewrite(inputFileName, "");
					out.println("\ngraph rewrited in "
							+ (System.currentTimeMillis() - start)
							+ " milliseconds");
				} catch (FileNotFoundException e) {
					out.println("\nWARNING: unable to find file: \n\t"
							+ inputFileName);
				}
			} else {
				out.println("\nSYNTAX ERROR: missing argument ");
			}
		}
	}

	private void execute_setTNorm() {
		if (fuzzySystem == null) {
			out.println("\n WARNING: initialize the system first!!");
		} else {
			int argument = parseArgument(CommandCode.SET_TNORM.ordinal());
			if (argument == TnormArgumentCode.LIST.ordinal()) {
				out.println("\ntnorm list follows");
				for (String targ : tnormArgumentArray)
					out.print(" " + targ);
				out.println("\ntnorm list ends");
			} else if (argument >= 0 && argument < tnormArgumentArray.length) {
				fuzzySystem
						.setTnorm(GenericTnormFunctor.predefinedTnormArray[argument]);
				out.println("\ntnorm " + tnormArgumentArray[argument]
						+ " set correctly");
			} else {
				out.println("\ntnorm not valid");
				out.flush();
			}
		}
	}

	private void execute_showModelContent() {
		if (fuzzySystem == null) {
			out.println("\n WARNING: initialize the system first!!");
		} else {
			try {
				if (parser.hasMoreTokens()) {
					String fileName = parser.nextToken();
					try {
						PrintWriter fileOut = new PrintWriter(
								new FileOutputStream(fileName));
						fuzzySystem.write(fileOut, "N3");
						fileOut.close();
					} catch (IOException e) {
						out
								.println("\nunable to write model content on file: \""
										+ fileName + "\"");
					}
				} else {
					out.println("\nTHE WHOLE MODEL CONTENT FOLLOWS:\n");
					fuzzySystem.write(out, "N3");
					out.println("\nEND OF MODEL CONTENT\n");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void execute_showModelContentFromModel() {
		if (fuzzySystem == null) {
			out.println("\n WARNING: initialize the system first!!");
			return;
		}
		try {
			if (parser.hasMoreTokens()) {
				String fileName = parser.nextToken();
				try {
					PrintWriter fileOut = new PrintWriter(new FileOutputStream(
							fileName));
					fuzzySystem.writeFromModel(fileOut, "N3");
					fileOut.close();
				} catch (IOException e) {
					out.println("\nunable to write model content on file: \""
							+ fileName + "\"");
				}
			} else {
				out.println("\nTHE WHOLE MODEL CONTENT FOLLOWS:\n");
				fuzzySystem.write(out, "N3");
				out.println("\nEND OF MODEL CONTENT\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void fetch() {
		do {
			try {
				prompt();
				inputLine = in.readLine();
				if (inputLine != null)
					inputLine = inputLine.trim();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} while (inputLine == null || inputLine.equals(""));
		parser = new StringTokenizer(inputLine);
	}

	private int parseArgument(int commandType) {
		if (!parser.hasMoreTokens())
			return MISSING_ARGUMENT;
		String argument = parser.nextToken();
		if (argument == null)
			return MISSING_ARGUMENT;
		argument = argument.trim();

		if (commandType == CommandCode.INIT.ordinal()) {
			for (int i = 0; i < initStorageOptionArgumentArray.length; i++)
				if (argument.equals(initStorageOptionArgumentArray[i]))
					return i;
			return ARGUMENT_NOT_VALID;
		}
		if (commandType == CommandCode.SET_TNORM.ordinal()) {
			for (int i = 0; i < tnormArgumentArray.length; i++)
				if (argument.equals(tnormArgumentArray[i]))
					return i;
			return ARGUMENT_NOT_VALID;
		}
		return 0;
	}

	private int parseCommand() {
		if (!parser.hasMoreTokens())
			return CommandCode.EMPTY_COMMAND.ordinal();
		String command = parser.nextToken().trim();
		// TODO rimpiazzare con una binary search????? oppure non conviene?
		for (int i = 0; i < commandArray.length; i++) {
			if (command.equals(commandArray[i]))
				return i;
		}
		return CommandCode.COMMAND_NOT_FOUND.ordinal();
	}

	private void prompt() {
		out.print("\nfuzzy:$ ");
		out.flush();
	}

	public void run() {
		while (true) {
			fetch();
			int command = parseCommand();
			if (printCommandMode) {
				out.println(inputLine);
				out.flush();
			}
			if (command == CommandCode.INIT.ordinal()) {
				execute_init();
			} else if (command == CommandCode.SHOW_FROM_MODEL.ordinal()) {
				execute_showModelContentFromModel();
			} else if (command == CommandCode.QUERY.ordinal()) {
				execute_query();
			} else if (command == CommandCode.ADD_GRAPH.ordinal()) {
				execute_add();
			} else if (command == CommandCode.EXIT.ordinal()) {
				execute_exit();
				return;
			} else if (command == CommandCode.SHOW_MODEL_CONTENT.ordinal()) {
				execute_showModelContent();
			} else if (command == CommandCode.HELP.ordinal()) {
				execute_help();
			} else if (command == CommandCode.COMMAND_NOT_FOUND.ordinal()) {
				execute_commandNoFound();
			} else if (command == CommandCode.CURRENT_DIRECTORY.ordinal()) {
				execute_currentDirectory();
			} else if (command == CommandCode.CLEAR.ordinal()) {
				execute_clear();
			} else if (command == CommandCode.SET_TNORM.ordinal()) {
				execute_setTNorm();
			} else if (command == CommandCode.REWRITE.ordinal()) {
				execute_rewrite();
			} else
				throw new Error();
		}
	}
}
