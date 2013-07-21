package internal.parser;

public enum TokenCmpOp {
	GE(">="),
	LE("<="),
	GT(">"),
	LT("<"),
	EQ("="),
	NOTEQ("!="),
	;
	
	private final String op;
	
	private TokenCmpOp(String op) {
		this.op = op;
	}
	
	public static TokenCmpOp parse(String op) {
		for(TokenCmpOp tk : values()) {
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
