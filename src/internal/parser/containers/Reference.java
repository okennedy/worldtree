package internal.parser.containers;

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
	
	@Override
	public boolean equals(Object o) {
		Reference ref = null;
		try {
			ref = (Reference) o;
		} catch(ClassCastException e) {
			return false;
		}
		if(this.name.equals(ref.name))
			return true;
		else
			return false;
	}
}
