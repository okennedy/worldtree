package internal.parser.resolve.constraint;

import development.com.collection.range.RangeSet;
import internal.parser.containers.Constraint;
import internal.parser.containers.property.PropertyDef;
import internal.tree.IWorldTree;

public interface IConstraintSolver {
	public void pushDownConstraints(IWorldTree node);
	public RangeSet getBounds(IWorldTree node, PropertyDef definition);
	public void initializeBounds(IWorldTree node);
	public RangeSet processBounds(IWorldTree node, Constraint constraint);
}
