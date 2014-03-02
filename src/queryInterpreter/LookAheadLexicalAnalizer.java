package queryInterpreter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;

/**
 * 1-lookahead lexical analizer
 * 
 * @author feffo
 * 
 */
public class LookAheadLexicalAnalizer {

	public static void main(String[] args) throws FileNotFoundException,
			LexicalErrorException {
		test();
	}

	private static void test() throws FileNotFoundException,
			LexicalErrorException {
		LookAheadLexicalAnalizer lala = new LookAheadLexicalAnalizer("file1");
		Token nextToken;
		while ((nextToken = lala.nextToken()) != null) {
			System.out.println(nextToken + "\t" + lala.lookahead());
		}
	}

	private final LexicalAnalizer la;

	private Token lookahead = null;

	public LookAheadLexicalAnalizer(String fileName)
			throws FileNotFoundException {
		la = new LexicalAnalizer(fileName);
	}

	public LookAheadLexicalAnalizer(BufferedReader in) {
		la = new LexicalAnalizer(in);
	}

	/**
	 * Avanza di uno il puntatore ai token e restituisce il token puntato.
	 * 
	 * @return
	 * @throws LexicalErrorException
	 */
	public Token nextToken() throws LexicalErrorException {
		if (lookahead == null)
			return la.nextToken();
		Token temp = lookahead;
		lookahead = null;
		return temp;
	}

	public Token lookahead() throws LexicalErrorException {
		if (lookahead != null)
			return lookahead;
		lookahead = la.nextToken();
		return lookahead;
	}
}
