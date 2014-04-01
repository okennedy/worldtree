package internal.parser.resolve;

import internal.parser.containers.Reference;
import internal.tree.IWorldTree;

import java.util.Collection;
import java.util.Vector;

/**
 * The Column class is used to store each column of the output produced by the ResolutionEngine.<br>
 * @author guru
 *
 */
public class Column extends Vector<IWorldTree> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8728524978224116816L;
	protected Reference reference;
	
	public Column(Reference reference, Collection<IWorldTree> collection) {
		this.reference	= reference;
		this.addAll(collection);
	}

	public Column(Reference reference) {
		this.reference 	= reference;
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer(reference + "\n");
		
		for(IWorldTree obj : this) {
			result.append(obj.absoluteName() + "\n");
		}
		
		return result.toString();
	}

	public Reference name() {
		return reference;
	}
}
