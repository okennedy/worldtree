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
		return name + regex;
	}



	@Override
	public String debugString() {
		return "RELATION(" + name + regex + ")";
	}
}
