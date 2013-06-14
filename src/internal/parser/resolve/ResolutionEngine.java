package internal.parser.resolve;

import internal.containers.pattern.IPattern;
import internal.containers.query.IQuery;
import internal.tree.IWorldTree;

public class ResolutionEngine {
	
//	TODO Figure out a way to make these work without being bound to the Tile-level
	public static String resolve(IWorldTree node, IQuery query) {
		
		IPattern pattern 	= query.pattern();
		while(pattern != null) {
			resolve(pattern);
			pattern = pattern.subPattern();
		}
		return null;
	}

	private static void resolve(IPattern pattern) {
		
	}
	
	
	
	
	
	private static boolean toeast(IWorldTree r1, IWorldTree r2) {
		
		return false;
	}
}
