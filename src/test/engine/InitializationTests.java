package test.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static internal.Helper.write;

import internal.parser.ParseException;
import internal.parser.Parser;
import internal.parser.containers.query.IQuery;
import internal.parser.resolve.ResolutionEngine;
import internal.parser.resolve.Result;
import internal.piece.PieceFactory;
import internal.tree.IWorldTree.IMap;
import internal.tree.WorldTreeFactory;

import org.junit.BeforeClass;
import org.junit.Test;
import static internal.Helper.multiLine;

public class InitializationTests {
	private IMap map;
	private WorldTreeFactory factory = null;
	private Parser parser			 = new Parser(new StringReader(""));
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Test
	public void test() {
		factory = new WorldTreeFactory("init.properties", "world.definitions");
		map = factory.newMap("InitTestMap", null);
		map.initRooms();
		map.initRegions();
		map.initTiles();
		
		map.materializeConstraints();
		System.out.println();
	}
}