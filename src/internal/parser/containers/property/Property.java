package internal.parser.containers.property;

import internal.parser.containers.IContainer;
import internal.parser.containers.Reference;

/**
 * Container class for a property <br>
 * PROPERTY := REFERENCE '.' PROPERTY COMPARATOR CONSTANT
 * @author guru
 *
 */
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
