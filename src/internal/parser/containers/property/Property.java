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
	
	
	/**
	 * Enum containing all the built-in properties<br>
	 * @author guru
	 *
	 */
	public enum InbuiltPropertyEnum {
		PASSABLE_EAST("passableeast"),
		PASSABLE_WEST("passablewest"),
		;
		
		private String property;
		
		private InbuiltPropertyEnum(String property) {
			this.property	= property;
		}
		
		/**
		 * Check whether a property is built-in
		 * @param method {@code String} containing the name of the property to check
		 * @return {@code InbuiltPropertyEnum} corresponding to the specified property <br>
		 * @throws IllegalStateException if there is no such property
		 */
		public static InbuiltPropertyEnum check(String property) {
			for(InbuiltPropertyEnum rel : values()) {
				if(rel.property.equalsIgnoreCase(property))
					return rel;
			}
			return null;
		}
		
	}
}
