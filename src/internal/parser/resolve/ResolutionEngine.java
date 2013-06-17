package internal.parser.resolve;

import java.util.ArrayList;
import java.util.List;

import internal.containers.Relation;
import internal.containers.pattern.IPattern;
import internal.containers.query.IQuery;
import internal.tree.IWorldTree;

public class ResolutionEngine {
	
//	TODO Figure out a way to make these work without being bound to the Tile-level
	public static String resolve(IWorldTree node, IQuery query) {
		Class<?> level		= query.level();
		IPattern pattern	= query.pattern();
		while(pattern != null) {
			resolve(node, level, pattern);
			pattern = pattern.subPattern();
		}
		return null;
	}

	private static void resolve(IWorldTree node, Class<?> level, IPattern pattern) {
		List<IWorldTree> nodeList   = new ArrayList<IWorldTree>();
		List<IWorldTree> objectList = new ArrayList<IWorldTree>();
		nodeList.add(node);
		
//		Get collection of relevant objects
		IWorldTree currentNode = null;
		while(nodeList.size() > 0) {
			currentNode = nodeList.get(0);
			for(IWorldTree child : currentNode.children()) {
				if(child.getClass().equals(level))
					objectList.add(child);
				else
					nodeList.add(child);
			}
		}
		
		Relation relation = pattern.relation();
	}
}
