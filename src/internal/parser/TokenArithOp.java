package internal.parser;

public enum TokenArithOp {
	TK_PLUS("+"),
	TK_MINUS("-"),
	TK_MULT("*"),
	TK_DIV("/"),
	;
	
	private String op;
	
	private TokenArithOp(String op) {
		this.op	= op;
	}
	
	public static TokenArithOp parse(String op) {
		for(TokenArithOp tk : values()) {
			if(tk.op.equals(op))
				return tk;
		}
		return null;
	}
}