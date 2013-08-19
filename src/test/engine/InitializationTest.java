package test.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import static internal.Helper.*;

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

import static org.junit.Assert.fail;

public class InitializationTest {
	private IMap map;
	private WorldTreeFactory factory = null;
	
	@BeforeClass
	public static void setUp() {
		try {
			PieceFactory.initialize(pieceStrings);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Test
	public void materializationTest() {
		factory = new WorldTreeFactory("init.properties", "world.definitions");
		map = factory.newMap("InitTestMap", null);
		map.initRooms();
		map.initRegions();
		map.initTiles();
		
		map.materializeConstraints();
		System.out.println();
		
	}
	
	@Test
	public void materializationAndQueryTest() {
		factory = new WorldTreeFactory("init.properties", "world.definitions");
		map = factory.newMap("InitTestMap", null);
		map.initRooms();
		map.initRegions();
		map.initTiles();
		
		map.materializeConstraints();
		
		
		map.fill();
		
		write(map);
		
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
					write("query", makeStringFromResult(query, result));
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