package queryInterpreter;

public class ParsedWhereStatement {

	final Token subject;
	final Token predicate;
	final Token object;
	final Token degree;

	public ParsedWhereStatement(Token subject, Token predicate, Token object,
			Token degree) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
		this.degree = degree;
	}

	public String toString() {
		return "{" + subject.toString() + ", " + predicate.toString() + ", "
				+ object.toString() + ", " + degree.toString() + "}";
	}
}
