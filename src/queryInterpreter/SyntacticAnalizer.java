package queryInterpreter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import fuzzyRDFSEngine.NameMap;

import queryInterpreter.LexicalAnalizer.TokenTypes;

public class SyntacticAnalizer {

	public static void main(String[] args) throws FileNotFoundException,
			LexicalErrorException, SyntacticErrorException {
		test();
	}

	private static void test() throws FileNotFoundException,
			LexicalErrorException, SyntacticErrorException {
		SyntacticAnalizer sa = new SyntacticAnalizer("file1", new NameMap());
		RStatement next;
		while ((next = sa.analizeASingleRStatement()) != null) {
			// next.print(System.out);
			next.print(System.out);
		}
	}

	// TODO il punto interrogativo deve essere trattato dall'analizzatore
	// lessicale introducendo un altro tipo di token. es: variable
	private LookAheadLexicalAnalizer la;
	private final NameMap predicateToTableName;

	public SyntacticAnalizer(String fileName, NameMap predicateToTableName)
			throws FileNotFoundException {
		this.predicateToTableName = predicateToTableName;
		la = new LookAheadLexicalAnalizer(fileName);
	}

	public SyntacticAnalizer(BufferedReader in, NameMap predicateToTableName) {
		this.predicateToTableName = predicateToTableName;
		la = new LookAheadLexicalAnalizer(in);
	}

	public RStatement analizeASingleRStatement() throws LexicalErrorException,
			SyntacticErrorException {
		Token nextToken = la.nextToken();
		if (nextToken == null)
			return null;
		RStatement rs = new RStatement();
		match(nextToken, TokenTypes.O_ROUND_BRACKET);
		parseRetreive(rs);
		parseWhereConditionList(rs);
		// TODO dopo una groupby ci deve essere necessariamente una orderby???
		if (la.lookahead() != null) {
			if (la.lookahead().item2.ordinal() == TokenTypes.GROUPBY.ordinal()) {
				parseCanonicalGroupBy(rs);
				parseCanonicalOrderBy();
			} else if (la.lookahead().item2.ordinal() == TokenTypes.ORDERBY
					.ordinal()) {
				parseCanonicalOrderBy();
			}
			if (la.lookahead().item2.ordinal() == TokenTypes.LIMIT.ordinal())
				parseCanonicalLimit(rs);
		}
		match(getNextToken(), TokenTypes.C_ROUND_BRACKET);
		return rs;
	}

	private void parseCanonicalLimit(RStatement rs)
			throws SyntacticErrorException, LexicalErrorException {
		match(getNextToken(), TokenTypes.LIMIT);
		match(getNextToken(), TokenTypes.O_ROUND_BRACKET);
		Token limitToken = getNextToken();
		match(limitToken, TokenTypes.CONSTANT_NUMBER);
		rs.setlimit(Integer.parseInt(limitToken.item1));
		match(getNextToken(), TokenTypes.C_ROUND_BRACKET);
	}

	private void parseCanonicalOrderBy() throws SyntacticErrorException,
			LexicalErrorException {
		match(getNextToken(), TokenTypes.ORDERBY);
		match(getNextToken(), TokenTypes.O_ROUND_BRACKET);
		Token nextToken;
		while ((nextToken = getNextToken()).item2.ordinal() != TokenTypes.C_ROUND_BRACKET
				.ordinal()) {
			// TODO
		}
	}

	private void parseCanonicalGroupBy(RStatement rs)
			throws SyntacticErrorException, LexicalErrorException {
		match(getNextToken(), TokenTypes.GROUPBY);
		match(getNextToken(), TokenTypes.O_ROUND_BRACKET);
		Token nextToken;
		while ((nextToken = getNextToken()).item2.ordinal() != TokenTypes.C_ROUND_BRACKET
				.ordinal()) {
			nextToken = getNextToken();
			match(nextToken, TokenTypes.VARIABLE);
			rs.addGroupByVariable(nextToken.item1);
			if (la.lookahead().item2.ordinal() == TokenTypes.COMMA.ordinal())
				match(la.nextToken(), TokenTypes.COMMA);
		}
	}

