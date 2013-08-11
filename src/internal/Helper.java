package internal;

import internal.parser.containers.IStatement;
import internal.parser.resolve.Column;
import internal.parser.resolve.Result;
import internal.tree.IWorldTree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class contains all helper methods
 * @author Guru
 *
 */
public class Helper {

	/**
	 * Write an {@code IWorldTree} object to file {@code 'output/output.txt'}
	 * @param object {@code IWorldTree} object to write
	 */
	public static void write(IWorldTree object) {
		List<IWorldTree> nodes = new ArrayList<IWorldTree>();
		nodes.add(object);
		while(nodes.size() > 0) {
			IWorldTree node = nodes.get(0);
			if(node.children() != null) {
				for(IWorldTree child : node.children()) {
					nodes.add(child);
				}
				node.getStringRepresentation();
			}
			nodes.remove(node);
		}
		
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(new File("output/output.txt")));
			out.write(object.toString());
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Write a particular {@code String} to a file
	 * @param fileName {@code String} representing the name of the file
	 * @param string {@code String} containing the value to write
	 */
	public static void write(String fileName, String string) {
		BufferedWriter out = null;
		try {
			if(!fileName.contains(".txt") && !fileName.substring(fileName.length() - 4).equalsIgnoreCase(".txt"))
				fileName += ".txt";
			out = new BufferedWriter(new FileWriter(new File("output/" + fileName)));
			out.write(string);
			out.newLine();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Converts multiple multi-line visuals into a single string representation
	 * @param listStringList {@code List<String>} containing the set of multi-line visuals to compact
	 */
	public static String multiLine(List<String> stringList) {
		StringBuffer result = new StringBuffer();
//		Convert each String into its lines
		List<List<String>> listStringList = new ArrayList<List<String>>();
		for(String string : stringList)
			listStringList.add(Arrays.asList(string.split("\n")));

//		Size is now determined as the size of the largest list
		int size = -1;
		for(List<String> list : listStringList)
			size = list.size() > size ? list.size() : size;
		
//		Throw warning in case of unequal list sizes
//		for(List<String> list : listStringList)
//			if(list.size() != size)
//				System.err.println("Warning: Strings have unequal number of lines in multiLine\n");
		
//		Find the largest single String
		int maxLength = 0;
		for(List<String> list : listStringList) {
			for(String s : list) {
				maxLength = s.length() > maxLength ? s.length() : maxLength;
			}
		}
		
//		Do the conversion
		for(int lineIndex = 0; lineIndex < size; lineIndex++) {
			StringBuffer line = new StringBuffer();
			for(List<String> list : listStringList) {
				String string = null;
				try {
					string = list.get(lineIndex);
				} catch(IndexOutOfBoundsException e) 
				{
					string = "";
				} finally {
					line.append(pad(string, maxLength));
				}
			}
			result.append(line + "\n");
		}
		return result.toString();
	}
	
	/**
	 * Pad a given string to ensure that it is of a certain length <br>
	 * Padding simply adds trailing spaces to the string  
	 * @param string {@code String} to pad
	 * @param length {@code int} required length
	 * @return {@code String} representing the padded form of <b> string </b>
	 */
	public static String pad(String string, int length) {
		StringBuffer result = new StringBuffer(string);
		assert(result.length() <= length);
		while(result.length() <= length)
			result.append(" ");
		
		return result.toString();
	}
	
	/**
	 * Helper method used to convert {@code Result} to a {@code String}
	 * @param statement {@code IStatement} representing the statement that is being evaluated
	 * @param result {@code Result} representing the collection that needs to be flattened
	 * @return {@code String} representing the flattened version of the parameter <b>result</b>
	 */
	public static String makeString(IStatement statement, Result result) {
		StringBuffer sb = new StringBuffer(statement.toString() + "\n" + statement.debugString() + "\n\n");
		
		int rowIndex = 0;
		while(rowIndex < result.get(0).size()) {
			List<String> stringList 	= new ArrayList<String>();
			for(Column t : result) {
				IWorldTree obj 			= t.get(rowIndex);
				StringBuffer visual 	= new StringBuffer(t.name() + "\n" + obj.absoluteName() + "  \n");
				List<String> stringRep	= t.get(rowIndex).getStringRepresentation();
				for(String line : stringRep) {
					visual.append(line + "\n");
				}
				stringList.add(visual.toString());
			}
			String multiline = multiLine(stringList);
			sb.append(multiline + "\n\n");
			
			rowIndex++;
		}
		return sb.toString();
	}
	
	public static String titleCase(String string) {
		StringBuffer sb = new StringBuffer();
		sb.append((string.substring(0, 1).toUpperCase()));
		sb.append(string.substring(1).toLowerCase());
		return sb.toString();
	}
	
	public enum Hierarchy {
		Map("Map"),
		Room("Room"),
		Region("Region"),
		Tile("Tile"),
		;
		
		private String level;
		
		private Hierarchy(String level) {
			this.level	= level;
		}
		
		public static Hierarchy childLevel(String level) {
			Hierarchy h = parse(level);
			
			switch(h) {
			case Map:
				return Room;
			case Region:
				return Tile;
			case Room:
				return Region;
			case Tile:
				return null;
			default:
				break;
			}
			return null;
		}
		
		public static Hierarchy parentLevel(String level) {
			Hierarchy h = parse(level);
			
			switch(h) {
			case Map:
				return null;
			case Region:
				return Room;
			case Room:
				return Map;
			case Tile:
				return Region;
			default:
				break;
			}
			return null;
		}
		
		public static Hierarchy parse(String level) {
			for(Hierarchy h : values()) {
				if(h.level.equalsIgnoreCase(level))
					return h;
			}
			throw new IllegalArgumentException(level + " is not a valid Hierarchy level!\n" +
					"Valid levels are :" + values());
		}
		
		@Override
		public String toString() {
			return level;
		}
	}
}
