package test.engine;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Arrays;

import static internal.Helper.multiLine;

import internal.parser.ParseException;
import internal.parser.Parser;
import internal.parser.containers.Reference;
import internal.parser.containers.Relation;
import internal.parser.containers.pattern.BasePattern;
import internal.parser.containers.query.BaseQuery;
import internal.parser.containers.query.IQuery;
import internal.parser.resolve.ResolutionEngine;
import internal.parser.resolve.Result;
import internal.piece.PieceFactory;
import internal.tree.IWorldTree.IMap;
import internal.tree.WorldTreeFactory;

import org.junit.BeforeClass;
import org.junit.Test;

public class QueryTest {
	private IMap map;
	private WorldTreeFactory factory = null;
	private Parser parser			 = null;
	private String dirPath		 = "src/test/engine/query tests/";
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
	public void setUp() {
		try {
			new PieceFactory(pieceStrings);
			parser = new Parser(new StringReader(""));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Test
	public void inbuiltPropertiesTest() {
//		Direction tests
		factory = new WorldTreeFactory("src/test/engine/query tests/inbuilt properties/init.properties");
		map = factory.newMap("InbuiltPropertiesTestMap", null, null);
		
		String string = "A toeast B";
		parser.ReInit(new StringReader(string));
		try {
			IQuery query = parser.query();
			System.out.println(ResolutionEngine.evaluate(map, query));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
