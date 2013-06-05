package internal.containers;

public class Property {
	private Reference reference;
	private String name;
	private String value;
	
	Property(Reference reference, String name, String value) {
		this.reference	= reference;
		this.name		= name;
		this.value		= value;
	}
}
