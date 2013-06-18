package internal.containers;

/**
 * Container class for storing a reference <br>
 * REFERENCE := WORD
 * @author guru
 *
 */
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
