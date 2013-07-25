package internal.parser.containers.property;

import internal.parser.TokenCmpOp;
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
	
	public Property(Reference reference, String name) {
		this.reference	= reference;
		this.name		= name;
	}
	
	
	@Override
	public String toString() {
		return reference.toString() + "." + name;
	}
	
	@Override
	public String debugString() {
		return "PROPERTY(" + reference.debugString() + "." + name + ")"; 
	}
	
	/**
	 * Obtain the reference of this property
	 * @return {@code Reference}
	 */
	public Reference reference() {
		return reference;
	}
	
	/**
	 * Obtain the name of this property
	 * @return {@code String}
	 */
	public String name() {
		return name;
	}
}
