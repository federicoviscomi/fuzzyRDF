package queryInterpreter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;

import fuzzyRDFSEngine.NameMap;

public class CodeOptimizer {
	private IntermediateCodeGenerator icg;

	public CodeOptimizer(String fileName, NameMap predicateToTableName)
			throws FileNotFoundException {
		icg = new IntermediateCodeGenerator(fileName, predicateToTableName);
	}

	public CodeOptimizer(BufferedReader in, NameMap predicateToTableName) {
		icg = new IntermediateCodeGenerator(in, predicateToTableName);
	}

	public RStatement optimizeNextStatement() throws LexicalErrorException,
			SyntacticErrorException {
		RStatement nextStatement = icg.compileNextStatement();
		if (nextStatement == null)
			return null;
		optimize(nextStatement);
		return nextStatement;
	}

	private void optimize(RStatement nextStatement) {
		// TODO
	}

}
