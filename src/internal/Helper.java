package internal;

import internal.parser.containers.IStatement;
import internal.parser.resolve.Column;
import internal.parser.resolve.Result;
import internal.tree.IWorldTree;
import internal.parser.containers.Constraint;
import internal.parser.containers.property.Property;
import internal.parser.containers.Datum;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Helper class contains all helper methods
 * @author Guru
 *
 */
public class Helper {

	public static String[] pieceStrings = {
		"LR",
		"UD",
		"UL",
		"L",
		"U",
		"D",
		"R",
		"UDLR",
		"UDL",
		"UDR",
		"ULR",
		"DLR",
		"UR",
		"DR",
		"DL",
	};
	
	
	/**
	 * Write an {@code IWorldTree} object to file {@code 'output/output.txt'}
	 * @param object {@code IWorldTree} object to write
	 */
	public static void write(IWorldTree object) {
    write(object, "output/output.txt");
  }
	/**
	 * Write an {@code IWorldTree} object to the specified file
	 * @param object {@code IWorldTree} object to write
	 * @param output {@code String} filename to write to ('-' for stdout)
	 */
	public static void write(IWorldTree object, String output) {
		Logger out = null;
		try {
		  out = Logger.writerFor(output);
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
			out = Logger.writerFor(("output/" + fileName));
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
	 * Dump the properties and constraints of a particular {@code WorldTree} node 
	 * and all of its children to a file
	 * @param {@code IWorldTree} node The root of the hierarchy to dump
	 * @param {@code String} filename The file to dump to
   */
	public static void writeProperties(IWorldTree node, String output)
	{
    Logger out = null;
    try {
      out = Logger.writerFor(output);
      writeProperties(node, out, "");
    } catch(IOException e){
      e.printStackTrace();
    } finally {
      try {
        if(out != null){ out.close(); }
      } catch(IOException e){
        e.printStackTrace();
      }
    }
	}
	
	/**
	 * Dump the properties and constraints of a particular {@code WorldTree} node 
	 * and all of its children to a file.  
	 *
	 * Internal function for recursing.
	 * @param {@code IWorldTree} node The current node of the hierarchy
	 * @param {@code BufferedWriter} out output stream to dump to
	 * @param {@code String} indent indentation prefix
   */
	protected static void writeProperties(IWorldTree node, BufferedWriter out, 
	                                      String indent)
    throws IOException
  {
    out.write(indent+"=== " + node.name() + "===\n");
    for(Constraint c : node.constraints()){
      out.write(indent+"  >  "+c.toString()+"\n");
    }
    for(Map.Entry<Property, Datum> p : node.properties().entrySet()){
      out.write(indent+"  >  "+p.getKey()+" : "+p.getValue()+"\n");
    }
    if(node.children() != null){
      for(IWorldTree child : node.children()){
        writeProperties(child, out, indent+"   ");
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
	public static String makeStringFromResult(IStatement statement, Result result) {
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
	
	/**
	 * Hierarchy is an enum that is used to obtain parent and child levels relative to a given level in the Hierarchy
	 * @author Guru
	 *
	 */
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
		
		/**
		 * Obtain the child level of this Hierarchy (level)
		 * @return {@code Hierarchy} representing the child level
		 */
		public Hierarchy childLevel() {
			switch(this) {
			case Map:
				return Room;
			case Region:
				return Tile;
			case Room:
				return Region;
			case Tile:
				return null;
			default:
				return null;
			}
		}
		
		/**
		 * Obtain the parent level of this Hierarchy (level)
		 * @return {@code Hierarchy} representing the parent level
		 */
		public Hierarchy parentLevel() {
			switch(this) {
			case Map:
				return null;
			case Region:
				return Room;
			case Room:
				return Map;
			case Tile:
				return Region;
			default:
				return null;
			}
		}
		
		/**
		 * Parse a {@code String} and obtain its corresponding {@code Hierarchy} enum
		 * @param level {@code String} containing the hierarchy level
		 * @return {@code Hierarchy} corresponding to the specified <b>level</b> <br>
		 * <b>null</b> if the specified level does not exist
		 */
		public static Hierarchy parse(String level) {
			for(Hierarchy h : values()) {
				if(h.level.equalsIgnoreCase(level))
					return h;
			}
			throw new IllegalArgumentException(level + " is not a valid Hierarchy level!\n" +
					"Valid levels are :" + values());
		}
		
		public static Hierarchy parse(Class<?> clazz) {
			String level = clazz.getSimpleName();
			return parse(level);
		}
		
		@Override
		public String toString() {
			return level;
		}

		/**
		 * Obtain the class corresponding to this level
		 * @return {@code Class<?>}
		 */
		public Class<?> HierarchyClass() {
			try {
				return Class.forName("internal.tree.WorldTreeFactory$" + level);
			} catch (ClassNotFoundException e) {
				System.err.println(e.getMessage());
			}
			return null;
		}
	}
	
	public static void fileCopy(File source, File destination, boolean replace, boolean verify) {
		if(!replace) {
			if(destination.exists())
				throw new IllegalStateException("Cannot perform fileCopy! Destination file already exists\n");
		}
		else {
			if(destination.exists())
				destination.delete();
		}
		try {
			OutputStream output 	= new FileOutputStream(destination);
			InputStream input 		= new FileInputStream(source);
			byte[] bytes = new byte[1024 * 1024];
			int length = -1;
			while((length = input.read(bytes)) > -1)
				output.write(bytes, 0, length);
			
			input.close();
			output.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		assert source.length() == destination.length();
		if(verify) {
			long length = source.length();
			try {
				InputStream src		= new FileInputStream(source);
				InputStream dest	= new FileInputStream(destination);
				int byte1, byte2;
				
				for(int byteIndex = 0; byteIndex < length; byteIndex++) {
					byte1 	= src.read();
					byte2	= dest.read();
					assert byte1 == byte2 : "File copy failed! source and destination do not match!\n";
				}
				
				src.close();
				dest.close();
			} catch(IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public static class Logger extends BufferedWriter {
//		We need a way to distinguish stdout from other file names.
//		We use a filename that is guaranteed to be invalid.
		public static final String STDOUT = "\0";
		private String path;

		private Logger(String path, Writer out) {
			super(out);
			this.path = path;
		}

		/**
		 * Get a buffered writer for a specified path.
		 * @param path {@code String} path to get a writer for ('-' means stdout)
		 * @returns {@code BufferedWriter} writer for path.
		 */
		public static Logger writerFor(String path)
	    throws IOException
		{
		 Writer out;
	    if(path.equals(STDOUT)){
	      out = new OutputStreamWriter(System.out);
	    } else {
	      out = new FileWriter(new File(path), true);
	    }
	    return new Logger(path, out);
		}

		@Override
		public void close() throws IOException {
			if(this.path.equals(STDOUT)) {
				this.flush();
//				We don't close stdout as we didn't open it..
				return;
			}
			else
				super.close();
		}
		
		public static boolean eraseFile(String path) {
			File file = new File(path);
			if(file.exists())
				return file.delete();
			return true;
		}
	}
}
