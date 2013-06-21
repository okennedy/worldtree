package internal.parser.containers;

/**
 * Container class for storing a relation <br>
 * RELATION := WORD(*|+)?
 * @author guru
 *
 */
public class Relation implements IContainer {
	private String name;
	private Regex regex;
	
	public Relation(String name, String regex) {
		this.name	= name;
		if(regex == null || regex == "")
			this.regex = Regex.NONE;
		else
			this.regex	= Regex.get(regex);	//Last index
	}
	
	/**
	 * Obtain the name of this{@code Relation} 
	 * @return {@code String} representing the name of this{@code Relation}
	 */
	public String name() {
		return name;
	}
	
	/**
	 * Obtain the Regex qualifier for this{@code Relation}
	 * @return {@code Regex} specifying the Regex qualifier
	 */
	public Regex regex() {
		return regex;
	}
	
	@Override
	public String toString() {
		return regex == null ? name : name + regex;
	}



	@Override
	public String debugString() {
		StringBuffer result = new StringBuffer("RELATION(" + name);
		
		if(regex != null)
			result.append(regex);
		
		result.append(")");
		return result.toString();
	}
	
	/**
	 * Obtain the type of this {@code Relation}
	 * @return {@code Type}
	 */
	public Type type() {
		InbuiltRelationEnum rel = InbuiltRelationEnum.check(name);
		
		Type t = (rel == null) ? Type.CUSTOM : Type.INBUILT;
		return t;
	}
	
	
	/**
	 * Enum that is used to identify whether this relation is built-in or custom
	 * @author guru
	 *
	 */
	public enum Type {
		INBUILT,
		CUSTOM
	}
	
	/**
	 * Enum containing all the built-in relations<br>
	 * Any update to the {@code InbuiltRelations} inner-class in {@code ResolutionEngine} must reflect in this enum as well  
	 * @author guru
	 *
	 */
	public enum InbuiltRelationEnum {
		TO_EAST("toeast"),
		TO_WEST("towest"),
		TO_NORTH("tonorth"),
		TO_SOUTH("tosouth"),
		BEGIN("begin"),
		END("end"),
		;
		
		private String method;
		
		private InbuiltRelationEnum(String method) {
			this.method	= method;
		}
		
		/**
		 * Check whether a method is built-in
		 * @param method {@code String} containing the name of the method to check
		 * @return {@code InbuiltRelationEnum} corresponding to the specified method <br>
		 * @throws IllegalStateException if there is no such method
		 */
		public static InbuiltRelationEnum check(String method) {
			for(InbuiltRelationEnum rel : values()) {
				if(rel.method.equalsIgnoreCase(method))
					return rel;
			}
			throw new IllegalStateException("Cannot be checking for inbuilt relation when it does not exist in enum\n" +
					"Did you modify ResolutionEngine?\n");
		}
		
		public static Relation invert(Relation relation) {
			InbuiltRelationEnum rel = check(relation.name);
			String newMethod		= null;
			switch(rel) {
			case TO_EAST:
				newMethod = TO_WEST.method;
				break;
			case TO_NORTH:
				newMethod = TO_SOUTH.method;
				break;
			case TO_SOUTH:
				newMethod = TO_NORTH.method;
				break;
			case TO_WEST:
				newMethod = TO_EAST.method;
				break;
			case BEGIN:
			case END:
			default :
				throw new IllegalStateException("This should have been caught at InbuiltRelationEnum.check()");
			}
			
			return new Relation(newMethod, relation.regex.toString());
		}
	}
	
	/**
	 * Enum enumerating the possible RegularExpression states 
	 * @author guru
	 *
	 */
	public enum Regex {
		STAR("*"),
		PLUS("+"),
		NONE(""),
		;
		
		private String regex;
		
		private Regex(String regex) {
			this.regex	= regex;
		}
		
		/**
		 * Obtain the {@code Regex} corresponding to the parameter
		 * @param regex {@code String} representing the textual form of a {@code Regex}
		 * @return {@code Regex} mapped to the specified parameter
		 */
		public static Regex get(String regex) {
			for(Regex r : values()) {
				if(r.regex.equals(regex))
					return r;
			}
			return NONE;
		}
		
		/**
		 * Obtain {@code String} representation of this {@code Regex}
		 * @return {@code String} representing the textual form of this {@code Regex}
		 */
		@Override
		public String toString() {
			return regex;
		}
	}
}
