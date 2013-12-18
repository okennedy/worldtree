package test.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Random;
import java.util.Scanner;

import static internal.Helper.*;
import internal.parser.ParseException;
import internal.parser.Parser;
import internal.parser.TokenCmpOp;
import internal.parser.containers.Constraint;
import internal.parser.containers.Datum;
import internal.parser.containers.Reference;
import internal.parser.containers.Constraint.Type;
import internal.parser.containers.condition.BaseCondition;
import internal.parser.containers.condition.ICondition;
import internal.parser.containers.condition.BaseCondition.ConditionType;
import internal.parser.containers.pattern.BasePattern;
import internal.parser.containers.pattern.IPattern;
import internal.parser.containers.property.Property;
import internal.parser.containers.query.BaseQuery;
import internal.parser.containers.query.IQuery;
import internal.parser.resolve.ResolutionEngine;
import internal.parser.resolve.Result;
import internal.piece.PieceFactory;
import internal.tree.IWorldTree;
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

		int maxTreasure = 32;
		int runs = 100;
		
		for(int testIndex = 0; testIndex < runs; testIndex++) {
			Constraint constraint = null;
			int treasure			= new Random().nextInt(maxTreasure);
			{
				Datum value				= new Datum.Int(treasure);
				IPattern pattern		= new BasePattern(new Reference("this"), null, null);
				Property childProperty	= new Property(new Reference("this"), "treasure");
				ICondition condition	= new BaseCondition(false, ConditionType.BASIC, childProperty, TokenCmpOp.EQ, value);
				IQuery query = new BaseQuery(Hierarchy.Map, pattern, null);
				constraint = new Constraint(Type.PROGRAM_GENERATED, Hierarchy.Map, query, condition);
			}
			map = factory.newMap("InitTestMap", null);
			
			map.initRooms();
			map.initRegions();
			map.initTiles();
			map.fill();
			map.addConstraint(constraint);
		
			map.materializeConstraints();
			write(map);
			
			Collection<IWorldTree> tiles = map.getNodesByLevel(Hierarchy.Tile);
			int sum = 0;
			
//			Check values
			for(IWorldTree tile : tiles) {
				Datum value = tile.properties().get("treasure");
				if(value != null) {
					sum = sum + ((Integer) value.toInt().data());
				}
			}
			assert sum == treasure : "Map constraint was not satisfied!\n"
					+ "Expected :" + treasure + "\n"
					+ "Obtained :" + sum;
			System.out.println("Finished run :" + testIndex);
		}
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