package internal.parser.containers.condition;

/**
 * Enum enumerating the various union types
 * @author guru
 *
 */
public enum UnionType {
	AND,
	OR,
	;
	
	/**
	 * Obtain the {@code UnionType} based on the parameter
	 * @param type {@code String} representing a {@code UnionType}
	 * @return {@code UnionType} corresponding to the parameter <br>
	 * <b>null</b> otherwise
	 */
	public static UnionType getType(String type) {
		for(UnionType u : values()) {
			if(u.toString().equalsIgnoreCase(type))
				return u;
		}
		return null;
	}
}
