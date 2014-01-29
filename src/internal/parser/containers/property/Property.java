package internal.parser.containers.property;

import java.util.HashMap;
import internal.parser.containers.IContainer;

/**
 * Container class for a property <br>
 * PROPERTY := REFERENCE '.' PROPERTY COMPARATOR CONSTANT
 * @author guru
 *
 */
public class Property implements IContainer {
	private static final HashMap<String, Property> propertyMap = new HashMap<String, Property>();
	
	private String name;
	
	protected Property() {
	}
	
	private Property(String name) {
		this.name		= name;
	}

	public static Property getProperty(String name) {
		Property returnValue = propertyMap.get(name);
		if (returnValue == null) {
			returnValue = new Property(name);
			propertyMap.put(name, returnValue);
		}
		return returnValue;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public String debugString() {
		return "PROPERTY(" + name + ")"; 
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
		public static InbuiltPropertyEnum check(Property property) {
			for(InbuiltPropertyEnum rel : values()) {
				if(rel.property.equalsIgnoreCase(property.toString()))
					return rel;
			}
			return null;
		}
		
	}
}
