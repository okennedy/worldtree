package test.engine;

import static org.junit.Assert.*;

import java.io.StringReader;

import internal.parser.ParseException;
import internal.parser.Parser;
import internal.parser.containers.Reference;
import internal.parser.containers.Relation;
import internal.parser.containers.pattern.BasePattern;
import internal.parser.containers.query.BaseQuery;
import internal.parser.containers.query.IQuery;
import internal.parser.resolve.ResolutionEngine;
import internal.piece.PieceFactory;
import internal.tree.IWorldTree.IMap;
import internal.tree.WorldTreeFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import static test.ui.UIDebugEngine.write;

public class EngineTest {
	private IMap map;
	private WorldTreeFactory factory = new WorldTreeFactory();
	private Parser parser			 = null;
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
		""
	};
	
	@BeforeClass
	public void setUp() {
		try {
			new PieceFactory(pieceStrings);
			map = factory.newMap("Map", null, null);
			map.fullInit();
			write(map);
			parser = new Parser(new StringReader(""));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void inbuiltPropertiesTest() {
//		Direction tests
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
