package internal.containers;

public class Reference {
	private String name;
	
	public Reference(String name) {
		this.name	= name;
	}
	
	
	@Override
	public String toString() {
		return name;
	}
}
