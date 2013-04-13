package internal.tree;

import java.util.HashMap;

public class Constraint extends HashMap<String, String> {
	private static final long serialVersionUID = 1L;
	
	public void add(String key, String value) {
		this.put(key, value);
	}
}
