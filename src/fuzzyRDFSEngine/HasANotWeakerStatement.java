package fuzzyRDFSEngine;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.reasoner.rulesys.Builtin;
import com.hp.hpl.jena.reasoner.rulesys.RuleContext;
import com.hp.hpl.jena.util.iterator.ClosableIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import fuzzyRDFSEngine.DebugInfo.DebugLevels;

public class HasANotWeakerStatement implements Builtin {

	public static final Builtin builtin = new HasANotWeakerStatement(
			FuzzySystem.degree.getNameSpace());
	private final String nameSpace;

	public HasANotWeakerStatement(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	/**
	 * Return the n'th argument node after dererencing by what ever type of rule
	 * engine binding environment is appropriate.
	 */
	public Node getArg(int n, Node[] args, RuleContext context) {
		return context.getEnv().getGroundVersion(args[n]);
	}

	/**
	 * 
	 * argv = [soggetto, predicato, oggetto, grado]
	 */
	@Override
	public boolean bodyCall(Node[] argv, int argc, RuleContext context) {
		Node subject = getArg(0, argv, context);
		Node predicate = getArg(1, argv, context);
		Node object = getArg(2, argv, context);
		double degree = Double.parseDouble(getArg(3, argv, context)
				.getLiteralValue().toString());
		/**
		 * controlla che non ci sia uno statement non reificato (che ha
		 * necessariamente grado 1 e dunque e' non minore di ogni altro grado)
		 */
		if (context.find(subject, predicate, object).hasNext()) {
			if (DebugInfo.isDebugLevelSet(DebugLevels.BUILTIN2))
				System.err
						.println("\t the model has a non weaker statement (unreified): ");
			return false;
		}
		/** controlla se c'e' una reificazione uguale ma con grado non minore */
		ClosableIterator sameSubject = context.find(null, RDF.subject.asNode(),
				subject);
		if (DebugInfo.isDebugLevelSet(DebugLevels.BUILTIN2))
			System.err.println("\n " + subject + " " + predicate + " " + object
					+ " " + degree);
		while (sameSubject.hasNext()) {
			Triple subjectLoopStatement = (Triple) sameSubject.next();
			// System.err.println("\t" + subjectLoopStatement);
			Node id = subjectLoopStatement.getSubject();
			ClosableIterator sameId = context.find(id, null, null);
			boolean equals = true;
			Node innerDegree = null;
			while (sameId.hasNext()) {
				// System.err.println("\t\t" + sameId.next());
				Triple triple = (Triple) sameId.next();
				if (triple.getPredicate().equals(RDF.type.asNode())) {
					if (!triple.getObject().equals(RDF.Statement.asNode()))
						throw new Error(" implementation error: " + RDF.type
								+ " is supposed to be " + RDF.Statement);
				} else if (triple.getPredicate().equals(RDF.predicate.asNode())) {
					if (!triple.getObject().equals(predicate))
						equals = false;
				} else if (triple.getPredicate().equals(RDF.object.asNode())) {
					if (!triple.getObject().equals(object))
						equals = false;
				} else if (triple.getPredicate().equals(
						FuzzySystem.degree.asNode())) {
					innerDegree = triple.getObject();
				} else if (!triple.getPredicate().equals(RDF.subject.asNode())) {
					throw new Error(" implementation error: predicate \""
							+ triple.getPredicate() + "\" not allowed");
				}
				if (!equals)
					break;
			}
			if (equals) {
				double degreeOfExistingEqualsStatement = Double
						.parseDouble(innerDegree.getLiteralValue().toString());
				if (degreeOfExistingEqualsStatement >= degree) {
					if (DebugInfo.isDebugLevelSet(DebugLevels.BUILTIN2)) {
						System.err
								.println("\t the model has a non weaker statement (reified): ");
						sameId = context.find(id, null, null);
						while (sameId.hasNext())
							System.err.println("\t\t" + sameId.next());
					}
					return false;
				}
			}
		}
		if (DebugInfo.isDebugLevelSet(DebugLevels.BUILTIN2))
			System.err.println("\t the model has NO not-weaker statements");
		return true;
	}

	@Override
	public int getArgLength() {
		return 4;
	}

	@Override
	public String getName() {
		String name = this.getClass().getSimpleName();
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

	@Override
	public String getURI() {
		return nameSpace + this.getName();
	}

	@Override
	public void headAction(Node[] arg0, int arg1, RuleContext arg2) {
		throw new UnsupportedOperationException(
				"this builtin is meant to be used in rule body only");
	}

	@Override
	public boolean isMonotonic() {
		return true;
	}

	@Override
	public boolean isSafe() {
		return true;
	}

}
