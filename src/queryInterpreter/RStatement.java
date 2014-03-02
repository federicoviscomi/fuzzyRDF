package queryInterpreter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import queryInterpreter.LexicalAnalizer.TokenTypes;

public class RStatement {
	private static final String UNRESOLVED = "???";

	Map<String, String> retrieveVariables;

	ArrayList<ParsedWhereStatement> whereConditionList;

	private int limit = -1;

	private ArrayList<String> groupbyVariableList;

	public RStatement() {
		whereConditionList = new ArrayList<ParsedWhereStatement>();
		groupbyVariableList = new ArrayList<String>();
		retrieveVariables = new TreeMap<String, String>();
	}

	public void print(PrintStream out) {
		out.print("RETREIVE (");
		for (String key : retrieveVariables.keySet())
			out.print(" " + key);
		out.println(" )");
		out.print("WHERE (");
		for (ParsedWhereStatement stm : whereConditionList)
			out.print(stm);
		out.println(")");
		out.print("GROUPBY (");
		out.println(")");
		out.print("ORDERBY (TODO");
		for (String key : groupbyVariableList)
			out.print(" " + key);
		out.println(")");
		// if (limit != -1)
		out.println("LIMIT (" + limit + ")");
	}

	public void printResolved(PrintStream out) {
		out.print("RETREIVE (");
		for (String key : retrieveVariables.values())
			out.print(" " + key);
		out.println(" )");
		out.print("WHERE (");
		for (ParsedWhereStatement stm : whereConditionList) {
			Token subject = stm.subject;
			if (stm.subject.item2.ordinal() == TokenTypes.VARIABLE.ordinal())
				subject = new Token(retrieveVariables.get(stm.subject.item1),
						stm.subject.item2);
			Token predicate = stm.predicate;
			if (stm.predicate.item2.ordinal() == TokenTypes.VARIABLE.ordinal())
				predicate = new Token(retrieveVariables
						.get(stm.predicate.item1), stm.predicate.item2);
			Token object = stm.object;
			if (stm.object.item2.ordinal() == TokenTypes.VARIABLE.ordinal())
				object = new Token(retrieveVariables.get(stm.object.item1),
						stm.object.item2);
			Token degree = stm.degree;
			if (stm.degree.item2.ordinal() == TokenTypes.VARIABLE.ordinal())
				degree = new Token(retrieveVariables.get(stm.degree.item1),
						stm.degree.item2);
			ParsedWhereStatement resolved = new ParsedWhereStatement(subject,
					predicate, object, degree);
			out.print(resolved);
		}
		out.println(")");
		out.print("GROUPBY (");
		out.println(")");
		out.print("ORDERBY (TODO");
		for (String gbvar : groupbyVariableList)
			out.print(" " + gbvar);
		out.println(")");
		out.println("LIMIT (" + limit + ")");
	}

	public static boolean isUnresolved(String resolved) {
		return resolved.equals(UNRESOLVED);
	}

	public void setlimit(int limit) {
		this.limit = limit;
	}

	public void addGroupByVariable(String variable) {
		groupbyVariableList.add(variable);
	}

	public void addRetreiveVar(String variable) {
		retrieveVariables.put(variable, UNRESOLVED);
	}

	public void addWhereCondition(ParsedWhereStatement parsedWhereStatement) {
		whereConditionList.add(parsedWhereStatement);
	}

	public boolean unresolved() {
		return retrieveVariables.containsValue(UNRESOLVED);
	}

	public Map<String, String> getUnresolved() {
		Map<String, String> unresolved = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : retrieveVariables.entrySet())
			if (entry.getValue().equals(UNRESOLVED))
				unresolved.put(entry.getKey(), entry.getValue());
		return unresolved;
	}

	public int getLimit() {
		return limit;
	}
}
