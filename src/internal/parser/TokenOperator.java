package internal.parser;

public enum TokenOperator {
	GE(">="),
	LE("<="),
	GT(">"),
	LT("<"),
	EQ("="),
	NOTEQ("!="),
	;
	
	private final String op;
	
	private TokenOperator(String op) {
		this.op = op;
	}
	
	public static TokenOperator parse(String op) {
		for(TokenOperator tk : values()) {
			if(tk.op.equals(op))
				return tk;
		}
		throw new IllegalArgumentException("'" + op + "' is not a valid TokenOperator!");
	}
	
	@Override
	public String toString() {
		return op;
	}
}
