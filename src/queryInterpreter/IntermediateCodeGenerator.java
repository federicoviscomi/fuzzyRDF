package queryInterpreter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;

import fuzzyRDFSEngine.NameMap;

import queryInterpreter.LexicalAnalizer.TokenTypes;

public class IntermediateCodeGenerator {

	private final SyntacticAnalizer sa;

	public static void main(String[] args) throws FileNotFoundException,
			LexicalErrorException, SyntacticErrorException {
		test();
	}

	private static void test() throws FileNotFoundException,
			LexicalErrorException, SyntacticErrorException {
		IntermediateCodeGenerator icg = new IntermediateCodeGenerator("file1",
				new NameMap());
		RStatement next;
		while ((next = icg.compileNextStatement()) != null)
			next.printResolved(System.out);
	}

	public IntermediateCodeGenerator(String fileName,
			NameMap predicateToTableName) throws FileNotFoundException {
		this.sa = new SyntacticAnalizer(fileName, predicateToTableName);
	}

	public IntermediateCodeGenerator(BufferedReader in,
			NameMap predicateToTableName) {
		this.sa = new SyntacticAnalizer(in, predicateToTableName);
	}

	/**
	 * se NON ci sono variabili nella seconda componente di una condizione
	 * allora non fa nulla??? altrimenti assegna a tali variabili tutte le
	 * possibili combinazioni di valori e scrive le query relative
	 * 
	 * @throws SyntacticErrorException
	 * @throws LexicalErrorException
	 */
	public RStatement compileNextStatement() throws LexicalErrorException,
			SyntacticErrorException {
		RStatement nextStatement = sa.analizeASingleRStatement();
		if (nextStatement == null)
			return null;
		partialResolve(nextStatement);
		return nextStatement;
	}

	private void partialResolve(RStatement nextStatement)
			throws SyntacticErrorException {
		for (ParsedWhereStatement pws : nextStatement.whereConditionList) {
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
				if (RStatement.isUnresolved(resolved)) {
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
				if (RStatement.isUnresolved(resolved)) {
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
				if (resolved.equals("?")) {
					nextStatement.retrieveVariables.put(degree.item1,
							predicate.item1 + ".object");
				}
			}
		}
	}
}
