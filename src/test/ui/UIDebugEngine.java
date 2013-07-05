package test.ui;

import static org.junit.Assert.*;

import static internal.Helper.*;

import internal.piece.PieceFactory;
import internal.tree.IWorldTree;
import internal.tree.WorldTreeFactory;
import internal.tree.IWorldTree.IMap;
import internal.tree.IWorldTree.IRegion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
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
			factory = new WorldTreeFactory();
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
			write(map);
			
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
}
