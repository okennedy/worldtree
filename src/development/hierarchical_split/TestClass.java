package development.hierarchical_split;

import static org.junit.Assert.*;
import internal.Helper;
import internal.Helper.Hierarchy;
import internal.parser.containers.Constraint;
import internal.parser.containers.Datum;
import internal.parser.containers.property.PropertyDef;
import internal.parser.containers.property.PropertyDef.RandomSpec;
import internal.piece.PieceFactory;
import internal.tree.IWorldTree;
import internal.tree.WorldTreeFactory;
import internal.tree.IWorldTree.IMap;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import development.com.collection.range.Range;
import development.com.collection.range.RangeSet;

import static internal.Helper.*;

public class TestClass {
	public static WorldTreeFactory factory = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PieceFactory.initialize(Helper.pieceStrings);
		factory = new WorldTreeFactory("init.properties", "world.definitions");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void preliminaryTest() {
		IMap map = factory.newMap("TestMap", null);
		map.initRooms();
		map.initRegions();
		map.initTiles();

		Hierarchy level	= Hierarchy.parse(map.getClass());
		
		Constraint constraint 	= null;
		PropertyDef definition	= null;
		for(Constraint c : map.constraints()) {
			if(c.level().equals(level))
				constraint = c;
		}
		
		for(PropertyDef def : map.definitions()) {
			if(def.property().name().equals(constraint.condition().property().name()) &&
					def.level().equals(level))
				definition	= def;
		}
		
		
		Map<IWorldTree, RangeSet> childRanges = new HashMap<IWorldTree, RangeSet>();
		
		for(IWorldTree child : map.children()) {
			RangeSet bounds = child.getBounds(definition);
			childRanges.put(child, bounds);
		}
		Map<IWorldTree, Datum> split = HierarchicalSplit.split(map, constraint, definition);
		
		for(Map.Entry<IWorldTree, Datum> entry : split.entrySet()) {
			System.out.println("Range of child :" + childRanges.get(entry.getKey()));
			System.out.println("Chosen value   :" + entry.getValue());
			System.out.println();
		}
	}
	
	@Test
	public void timingTest() {
		IMap map = factory.newMap("TestMap", null);
		map.initRooms();
		map.initRegions();
		map.initTiles();

		long startTime	= System.nanoTime();
		map.materializeConstraints();
		long endTime	= System.nanoTime();
		
		System.out.println("Time taken  :" + String.format("%.4f", ((endTime - startTime) / 1e9)) + "seconds");
//		write(map);
		
	}
}
