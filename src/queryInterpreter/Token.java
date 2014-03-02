package queryInterpreter;

import queryInterpreter.LexicalAnalizer.TokenTypes;

public class Token {

	public final TokenTypes item2;
	public final String item1;

	public Token(String item1, TokenTypes item2) {
		this.item1 = item1;
		this.item2 = item2;
	}

	public String toString() {
		return "  |:::" + String.format("%15.15s", item2.name()) + "::::::"
				+ item1 + ":::|  ";
	}
}
