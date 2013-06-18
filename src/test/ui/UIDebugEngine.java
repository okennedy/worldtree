package test.ui;

import static org.junit.Assert.*;

import internal.piece.PieceFactory;
import internal.tree.IWorldTree;
import internal.tree.WorldTreeFactory;
import internal.tree.IWorldTree.IMap;
import internal.tree.IWorldTree.IRegion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class UIDebugEngine {
	private static IMap map;
	private static WorldTreeFactory factory;
	private static String[] pieceStrings = {
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
		""
	};
	
	@BeforeClass
	public static void setUp() {
		try {
			new PieceFactory(pieceStrings);
			WorldTreeFactory factory = new WorldTreeFactory();
			map = factory.newMap("TestMap", null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void movementTest() {
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new InputStreamReader(System.in));
			String command;
			map.initialize();	//Map initialized
			map.initialize();	//Rooms initialized
			IRegion child = (IRegion)((IWorldTree) map.children().toArray()[0]).children().toArray()[0];	//Region0
			while(true) {
				command = in.readLine();
				UIDebugParser testParser = new UIDebugParser(new StringReader(command));
				try {
					testParser.parse(child);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
					fail();
				}
			}
		}
	}
	
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
	
	public static void write(String string) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(new File("output/output.txt")));
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
//		Verify that all Strings are of equal number of lines
		int size = listStringList.get(0).size();
		for(List<String> list : listStringList)
			assert(list.size() == size);
		
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
				line.append(pad(list.get(lineIndex), maxLength));
			}
			result.append(line + "\n");
		}
		return result.toString();
	}
	
	public static String pad(String string, int length) {
		StringBuffer result = new StringBuffer(string);
		assert(result.length() <= length);
		while(result.length() <= length)
			result.append(" ");
		
		return result.toString();
	}
}
