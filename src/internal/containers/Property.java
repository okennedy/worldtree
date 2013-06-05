package internal.containers;

public class Property implements IContainer {
	private Reference reference;
	private String name;
	private String value;
	
	public Property(Reference reference, String name, String value) {
		this.reference	= reference;
		this.name		= name;
		this.value		= value;
	}
	
	
	@Override
	public String toString() {
		return reference.toString() + "." + name + " = " + value;
	}
	
	@Override
	public String debugString() {
		return "PROPERTY(" + reference.debugString() + "." + name + " = " + value + ")"; 
	}
}
