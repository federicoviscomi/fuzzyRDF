package queryInterpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import queryInterpreter.LexicalAnalizer.TokenTypes;

import fuzzyRDFSEngine.MonetDBWrapper;
import fuzzyRDFSEngine.NameMap;

public class TargetCodeGenerator {
	private final CodeOptimizer co;
	private final PrintWriter out;
	private final MonetDBWrapper db;
	private String fileName;
	private final NameMap predicateToTableNameMap;
	private static final Comparator<String> stringComparator = new Comparator<String>() {
		@Override
		public int compare(String arg0, String arg1) {
			return arg0.compareTo(arg1);
		}
	};

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, SQLException, LexicalErrorException,
			SyntacticErrorException {
		test();
	}

	private static void test() throws IOException, ClassNotFoundException,
			SQLException, LexicalErrorException, SyntacticErrorException {
		TargetCodeGenerator tcg = new TargetCodeGenerator("file1",
				new MonetDBWrapper(), new NameMap());
		while (tcg.generateNextStatement()) {

		}
		BufferedReader in = new BufferedReader(new FileReader(tcg.fileName
				+ ".sql"));
		String nextLine;
		while ((nextLine = in.readLine()) != null)
			System.out.println(nextLine);
	}

	public TargetCodeGenerator(String fileName, MonetDBWrapper db,
			NameMap predicateToTableNameMap) throws IOException {
		this.fileName = fileName;
		this.db = db;
		this.predicateToTableNameMap = predicateToTableNameMap;
		co = new CodeOptimizer(fileName, predicateToTableNameMap);
		out = new PrintWriter(new FileWriter(fileName + ".sql"));
	}

	public TargetCodeGenerator(BufferedReader in, MonetDBWrapper db, File file,
			NameMap predicateToTableNameMap) throws IOException {
		this.db = db;
		this.predicateToTableNameMap = predicateToTableNameMap;
		co = new CodeOptimizer(in, predicateToTableNameMap);
		out = new PrintWriter(new FileWriter(file));
	}

	public boolean generateNextStatement() throws LexicalErrorException,
			SyntacticErrorException, SQLException, IOException {
		RStatement nextStatement = co.optimizeNextStatement();
		if (nextStatement == null) {
			out.close();
			return false;
		}
		write(nextStatement);
		out.flush();
		return true;
	}

	private void write(RStatement nextStatement) throws SQLException,
			SyntacticErrorException {
		if (nextStatement.unresolved()) {
			ArrayList<String> relations = db.getRelations();
			for (String rel : relations)
				System.out.println(rel);
			System.out.flush();
			if (relations.size() == 0)
				throw new Error();
			// TODO eliminare unresolved
			Map<String, String> unresolved = nextStatement.getUnresolved();
			int index = 0;
			String[] unresolvedOrdered = new String[unresolved.size()];
			for (String s : unresolved.keySet()) {
				unresolvedOrdered[index] = s;
				index++;
			}
			java.util.Arrays.sort(unresolvedOrdered, stringComparator);
			int[] indexes = new int[unresolved.size()];
			for (int i = 0; i < indexes.length; i++)
				indexes[i] = 0;
			for (int i = 0; i < Math.pow(relations.size(), unresolved.size()); i++) {
				writeASingleStatement(indexes, relations, unresolved,
						nextStatement, unresolvedOrdered);
				indexes[indexes.length - 1]++;
				int riporto = 0;
				for (int j = indexes.length - 1; j >= 0; j--) {
					indexes[j] += riporto;
					if (indexes[j] == unresolved.size()) {
						indexes[j] = 0;
						riporto = 1;
					} else
						riporto = 0;
				}

			}
		} else {
			writeASingleStatement(nextStatement);
		}
	}

	private void writeASingleStatement(int[] indexes,
			ArrayList<String> relations, Map<String, String> unresolved,
			RStatement nextStatement, String[] unresolvedOrdered)
			throws SQLException, SyntacticErrorException {
		for (int i = 0; i < indexes.length; i++) {
			nextStatement.retrieveVariables.put(unresolvedOrdered[i], relations
					.get(indexes[i]));
		}
		resolve(nextStatement, unresolvedOrdered);
		writeASingleStatement(nextStatement);
	}

