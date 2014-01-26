package internal.parser.resolve.constraint;

import development.com.collection.range.RangeSet;
import internal.parser.containers.Constraint;
import internal.parser.containers.property.PropertyDef;
import internal.tree.IWorldTree;

public class ConstraintSolver {
	private static IConstraintSolver solver = new BasicSolver();
	
	public static void pushDownConstraints(IWorldTree node) {
		solver.pushDownConstraints(node);
	}
	
	public static RangeSet getBounds(IWorldTree node, PropertyDef definition) {
		return solver.getBounds(node, definition);
	}
	
	public static void initializeBounds(IWorldTree node) {
		solver.initializeBounds(node);
	}
	
	public static RangeSet processBounds(IWorldTree node, Constraint constraint) {
		return solver.processBounds(node, constraint);
	}
}
