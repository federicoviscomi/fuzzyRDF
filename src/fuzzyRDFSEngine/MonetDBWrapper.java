package fuzzyRDFSEngine;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import fuzzyRDFSEngine.DebugInfo.DebugLevels;

/**
 * mumble
 * 
 * come fare se un certo statement e' soggetto di piu' altri statement
 * 
 * ci possono essere due statement inferiti uguali che fare??????
 * 
 * 
 */
/**
 * Memorizza un grafo RDF secondo lo schema:
 * 
 * per ogni predicato c'e' una tabella che ha i campi (soggetto, oggetto,
 * grado).
 * 
 * 
 * @author feffocarta
 * 
 */
public class MonetDBWrapper {

	private Connection connection;

	private StatementStore stmstr;

	private Statement sqlStatement;

	public NameMap predicateToTableNameMap;

	public MonetDBWrapper(String databaseName, String user, String password)
			throws ClassNotFoundException, SQLException {
		this(databaseName, user, password, "//localhost/");
	}

	public MonetDBWrapper(String databaseName, String user, String password,
			String databaseLocation) throws ClassNotFoundException,
			SQLException {
		// make sure the driver is loaded
		Class.forName("nl.cwi.monetdb.jdbc.MonetDriver");
		connection = DriverManager.getConnection("jdbc:monetdb:"
				+ databaseLocation + databaseName, user, password);
		sqlStatement = connection.createStatement();
		predicateToTableNameMap = new NameMap();
		stmstr = new StatementStore();
	}

	public MonetDBWrapper() throws ClassNotFoundException, SQLException {
		this("database", "monetdb", "monetdb");
	}

	void addAll(GenericModel model) throws SQLException {
		// list the statements in the graph
		StmtIterator statementsList = model.listStatements();

		while (statementsList.hasNext()) {
			// get next statement
			com.hp.hpl.jena.rdf.model.Statement rdfStatement = statementsList
					.nextStatement();
			if (isPartOfAReification(rdfStatement)) {
				if (DebugInfo
						.isDebugLevelSet(DebugLevels.MONETDBWRAPPER_ADDALL))
					System.err.println("Statement " + rdfStatement
							+ " is a part of a reification");
				stmstr.addStmPartOfAReification(rdfStatement);
				Resource subject = rdfStatement.getSubject();
				if (stmstr.containsWholeReification(subject)) {
					addToDatabaseFromAReification(subject);
				}
			} else {
				if (DebugInfo
						.isDebugLevelSet(DebugLevels.MONETDBWRAPPER_ADDALL))
					System.err.println("Statement " + rdfStatement
							+ " is NOT a part of a reification");
				addANotReifiedStatementToDatabase(rdfStatement);
			}
		}

	}

	private void addANotReifiedStatementToDatabase(
			com.hp.hpl.jena.rdf.model.Statement rdfStatement)
			throws SQLException {
		String subjectName = rdfStatement.getSubject().getURI();
		String objectName = rdfStatement.getObject().toString();
		String predicateName = rdfStatement.getPredicate().getLocalName();
		predicateName = predicateToTableNameMap.get(predicateName);
		if (!tableExist(predicateName)) {
			createDefaultTable(predicateName);
		}

		if (DebugInfo.isDebugLevelSet(DebugInfo.DebugLevels.DATABASE)) {
			System.err.println("INSERT INTO " + predicateName + " VALUES ("
					+ subjectName + ", " + objectName + "', 1.0)");
		}
		sqlStatement.executeUpdate("INSERT INTO " + predicateName
				+ " VALUES ('" + subjectName + "', '" + objectName + "', 1.0)");
	}

	public static boolean isPartOfAReification(
			com.hp.hpl.jena.rdf.model.Statement rdfStatement) {
		Property predicate = rdfStatement.getPredicate();
		RDFNode object = rdfStatement.getObject();
		if (predicate.equals(RDF.subject) || predicate.equals(RDF.object))
			return true;
		if (predicate.equals(RDF.type)) {
			if (!object.equals(RDF.Statement.as(RDFNode.class))) {
				return false;
			}
			return true;
		} else if (predicate.equals(RDF.predicate)) {
			if (object.canAs(Property.class)) {
				return true;
			}
			return false;
		} else if (predicate.equals(FuzzySystem.degree)) {
			if (object.canAs(Literal.class)) {
				try {
					((Literal) object.as(Literal.class)).getDouble();
				} catch (NumberFormatException e) {
					return false;
				}
				return true;
			}
			return false;
		}
		return false;
	}

	private void addToDatabaseFromAReification(Resource subject)
			throws SQLException {
		String predicateName = stmstr.getPredicate(subject).getLocalName();
		predicateName = predicateToTableNameMap.get(predicateName);
		if (!tableExist(predicateName)) {
			createDefaultTable(predicateName);
		}
		String subjectName = stmstr.getSubject(subject);
		String objectName = stmstr.getObject(subject);
		String degree = stmstr.getDegree(subject) + "";
		if (DebugInfo.isDebugLevelSet(DebugInfo.DebugLevels.DATABASE)) {
			System.err.println("INSERT INTO " + predicateName + " VALUES ("
					+ subjectName + ", " + objectName + "', " + degree + ")");
		}
		sqlStatement.executeUpdate("INSERT INTO " + predicateName
				+ " VALUES ('" + subjectName + "', '" + objectName + "', "
				+ degree + ")");
		stmstr.remove(subject);
	}