	private void resolve(RStatement nextStatement, String[] unresolved)
			throws SyntacticErrorException {
		Iterator<ParsedWhereStatement> iterator = nextStatement.whereConditionList
				.iterator();
		while (iterator.hasNext()) {
			ParsedWhereStatement pws = iterator.next();
			Token subject = pws.subject;
			Token predicate = pws.predicate;
			Token object = pws.object;
			Token degree = pws.degree;
			if (subject.item2.ordinal() == TokenTypes.VARIABLE.ordinal()) {
				String resolved = nextStatement.retrieveVariables
						.get(subject.item1);
				if (resolved == null)
					throw new SyntacticErrorException(
							"All variabels in WHERE part of query must appear in RETREIVE part too. Unable to resolve "
									+ subject.item1);
				if (contains(resolved, unresolved)) {
					nextStatement.retrieveVariables.put(subject.item1,
							predicate.item1 + ".subject");
				}
			}
			if (object.item2.ordinal() == TokenTypes.VARIABLE.ordinal()) {
				String resolved = nextStatement.retrieveVariables
						.get(object.item1);
				if (resolved == null)
					throw new SyntacticErrorException(
							"All variabels in WHERE part of query must appear in RETREIVE part too. Unable to resolve "
									+ object.item1);
				if (contains(resolved, unresolved)) {
					nextStatement.retrieveVariables.put(object.item1,
							predicate.item1 + ".object");
				}
			}
			if (degree.item2.ordinal() == TokenTypes.VARIABLE.ordinal()) {
				String resolved = nextStatement.retrieveVariables
						.get(degree.item1);
				if (resolved == null)
					throw new SyntacticErrorException(
							"All variabels in WHERE part of query must appear in RETREIVE part too. Unable to resolve "
									+ degree.item1);
				if (contains(resolved, unresolved)) {
					nextStatement.retrieveVariables.put(degree.item1,
							predicate.item1 + ".object");
				}
			}
		}
	}

	private boolean contains(String resolved, String[] unresolved) {
		for (String s : unresolved)
			if (resolved.equals(s))
				return true;
		return false;
	}

	/**
	 * 
	 * @param nextStatement
	 *            is a completely resolved statement
	 */
	private void writeASingleStatement(RStatement nextStatement) {
		out.print("SELECT ");
		int index = 0;
		for (String var : nextStatement.retrieveVariables.values()) {
			if (index < nextStatement.retrieveVariables.values().size() - 1)
				out.print(var + ", ");
			else
				out.print(var + " ");
			index++;
		}

		out.print("\nFROM ");

		for (int i = 0; i < nextStatement.whereConditionList.size(); i++) {
			ParsedWhereStatement ns = nextStatement.whereConditionList.get(i);
			String predicate;
			if (ns.predicate.item2.ordinal() == TokenTypes.VARIABLE.ordinal())
				predicate = nextStatement.retrieveVariables
						.get(ns.predicate.item1);
			else
				predicate = predicateToTableNameMap.get(ns.predicate.item1);
			out.print(predicate);
			if (i < nextStatement.whereConditionList.size() - 1)
				out.print(", ");
			else
				out.print(" ");
		}
		Iterator<ParsedWhereStatement> iterator = nextStatement.whereConditionList
				.iterator();
		while (iterator.hasNext()) {
			ParsedWhereStatement ns = iterator.next();
			String predicate;
			if (ns.predicate.item2.ordinal() == TokenTypes.VARIABLE.ordinal())
				predicate = nextStatement.retrieveVariables
						.get(ns.predicate.item1);
			else
				predicate = ns.predicate.item1;
			String subject;
			if (ns.subject.item2.ordinal() == TokenTypes.VARIABLE.ordinal())
				subject = nextStatement.retrieveVariables.get(ns.subject.item1);
			else
				subject = ns.subject.item1;
			String object;
			if (ns.object.item2.ordinal() == TokenTypes.VARIABLE.ordinal())
				object = nextStatement.retrieveVariables.get(ns.object.item1);
			else
				object = ns.object.item1;
			if (subject.equals(predicate + ".subject")
					&& object.equals(predicate + ".object")) {
				iterator.remove();
			}
		}
		if (nextStatement.whereConditionList.size() > 0) {
			out.print("\nWHERE ");
			ParsedWhereStatement ns;
			for (int i = 0; i < nextStatement.whereConditionList.size(); i++) {
				ns = nextStatement.whereConditionList.get(i);
				String predicate;
				if (ns.predicate.item2.ordinal() == TokenTypes.VARIABLE
						.ordinal())
					predicate = nextStatement.retrieveVariables
							.get(ns.predicate.item1);
				else
					predicate = ns.predicate.item1;
				String subject;
				if (ns.subject.item2.ordinal() == TokenTypes.VARIABLE.ordinal())
					subject = nextStatement.retrieveVariables
							.get(ns.subject.item1);
				else
					subject = ns.subject.item1;
				String object;
				if (ns.object.item2.ordinal() == TokenTypes.VARIABLE.ordinal())
					object = nextStatement.retrieveVariables
							.get(ns.object.item1);
				else
					object = ns.object.item1;
				if (subject.equals(predicate + ".subject")) {
					if (!object.equals(predicate + ".object")) {
						out
								.print(" " + predicate + ".object = " + object
										+ " ");
					}
				} else {
					out.print(predicate + ".subject = " + subject + " ");
					if (!object.equals(predicate + ".object")) {
						out.print(" AND " + predicate + ".object = " + object
								+ " ");
					}
				}
				if (i < nextStatement.whereConditionList.size() - 1)
					out.print(" AND ");
			}
		}
		if (nextStatement.getLimit() != -1) {
			out.print("\nLIMIT " + nextStatement.getLimit() + " ;");
		}
	}
}
