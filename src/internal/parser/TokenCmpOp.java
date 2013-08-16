package internal.parser;

/**
 * enum used to store various comparison operators 
 * @author Guru
 *
 */
public enum TokenCmpOp {
	GE(">="),
	LE("<="),
	GT(">"),
	LT("<"),
	EQ("="),
	NOTEQ("!="),
	;
	
	private final String operator;
	
	private TokenCmpOp(String operator) {
		this.operator = operator;
	}
	
	/**
	 * Parse a {@code String} and obtain its corresponding {@code TokenCmpOp} enum
	 * @param operator {@code String} containing the operator
	 * @return {@code TokenCmpOp} corresponding to the specified <b>operator</b> <br>
	 * @throws IllegalArgumentException if the specified <b>operator</b> is invalid
	 */
	public static TokenCmpOp parse(String operator) {
		for(TokenCmpOp tk : values()) {
			if(tk.operator.equals(operator))
				return tk;
		}
		throw new IllegalArgumentException("'" + operator + "' is not a valid comparison operator!");
	}
	
	@Override
	public String toString() {
		return operator;
	}
}
