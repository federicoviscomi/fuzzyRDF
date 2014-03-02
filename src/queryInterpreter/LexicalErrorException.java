package queryInterpreter;

public class LexicalErrorException extends Exception {

	public LexicalErrorException(String nextLine) {
		super(nextLine);
	}
}
