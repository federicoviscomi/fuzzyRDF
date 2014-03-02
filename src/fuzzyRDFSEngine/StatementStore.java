package fuzzyRDFSEngine;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

class StatementStore {

	private final Thread shutDownHook = new Thread() {

		@Override
		public void run() {
			if (DebugInfo
					.isDebugLevelSet(DebugInfo.DebugLevels.INTERNALSTATEMENTSTORE)) {
				defaultOutput.close();
			}
			fileOut.flush();
			fileOut.close();
		}
	};

	private Map<Resource, String> subjectNullMap;

	private Map<String, String> subjectUriMap;

	private Map<Resource, String> objectNullMap;

	private Map<String, String> objectUriMap;

	private Map<Resource, Property> predicateNullMap;

	private Map<String, Property> predicateUriMap;

	private Map<Resource, Double> degreeNullMap;

	private Map<String, Double> degreeUriMap;

	private Map<Resource, String> typeNullMap;

	private Map<String, String> typeUriMap;

	private Map<Resource, ArrayList<Statement>> statementNullMap;

	private Map<String, ArrayList<Statement>> statementUriMap;

	private PrintWriter defaultOutput;

	private ArrayList<Statement> statementToRemove;

	private PrintWriter fileOut;

	StatementStore() {
		subjectNullMap = new TreeMap<Resource, String>(
				ResourceComparator.resourceComparator);
		subjectUriMap = new TreeMap<String, String>();
		objectNullMap = new TreeMap<Resource, String>(
				ResourceComparator.resourceComparator);
		objectUriMap = new TreeMap<String, String>();
		predicateNullMap = new TreeMap<Resource, Property>(
				ResourceComparator.resourceComparator);
		predicateUriMap = new TreeMap<String, Property>();
		degreeNullMap = new TreeMap<Resource, Double>(
				ResourceComparator.resourceComparator);
		degreeUriMap = new TreeMap<String, Double>();
		typeNullMap = new TreeMap<Resource, String>(
				ResourceComparator.resourceComparator);
		typeUriMap = new TreeMap<String, String>();
		statementNullMap = new TreeMap<Resource, ArrayList<Statement>>(
				ResourceComparator.resourceComparator);
		statementUriMap = new TreeMap<String, ArrayList<Statement>>();
		if (DebugInfo
				.isDebugLevelSet(DebugInfo.DebugLevels.INTERNALSTATEMENTSTORE)) {
			try {
				defaultOutput = new PrintWriter(new FileOutputStream(
						"internalState"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				
			}
		}
		try {
			fileOut = new PrintWriter(new FileOutputStream("res"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		statementToRemove = new ArrayList<Statement>();
		Runtime.getRuntime().addShutdownHook(this.shutDownHook);
	}

	void addStmPartOfAReification(Statement rdfStatement) {
		// get the subject
		Resource subject = rdfStatement.getSubject();
		// get the predicate
		Property predicate = rdfStatement.getPredicate();
		// get the object
		RDFNode object = rdfStatement.getObject();
		if (predicate.equals(RDF.type)) {
			// System.out.println("predicate.equals(RDF.type) " +
			// predicate);
			if (!object.equals(RDF.Statement)) {
				throw new RuntimeException(
						"statement is not part of a reification" + rdfStatement);
			}
			// non e' necessario. basta un flag
			if (subject.getURI() == null) {
				typeNullMap.put(subject, null);
			} else {
				typeUriMap.put(subject.getURI(), null);
			}
		} else if (predicate.equals(RDF.subject)) {
			// System.out.println("predicate.equals(RDF.subject) " +
			// predicate);
			if (subject.getURI() == null) {
				subjectNullMap.put(subject, ((Resource) object).getURI());
			} else {
				subjectUriMap.put(subject.getURI(), ((Resource) object)
						.getURI());
			}
		} else if (predicate.equals(RDF.object)) {
			// System.out.println("predicate.equals(RDF.object) " +
			// predicate);
			if (subject.getURI() == null) {
				objectNullMap.put(subject, object.toString());
			} else {
				objectUriMap.put(subject.getURI(), object.toString());
			}
		} else if (predicate.equals(RDF.predicate)) {
			if (object.canAs(Property.class)) {
				if (subject.getURI() == null) {
					predicateNullMap.put(subject, (Property) object
							.as(Property.class));
				} else {
					predicateUriMap.put(subject.getURI(), (Property) object
							.as(Property.class));
				}
			} else {
				throw new RuntimeException();
			}
		} else if (predicate.equals(FuzzySystem.degree)) {
			if (object.canAs(Literal.class)) {
				Double degree = ((Literal) object.as(Literal.class))
						.getDouble();
				if (subject.getURI() == null) {
					degreeNullMap.put(subject, degree);
				} else {
					degreeUriMap.put(subject.getURI(), degree);
				}
			} else {
				throw new RuntimeException();
			}
		} else {
			throw new RuntimeException(
					" predicate not valid. allowed only: type, subject, object, predicate, degree.\n"
							+ predicate + "\n " + FuzzySystem.degree);
		}
		if (subject.getURI() == null) {
			ArrayList<Statement> reification;
			if ((reification = statementNullMap.get(subject)) == null) {
				reification = new ArrayList<Statement>(5);
				statementNullMap.put(subject, reification);
			}
			reification.add(rdfStatement);
		} else {
			ArrayList<Statement> reification;
			if ((reification = statementUriMap.get(subject.getURI())) == null) {
				reification = new ArrayList<Statement>(5);
				statementUriMap.put(subject.getURI(), reification);
			}
			reification.add(rdfStatement);
		}
	}

	boolean containsWholeReification(Resource subject) {
		boolean con;
		if (subject.getURI() == null) {
			con = (typeNullMap.containsKey(subject)
					&& subjectNullMap.containsKey(subject)
					&& objectNullMap.containsKey(subject)
					&& predicateNullMap.containsKey(subject) && degreeNullMap
					.containsKey(subject));
		} else {
			con = (typeUriMap.containsKey(subject.getURI())
					&& subjectUriMap.containsKey(subject.getURI())
					&& objectUriMap.containsKey(subject.getURI())
					&& predicateUriMap.containsKey(subject.getURI()) && degreeUriMap
					.containsKey(subject.getURI()));
		}
		return con;
	}

	Double getDegree(Resource subject) {
		if (subject.getURI() == null) {
			return degreeNullMap.get(subject);
		}
		return degreeUriMap.get(subject.getURI());
	}

	String getObject(Resource subject) {
		if (subject.getURI() == null) {
			return objectNullMap.get(subject);
		}
		return objectUriMap.get(subject.getURI());
	}

	Property getPredicate(Resource subject) {
		if (subject.getURI() == null) {
			return predicateNullMap.get(subject);
		}
		return predicateUriMap.get(subject.getURI());
	}

	private ArrayList<Statement> getStatement(Resource key) {
		if (key.getURI() == null) {
			return statementNullMap.get(key);
		}
		return statementUriMap.get(key.getURI());
	}

	public ArrayList<Statement> getStatementToRemove() {
		return statementToRemove;
	}

	String getSubject(Resource subject) {
		if (subject.getURI() == null) {
			return subjectNullMap.get(subject);
		}
		return subjectUriMap.get(subject.getURI());
	}

	private void printInternalState(PrintWriter out) {
		out.println("*********************************************");
		out.println("subjectNullMap;");
		for (Map.Entry<Resource, String> entry : subjectNullMap.entrySet()) {
			out.println(entry.getKey() + "::" + entry.getValue());
		}
		out.println("subjectUriMap;");
		for (Entry<String, String> entry : subjectUriMap.entrySet()) {
			out.println(entry.getKey() + "::" + entry.getValue());
		}
		out.println("\nobjectNullMap;");
		for (Map.Entry<Resource, String> entry : objectNullMap.entrySet()) {
			out.println(entry.getKey() + "::" + entry.getValue());
		}
		out.println("objectUriMap;");
		for (Entry<String, String> entry : objectUriMap.entrySet()) {
			out.println(entry.getKey() + "::" + entry.getValue());
		}
		out.println("\npredicateNullMap;");
		for (Entry<Resource, Property> entry : predicateNullMap.entrySet()) {
			out.println(entry.getKey() + "::" + entry.getValue());
		}
		out.println("predicateUriMap;");
		for (Entry<String, Property> entry : predicateUriMap.entrySet()) {
			out.println(entry.getKey() + "::" + entry.getValue());
		}
		out.println("\ndegreeNullMap;");
		for (Entry<Resource, Double> entry : degreeNullMap.entrySet()) {
			out.println(entry.getKey() + "::" + entry.getValue());
		}
		out.println("degreeUriMap;");
		for (Entry<String, Double> entry : degreeUriMap.entrySet()) {
			out.println(entry.getKey() + "::" + entry.getValue());
		}
		out.println("\ntypeNullMap;");
		for (Map.Entry<Resource, String> entry : typeNullMap.entrySet()) {
			out.println(entry.getKey() + "::" + entry.getValue());
		}
		out.println("typeUriMap;");
		for (Entry<String, String> entry : typeUriMap.entrySet()) {
			out.println(entry.getKey() + "::" + entry.getValue());
		}
		out.println("\nstatementNullMap;");
		for (Map.Entry<Resource, ArrayList<Statement>> entry : statementNullMap
				.entrySet()) {
			ArrayList<Statement> value = entry.getValue();
			out.print(entry.getKey() + "::");
			for (Statement s : value)
				out.print(s + " ");
			out.println();
		}
		out.println("statementUriMap;");
		for (Entry<String, ArrayList<Statement>> entry : statementUriMap
				.entrySet()) {
			ArrayList<Statement> value = entry.getValue();
			out.print(entry.getKey() + "::");
			for (Statement s : value)
				out.print(s + " ");
			out.println();
		}
		out.println("*********************************************");
		out.flush();
	}

	void remove(Resource subject) {
		if (subject.getURI() == null) {
			typeNullMap.remove(subject);
			subjectNullMap.remove(subject);
			objectNullMap.remove(subject);
			predicateNullMap.remove(subject);
			degreeNullMap.remove(subject);
			statementNullMap.remove(subject);
		} else {
			typeUriMap.remove(subject.getURI());
			subjectUriMap.remove(subject.getURI());
			objectUriMap.remove(subject.getURI());
			predicateUriMap.remove(subject.getURI());
			degreeUriMap.remove(subject.getURI());
			statementUriMap.remove(subject.getURI());
		}
	}

	/**
	 * 2 ipotesi
	 * 
	 * Assume che lo statement reificato con soggetto <param>key</param> sia
	 * presente nello store e che ci sia al piu' un altro statement reificato
	 * uguale tranne che eventualmente il grado. Per dirlo in altre parole e'
	 * meglio operare un distinguo tra la reificazione di uno statement e tale
	 * statement reificato.
	 * 
	 * @param subject
	 */
	public void updateToRemoveList(Resource key) {
		Set<Resource> allInferredKey = subjectNullMap.keySet();
		for (Resource innerKey : allInferredKey) {
			if (!innerKey.equals(key) && containsWholeReification(innerKey)
					&& getSubject(key).equals(getSubject(innerKey))
					&& getPredicate(key).equals(getPredicate(innerKey))
					&& getObject(key).equals(getObject(innerKey))) {
				if (getDegree(key) <= getDegree(innerKey)) {
					statementToRemove.addAll(getStatement(key));
					System.err.println("found duplicate " + key + " "
							+ innerKey);
					remove(key);
				} else {
					statementToRemove.addAll(statementNullMap.get(innerKey));
					System.err.println("found duplicate \n" + key + "\n"
							+ innerKey);
					remove(innerKey);
				}

				return;
			}
		}
		Set<String> allReadKey = subjectUriMap.keySet();
		for (String innerKey : allReadKey) {
			if (!innerKey.equals(key.getURI())
					&& getSubject(key).equals(subjectUriMap.get(innerKey))
					&& getPredicate(key).equals(predicateUriMap.get(innerKey))
					&& getObject(key).equals(objectUriMap.get(innerKey))
					&& degreeUriMap.get(innerKey) != null) {
				if (getDegree(key) <= degreeUriMap.get(innerKey)) {
					this.printInternalState(fileOut);
					fileOut.close();
					System.err.println("found duplicate \n" + key + "\n"
							+ innerKey);
					try {
						statementToRemove.addAll(getStatement(key));
					} catch (NullPointerException e) {
						fileOut.flush();
						fileOut.close();
						throw e;
					}
					remove(key);
				} else {
					System.err.println("found duplicate \n" + key + "\n"
							+ innerKey);
					statementToRemove.addAll(statementUriMap.get(innerKey));
					subjectUriMap.remove(innerKey);
					objectUriMap.remove(innerKey);
					predicateUriMap.remove(innerKey);
					degreeUriMap.remove(innerKey);
					statementUriMap.remove(innerKey);
				}
				return;
			}
		}
	}
}
