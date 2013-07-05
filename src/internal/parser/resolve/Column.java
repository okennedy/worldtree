package internal.parser.resolve;

import internal.tree.IWorldTree;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The Column class is used to store each column of the output produced by the ResolutionEngine.<br>
 * @author guru
 *
 */
public class Column extends ArrayList<IWorldTree> {
	protected String name;
	
	public Column(String name, Collection<IWorldTree> collection) {
		this.name	= name;
		this.addAll(collection);
	}

	public Column(String name) {
		this.name 	= name;
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer(name + "\n");
		
		for(IWorldTree obj : this) {
			result.append(obj.name() + "\n");
		}
		
		return result.toString();
	}

	public String name() {
		return name;
	}
}
