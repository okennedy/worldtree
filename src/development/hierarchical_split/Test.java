package development.hierarchical_split;

import java.util.HashMap;
import java.util.Map;

import development.com.collection.range.Range;

import internal.Helper;
import internal.Helper.Hierarchy;
import internal.parser.containers.Constraint;
import internal.parser.containers.Datum;
import internal.parser.containers.property.PropertyDef;
import internal.parser.containers.property.PropertyDef.RandomSpec;
import internal.piece.PieceFactory;
import internal.tree.IWorldTree;
import internal.tree.IWorldTree.IMap;
import internal.tree.WorldTreeFactory;

public class Test {

	public static void main(String[] args) {
		try {
			PieceFactory.initialize(Helper.pieceStrings);
		} catch (Exception e) {
			e.printStackTrace();
		}
		WorldTreeFactory factory = new WorldTreeFactory("init.properties", "world.definitions");
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
		
		
		Map<IWorldTree, Range> childRanges = new HashMap<IWorldTree, Range>();
		
		for(IWorldTree child : map.children()) {
			RandomSpec bound = child.getBounds(definition);
			childRanges.put(child, bound.range());
		}
		Map<IWorldTree, Datum> split = HierarchicalSplit.split(map, constraint, definition);
		
		for(Map.Entry<IWorldTree, Datum> entry : split.entrySet()) {
			System.out.println("Range of child :" + childRanges.get(entry.getKey()));
			System.out.println("Chosen value   :" + entry.getValue());
			System.out.println();
		}
	}
}
