package internal.containers;

public enum StatementType {
	QUERY 		("QUERY"),
	CONSTRAINT	("CONSTRAINT"),
	PROPERTYDEF	("PROPERTYDEF"),
	;
	
	private String type;
	
	private StatementType(String type) {
		this.type = type;
	}
	
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
