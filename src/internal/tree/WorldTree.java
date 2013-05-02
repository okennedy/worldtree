package internal.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * WorldTree is the abstract class that every object in the hierarchy extends.
 * It is used to provide a common structure and interface to all the objects in the world.
 * 
 * @author guru
 */

public abstract class WorldTree implements IWorldTree {
	protected IWorldTree parent;
	protected Collection<IWorldTree> children;
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
	
	public Collection<IWorldTree> children() {
		return children;
	}
	
	/**
	 * Get set of strings used to represent this WorldTree instance.
	 * @return {@code List<String>} containing the strings used to visually represent this WorldTree instance.
	 */
	public List<String> getStringRepresentation() {
		stringRepresentation.removeAll(stringRepresentation);
		initString();
		return stringRepresentation;
	}
	
	@Override
	public String toString() {
		List<String> stringList = getStringRepresentation();
		StringBuffer result = new StringBuffer();
		for(String string : stringList)
			result.append(string + System.getProperty("line.separator"));
		
		return result.toString();
	}
	
	/**
	 * Method provided to initialize the string representation of this instance.
	 * This method is to be called once all it's children have been initialized.
	 * <p>
	 * The logic is as follows:<br>
	 * Every child is expected to have a {@code List<String>} representing each line of its visual.<br>
	 * We need to concatenate each line of every child together and then CR+LF onto the next line.<br>
	 * {@code listStringList} contains the list of stringLists (2-D {@code ArrayList}).<br>
	 * We append every line of every {@code List<List<String>>} before moving onto the next index.<br>
	 */
	protected void initString() {
		if(children() == null)
			return;
		List<List<String>> listStringList = new ArrayList<List<String>>();
		for(IWorldTree child : children()) {
			listStringList.add(child.getStringRepresentation());
		}
		int lineCount = 0;
		for(List<String> stringList : listStringList)
				lineCount = lineCount > stringList.size() ? lineCount : stringList.size();
		for(int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
			StringBuffer fullLine = new StringBuffer();
			for(List<String> stringList : listStringList) {
				if(stringList.size() > lineIndex)
					fullLine.append(stringList.get(lineIndex) + " ");
			}
//			if(!stringRepresentation.contains(fullLine.toString()))
				stringRepresentation.add(fullLine.toString());
		}
		
//		We have obtained every line as we should. We now need to own everything that is within is.
		prepareToString();
		
	}
	
	/**
	 * Helper method that wraps around the initialized string representation to print ownership in the visual.
	 */
	protected void prepareToString() {
//		The string representation is in place. find the maximum length and wrap around it to own it.\
//		Make the top part of the outer shell that wraps around all of this instance's children.
		List<String> newStringRepresentation = new ArrayList<String>();
		int maxLineLength = 0;
		for(String string : stringRepresentation)
			maxLineLength = string.length() > maxLineLength ? string.length() : maxLineLength;
		StringBuffer header = new StringBuffer();
		maxLineLength += this.name.length();
		maxLineLength += (maxLineLength % 2 == 0) ? 0 : 1;
		header.append("+");
		for(int i = 0; i < maxLineLength; i++)
			header.append("-");
		header.append("+");
		newStringRepresentation.add(header.toString());
		header = new StringBuffer();
		header.append("|" + this.name);
		for(int i = 0; i < maxLineLength - name.length(); i++)
			header.append(" ");
		header.append("|");
		maxLineLength = header.toString().length();
		
		newStringRepresentation.add(header.toString());
		header = null;
		
//		Now that the top is done, add the middle components (string representation of children)
		for(String string : stringRepresentation) {
			int spaces = maxLineLength - string.length() - 2;	//The 2 is because of the starting and ending '|'
			StringBuffer line = new StringBuffer("|");
			for(int i = 0; i < spaces / 2; i++)
				line.append(" ");
			line.append(string);
			for(int i = 0; i < (spaces - spaces / 2); i++)
				line.append(" ");
			line.append("|");
			if(line.length() != newStringRepresentation.get(0).length())
				throw new IllegalStateException();
			newStringRepresentation.add(line.toString());
//			System.out.println(newStringRepresentation.toString().replaceAll("(\\[|\\]|,  )", ""));
		}
		
//		Add the bottom part of the outer shell that wraps around all of this instance's children
		StringBuffer footer = new StringBuffer();
		footer.append("|");
		for(int i = 0; i < maxLineLength - 2; i++)
			footer.append(" ");
		footer.append("|");
		newStringRepresentation.add(footer.toString());
		String line = footer.toString();
		footer = new StringBuffer();
		
		line = line.replace(" ", "-").replace("|", "+");
		footer.append(line);
		newStringRepresentation.add(footer.toString());
		footer = null;
		
//		Update pointer.
		stringRepresentation = newStringRepresentation;
	}
}
