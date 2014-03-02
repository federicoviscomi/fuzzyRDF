package queryInterpreter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class LexicalParser {
	private final BufferedReader in;
	private StringTokenizer tokenizer;
	private String nextLine;

	public static void main(String[] args) throws FileNotFoundException,
			LexicalErrorException {
		test("file1");
	}

	private static void test(String string) throws FileNotFoundException,
			LexicalErrorException {
		LexicalParser lp = new LexicalParser(string);
		String nextToken;
		while ((nextToken = lp.nextToken()) != null)
			System.out.println(nextToken);
	}

	public LexicalParser(String fileName) throws FileNotFoundException {
		in = new BufferedReader(new FileReader(fileName));
	}

	public LexicalParser(BufferedReader in) {
		this.in = in;
	}

	public String nextToken() throws LexicalErrorException {
		try {
			if (tokenizer == null || !tokenizer.hasMoreTokens()) {
				nextLine = in.readLine();
				if (nextLine == null)
					return null;
				nextLine = nextLine.trim();
				tokenizer = new StringTokenizer(nextLine, " \t\n\r\f[](),",
						true);
			}
			if (!tokenizer.hasMoreTokens())
				return null;
			String nextToken = null;
			while (tokenizer.hasMoreTokens()) {
				nextToken = tokenizer.nextToken();
				if (" \t\n\r\f".indexOf(nextToken.charAt(0)) == -1) {
					break;
				}
				nextToken = null;
			}
			if (nextToken == null)
				return null;
			if (nextToken.length() == 1
					&& "[](),".indexOf(nextToken.charAt(0)) != -1)
				return nextToken;
			if (nextToken.charAt(0) == '?') {
				if (!isCostantOrIdentifier(nextToken.substring(1)))
					throw new LexicalErrorException(nextLine + " ::: \""
							+ nextToken + "\"");
			} else {
				if (!isCostantOrIdentifier(nextToken))
					throw new LexicalErrorException(nextLine + " ::: \""
							+ nextToken + "\"");
			}
			return nextToken;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private boolean isCostantOrIdentifier(String nextToken) {
		if (Character.isDigit(nextToken.charAt(0))) {
			try {
				Double.parseDouble(nextToken);
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		}
		if (nextToken.charAt(0) == '\"') {
			if (nextToken.charAt(nextToken.length() - 1) != '\"')
				return false;
			for (int i = 1; i < nextToken.length() - 1; i++)
				if (!Character.isLetterOrDigit(nextToken.charAt(i)))
					return false;
			return true;
		}
		if (nextToken.charAt(0) == '\'') {
			if (nextToken.charAt(nextToken.length() - 1) != '\'')
				return false;
			for (int i = 1; i < nextToken.length() - 1; i++)
				if (!Character.isLetterOrDigit(nextToken.charAt(i)))
					return false;
			return true;
		}
		for (int i = 0; i < nextToken.length(); i++)
			if (!Character.isLetterOrDigit(nextToken.charAt(i)))
				return false;
		return true;
	}
}
