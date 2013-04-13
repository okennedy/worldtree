package internal.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * WorldTree is the abstract class that every object in the hierarchy extends.
 * It is used to provide a common structure and interface to all the objects in the world.
 * 
 * @author guru
 */

public abstract class WorldTree implements IWorldTree {
	protected IWorldTree parent;
	protected List<IWorldTree> children;
	private String name;
	private Constraint constraints;
	protected List<String> stringRepresentation;

	protected WorldTree(String name, IWorldTree parent, Constraint constraints) {
		this.parent 		= parent;
		this.children 		= null;
		this.name 			= name;
		this.constraints 	= constraints;
		this.stringRepresentation = new ArrayList<String>();
	}

	public String name() {
		return name;
	}
	
	public IWorldTree parent() {
		return parent;
	}
	
	public List<IWorldTree> children() {
		return children;
	}
	
	public List<String> getStringRepresentation() {
		return stringRepresentation;
	}
	
	@Override
	public String toString() {
		return stringRepresentation.toString();
	}
	
	public void initString() {
		StringBuffer returnString = new StringBuffer();
		if(children == null)
			return;
		List<List<String>> listStringList = new ArrayList<List<String>>();
		for(IWorldTree child : children) {
			listStringList.add(child.getStringRepresentation());
		}
		int lineCount = 0;
		for(List<String> stringList : listStringList)
				lineCount = lineCount > stringList.size() ? lineCount : stringList.size();
		for(int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
			StringBuffer fullLine = new StringBuffer();
			for(List<String> stringList : listStringList) {
				if(stringList.size() < lineIndex)
					fullLine.append(stringList.get(lineIndex));
			}
			returnString.append(fullLine.toString() + "\n");
			if(!stringRepresentation.contains(fullLine.toString()))
				stringRepresentation.add(fullLine.toString());
		}
		parent.initString();
	}
}