	void clearDatabase() throws SQLException {
		ArrayList<String> userDefinedTablesNameList = this
				.getUserDefinedTablesNameList();
		for (String userDefinedTableName : userDefinedTablesNameList) {
			if (DebugInfo.isDebugLevelSet(DebugInfo.DebugLevels.DATABASE)) {
				System.err.println("DROP TABLE " + userDefinedTableName + ";");
			}
			sqlStatement.executeUpdate("DROP TABLE " + userDefinedTableName
					+ ";");
		}
	}

	void close() throws SQLException {
		sqlStatement.close();
		connection.close();
	}

	private void createDefaultTable(String predicateName) throws SQLException {
		if (DebugInfo.isDebugLevelSet(DebugInfo.DebugLevels.DATABASE)) {
			System.err.println("CREATE TABLE " + predicateName
					+ " (subject VARCHAR(128), "
					+ "object VARCHAR(128), degree DOUBLE) ");
		}
		sqlStatement.executeUpdate("CREATE TABLE " + predicateName
				+ " (subject VARCHAR(64), "
				+ "object VARCHAR(64), degree DOUBLE) ");

	}

	private ArrayList<String> getUserDefinedTablesNameList()
			throws SQLException {
		ArrayList<String> userDefinedTablesNameList = null;

		userDefinedTablesNameList = new ArrayList<String>();
		if (DebugInfo.isDebugLevelSet(DebugInfo.DebugLevels.DATABASE)) {
			System.err.println("SELECT * " + "FROM tables "
					+ "WHERE tables.system = false;");
		}
		ResultSet result = sqlStatement.executeQuery("SELECT * "
				+ "FROM tables " + "WHERE tables.system = false;");
		while (result.next()) {
			String userDefinedTableName = result.getString("name");
			userDefinedTablesNameList.add(userDefinedTableName);
		}

		return userDefinedTablesNameList;
	}

	void printAllTablesName() throws SQLException {
		if (DebugInfo.isDebugLevelSet(DebugInfo.DebugLevels.DATABASE)) {
			System.err.println("SELECT name FROM tables");
		}
		ResultSet allTablesName = sqlStatement
				.executeQuery("SELECT name FROM tables");
		ResultSetMetaData metaData = allTablesName.getMetaData();
		int columnCount = metaData.getColumnCount();
		while (allTablesName.next()) {
			System.out.println();
			for (int i = 1; i <= columnCount; i++) {
				System.out.print(metaData.getColumnName(i) + "::"
						+ metaData.getColumnTypeName(i) + "::"
						+ allTablesName.getObject(i) + "##");
			}
		}
		System.out.println();
	}

	private boolean tableExist(String predicateName) throws SQLException {
		if (DebugInfo.isDebugLevelSet(DebugInfo.DebugLevels.DATABASE)) {
			System.err.println("SELECT name " + "FROM tables "
					+ "WHERE tables.name='" + predicateName + "';");
		}
		ResultSet resultSet = sqlStatement
				.executeQuery("SELECT name " + "FROM tables "
						+ "WHERE tables.name='" + predicateName + "';");
		return resultSet.next();
	}

	void write(PrintWriter out) throws SQLException {
		if (DebugInfo.isDebugLevelSet(DebugInfo.DebugLevels.DATABASE)) {
			System.err.println("SELECT name " + "FROM tables "
					+ "WHERE tables.system = false;");
		}
		ResultSet result = sqlStatement.executeQuery("SELECT name "
				+ "FROM tables " + "WHERE tables.system = false;");

		ArrayList<String> userDefinedTablesNameList = this
				.getUserDefinedTablesNameList();
		for (String userDefinedTableName : userDefinedTablesNameList) {
			if (DebugInfo.isDebugLevelSet(DebugInfo.DebugLevels.DATABASE)) {
				System.err.println("SELECT * " + "FROM " + userDefinedTableName
						+ ";");
			}
			ResultSet tableContent = sqlStatement.executeQuery("SELECT * "
					+ "FROM " + userDefinedTableName + ";");
			SQLUtils.printAll(tableContent, out);
		}
	}

	public ArrayList<String> getRelations() throws SQLException {
		if (DebugInfo.isDebugLevelSet(DebugInfo.DebugLevels.DATABASE)) {
			System.err
					.println("SELECT name FROM tables WHERE tables.system = false");
		}
		ResultSet allTablesName = sqlStatement
				.executeQuery("SELECT name FROM tables WHERE tables.system = false");
		ArrayList<String> relations = new ArrayList<String>();
		while (allTablesName.next())
			relations.add(allTablesName.getString(1));
		return relations;
	}

	public ResultSet submitQuery(String string) throws SQLException {
		ResultSet res = sqlStatement.executeQuery(string);
		return res;
	}

	public void printAllUserTablesName() throws SQLException {
		if (DebugInfo.isDebugLevelSet(DebugInfo.DebugLevels.DATABASE)) {
			System.err
					.println("SELECT name FROM tables WHERE tables.system = false");
		}
		ResultSet allTablesName = sqlStatement
				.executeQuery("SELECT name FROM tables WHERE tables.system = false");
		ResultSetMetaData metaData = allTablesName.getMetaData();
		int columnCount = metaData.getColumnCount();
		while (allTablesName.next()) {
			System.out.println();
			for (int i = 1; i <= columnCount; i++) {
				System.out.print(metaData.getColumnName(i) + "::"
						+ metaData.getColumnTypeName(i) + "::"
						+ allTablesName.getObject(i) + "##");
			}
		}
		System.out.println();
	}
}
