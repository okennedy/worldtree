package internal.containers;

public class Relation implements IContainer {
	private String name;
	private String regex;
	
	public Relation(String name, String regex) {
		this.name	= name;
		this.regex	= regex;
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
	
	public Type getType(String method) {
		InbuiltRelation rel = InbuiltRelation.check(method);
		
		Type t = (rel == null) ? Type.CUSTOM : Type.INBUILT;
		return t;
	}
	
	
	public enum Type {
		INBUILT,
		CUSTOM
	}
	
	private enum InbuiltRelation {
		TO_EAST("toeast"),
		TO_WEST("towest"),
		TO_NORTH("tonorth"),
		TO_SOUTH("tosouth"),
		BEGIN("begin"),
		END("end"),
		;
		
		private String method;
		
		private InbuiltRelation(String method) {
			this.method	= method;
		}
		
		public static InbuiltRelation check(String method) {
			for(InbuiltRelation rel : values()) {
				if(rel.method.equalsIgnoreCase(method))
					return rel;
			}
			return null;
		}
	}
}
