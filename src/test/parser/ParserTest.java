package test.parser;

import static org.junit.Assert.*;

import internal.parser.ParseException;
import internal.parser.Parser;
import internal.parser.containers.IStatement;
import internal.parser.containers.query.IQuery;
import internal.parser.resolve.ResolutionEngine;
import internal.parser.resolve.Result;
import internal.piece.PieceFactory;
import internal.tree.IWorldTree.IMap;
import internal.tree.WorldTreeFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.junit.BeforeClass;
import org.junit.Test;

import static internal.Helper.write;
import static internal.Helper.makeString;

public class ParserTest {
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
			PieceFactory.initialize(pieceStrings);
			factory = new WorldTreeFactory();
			map = factory.newMap("TestMap", null);
			map.fullInit();
			write(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Test
	public void queryResolutionTest() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			
			StringBuffer command = new StringBuffer();
			while(true) {
				String cmd = in.readLine();
				
				if(cmd.equalsIgnoreCase("quit") || cmd.equalsIgnoreCase("exit"))
					break;
				
				command.append(cmd);
				if(command.toString().contains(";")) {
					Parser parser = new Parser(new StringReader(command.toString()));
					IQuery query = (IQuery) parser.parse();
					System.out.println(query.debugString());
					Result result = ResolutionEngine.evaluate(map, query);
					System.out.println(result);
					write("query", makeString(query, result));
					command.delete(0, command.length());
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
			fail();
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void basicIOTest() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			
			StringBuffer command = new StringBuffer();
			while(true) {
				String cmd = in.readLine();
				
				if(cmd.equalsIgnoreCase("quit") || cmd.equalsIgnoreCase("exit"))
					break;
				
				command.append(cmd);
				if(command.toString().contains(";")) {
					Parser parser = new Parser(new StringReader(command.toString()));
					IStatement o = parser.parse();
					System.out.println(o.debugString());
					command.delete(0, command.length());
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
			fail();
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}
}
