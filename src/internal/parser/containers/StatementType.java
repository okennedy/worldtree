package internal.parser.containers;

/**
 * Enum containing the various statement types <br>
 * Any new statement that is added to the parser must also be added to this enum
 * @author guru
 *
 */
public enum StatementType {
	QUERY 		("QUERY"),
	CONSTRAINT	("CONSTRAINT"),
	PROPERTYDEF	("PROPERTYDEF"),
	;
	
	private String type;
	
	private StatementType(String type) {
		this.type = type;
	}
	
	/**
	 * Obtain the {@code StatementType} that corresponds to the specified parameter
	 * @param type {@code String} containing the textual representation of a {@code StatementType}
	 * @return {@code StatementType} representing the specified parameter
	 */
	public static StatementType getType(String type) {
		for(StatementType st : values()) {
			if(st.type.equalsIgnoreCase(type))
				return st;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return type;
	}
}
