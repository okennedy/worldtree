package internal.parser;

public class Query {
	private Pattern pattern;
	private Condition condition;
	
	public Query(Pattern pattern, Condition condition) {
		this.pattern	= pattern;
		this.condition	= condition;
	}
}
