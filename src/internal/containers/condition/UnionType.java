package internal.containers.condition;

public enum UnionType {
	AND,
	OR,
	;
	
	public static UnionType getType(String type) {
		for(UnionType u : values()) {
			if(u.toString().equalsIgnoreCase(type))
				return u;
		}
		return null;
	}
}
