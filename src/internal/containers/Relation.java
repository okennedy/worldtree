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
		
		if(regex == null) {
			result.append(")");
			return result.toString();
		}
		
		else {
			result.append(regex);
			result.append(")");
			return result.toString();
		}
	}
}
