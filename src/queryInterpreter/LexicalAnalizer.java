package queryInterpreter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;

import javax.lang.model.element.NestingKind;

public class LexicalAnalizer {
	private LexicalParser lp;

	public LexicalAnalizer(String fileName) throws FileNotFoundException {
		lp = new LexicalParser(fileName);
	}

	public LexicalAnalizer(BufferedReader in) {
		lp = new LexicalParser(in);
	}

	public static void main(String[] args) throws FileNotFoundException,
			LexicalErrorException {
		test();
	}

	private static void test() throws FileNotFoundException,
			LexicalErrorException {
		LexicalAnalizer la = new LexicalAnalizer("file1");
		Token nextToken;
		while ((nextToken = la.nextToken()) != null)
			System.out.println(nextToken);
	}

	public static enum TokenTypes {
		RETREIVE, WHERE, ORDERBY, GROUPBY, LIMIT, O_ROUND_BRACKET, C_ROUND_BRACKET, COMMA, O_SQUARE_BRACKET, C_SQUARE_BRACKET, IDENTIFIER, VARIABLE, CONSTANT_STRING, CONSTANT_NUMBER
	}

	private static final String[] tokens = { "RETREIVE", "WHERE", "ORDERBY",
			"GROUPBY", "LIMIT", "(", ")", ",", "[", "]" };

	public Token nextToken() throws LexicalErrorException {
		String nextLexicalToken = lp.nextToken();
		if (nextLexicalToken == null)
			return null;
		for (int i = 0; i < tokens.length; i++)
			if (tokens[i].equalsIgnoreCase(nextLexicalToken))
				return new Token(nextLexicalToken, TokenTypes.values()[i]);
		if (nextLexicalToken.startsWith("\"")
				|| nextLexicalToken.startsWith("\'"))
			return new Token(nextLexicalToken, TokenTypes.CONSTANT_STRING);
		if (Character.isDigit(nextLexicalToken.charAt(0)))
			return new Token(nextLexicalToken, TokenTypes.CONSTANT_NUMBER);
		if (nextLexicalToken.startsWith("?"))
			return new Token(nextLexicalToken, TokenTypes.VARIABLE);
		return new Token(nextLexicalToken, TokenTypes.IDENTIFIER);
	}
}
