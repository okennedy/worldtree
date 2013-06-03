package internal.parser;

public class Property {
	Reference reference;
	String name;
	String value;
	
	Property(Reference reference, String name, String value) {
		this.reference	= reference;
		this.name		= name;
		this.value		= value;
	}
}
