package internal.parser;

/**
 * This enum is used to store the various arithmetic operators
 * @author Guru
 *
 */
public enum TokenArithOp {
	TK_PLUS("+"),
	TK_MINUS("-"),
	TK_MULT("*"),
	TK_DIV("/"),
	;
	
	private String operator;
	
	private TokenArithOp(String operator) {
		this.operator	= operator;
	}
	
	/**
	 * Parse a {@code String} and obtain its corresponding {@code TokenArithOp} enum
	 * @param operator {@code String} containing the operator
	 * @return {@code TokenArithOp} corresponding to the specified <b>operator</b> <br>
	 * @throws IllegalArgumentException if the specified <b>operator</b> is invalid
	 */
	public static TokenArithOp parse(String operator) {
		for(TokenArithOp tk : values()) {
			if(tk.operator.equals(operator))
				return tk;
		}
		throw new IllegalArgumentException("'" + operator + "' is not a valid arithmetic operator!");
	}
	
	@Override
	public String toString() {
		return operator;
	}
}