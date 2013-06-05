package internal.containers;

public class Reference implements IContainer {
	private String name;
	
	public Reference(String name) {
		this.name	= name;
	}
	
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public String debugString() {
		return "REFERENCE" + "(" + name + ")";
	}
}
