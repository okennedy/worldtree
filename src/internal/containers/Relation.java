package internal.containers;

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
	
	public String name() {
		return name;
	}
	
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
	
	public Type type() {
		InbuiltRelationEnum rel = InbuiltRelationEnum.check(name);
		
		Type t = (rel == null) ? Type.CUSTOM : Type.INBUILT;
		return t;
	}
	
	
	public enum Type {
		INBUILT,
		CUSTOM
	}
	
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
		
		public static InbuiltRelationEnum check(String method) {
			for(InbuiltRelationEnum rel : values()) {
				if(rel.method.equalsIgnoreCase(method))
					return rel;
			}
			return null;
		}
	}
	
	public enum Regex {
		STAR("*"),
		PLUS("+"),
		NONE(""),
		;
		
		private String regex;
		
		private Regex(String regex) {
			this.regex	= regex;
		}
		
		public static Regex get(String regex) {
			for(Regex r : values()) {
				if(r.regex.equals(regex))
					return r;
			}
			return NONE;
		}
		
		@Override
		public String toString() {
			return regex;
		}
	}
}
