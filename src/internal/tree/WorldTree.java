package internal.tree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.Line;

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
		if(stringRepresentation.size() == 0)
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
				if(stringList.size() > lineIndex)
					fullLine.append(stringList.get(lineIndex));
			}
			returnString.append(fullLine.toString() + "\n");
//			if(!stringRepresentation.contains(fullLine.toString()))
				stringRepresentation.add(fullLine.toString());
		}
		
//		We have obtained every line as we should. We now need to own everything that is within is.
		prepareToString();
		
	}
	
	public void prepareToString() {
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
		
		stringRepresentation = newStringRepresentation;
	}
}