	private Token getNextToken() throws LexicalErrorException,
			SyntacticErrorException {
		Token nextToken = la.nextToken();
		if (nextToken == null)
			throw new SyntacticErrorException();
		return nextToken;
	}

	private void match(Token nextToken, TokenTypes retreive)
			throws SyntacticErrorException {
		if (nextToken.item2.ordinal() != retreive.ordinal())
			throw new SyntacticErrorException("found " + nextToken
					+ " but expected " + retreive);
	}

	private void match(Token nextToken, TokenTypes... types)
			throws SyntacticErrorException {
		for (TokenTypes type : types)
			if (nextToken.item2.ordinal() == type.ordinal())
				return;
		throw new SyntacticErrorException();
	}

	private void parseASingleRelation(RStatement rs)
			throws SyntacticErrorException, LexicalErrorException {
		match(getNextToken(), TokenTypes.O_ROUND_BRACKET);
		/** get subject */
		Token subject = getNextToken();
		match(subject, TokenTypes.IDENTIFIER, TokenTypes.CONSTANT_NUMBER,
				TokenTypes.CONSTANT_STRING, TokenTypes.VARIABLE);
		boolean commaExpected = false;
		if (la.lookahead().item2.ordinal() == TokenTypes.COMMA.ordinal()) {
			match(getNextToken(), TokenTypes.COMMA);
			commaExpected = true;
		}
		/** get predicate */
		Token predicate = getNextToken();
		match(predicate, TokenTypes.IDENTIFIER, TokenTypes.VARIABLE);
		predicate = new Token(predicateToTableName.get(predicate.item1),
				predicate.item2);
		if (commaExpected) {
			match(getNextToken(), TokenTypes.COMMA);
		}
		/** get object */
		Token object = getNextToken();
		match(subject, TokenTypes.IDENTIFIER, TokenTypes.CONSTANT_NUMBER,
				TokenTypes.CONSTANT_STRING, TokenTypes.VARIABLE);
		match(getNextToken(), TokenTypes.C_ROUND_BRACKET);
		/** get degree */
		Token degree = new Token("1", TokenTypes.CONSTANT_NUMBER);
		if (la.lookahead().item2.ordinal() == TokenTypes.O_SQUARE_BRACKET
				.ordinal()) {
			match(getNextToken(), TokenTypes.O_SQUARE_BRACKET);
			degree = getNextToken();
			match(degree, TokenTypes.VARIABLE, TokenTypes.CONSTANT_NUMBER);
			match(getNextToken(), TokenTypes.C_SQUARE_BRACKET);
		}
		/** done parsing */
		rs.addWhereCondition(new ParsedWhereStatement(subject, predicate,
				object, degree));
	}

	private void parseRetreive(RStatement rs) throws LexicalErrorException,
			SyntacticErrorException {
		match(getNextToken(), TokenTypes.RETREIVE);
		match(getNextToken(), TokenTypes.O_ROUND_BRACKET);
		Token nextToken;
		while ((nextToken = getNextToken()).item2.ordinal() != TokenTypes.C_ROUND_BRACKET
				.ordinal()) {
			match(nextToken, TokenTypes.VARIABLE);
			rs.addRetreiveVar(nextToken.item1);
			if (la.lookahead().item2.ordinal() == TokenTypes.COMMA.ordinal())
				match(la.nextToken(), TokenTypes.COMMA);
		}
	}

	private void parseWhereConditionList(RStatement rs)
			throws LexicalErrorException, SyntacticErrorException {
		match(getNextToken(), TokenTypes.WHERE);
		match(getNextToken(), TokenTypes.O_ROUND_BRACKET);
		while (la.lookahead().item2.ordinal() != TokenTypes.C_ROUND_BRACKET
				.ordinal()) {
			parseASingleRelation(rs);
		}
		match(getNextToken(), TokenTypes.C_ROUND_BRACKET);
	}

}
