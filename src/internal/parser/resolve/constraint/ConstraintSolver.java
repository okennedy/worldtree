package internal.parser.resolve.constraint;

import development.com.collection.range.RangeSet;
import internal.Helper.Hierarchy;
import internal.parser.containers.Constraint;
import internal.parser.containers.property.Property;
import internal.parser.containers.property.PropertyDef;
import internal.tree.IWorldTree;

public class ConstraintSolver {
	private static IConstraintSolver solver = new BasicSolver();
	
	public static void pushDownConstraints(IWorldTree node) {
		validateDefinitions(node);
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
	
	private static void validateDefinitions(IWorldTree node) {
		IWorldTree root = node.root();
		for(Constraint constraint : root.constraints()) {
			Property constraintProperty	= constraint.condition().property();
			Hierarchy constraintLevel	= constraint.level();
			Hierarchy currentLevel		= constraintLevel;
			while(currentLevel != null) {
				boolean validateDefinitions	= false;
				for(PropertyDef def : root.definitions()) {
					if(constraintProperty.equals(def.property()) && currentLevel.equals(def.level())) {
						validateDefinitions	= true;
						break;
					}
				}
				if(!validateDefinitions) {
					throw new IllegalStateException("Constraint '" + constraint + "' has no definition at " + currentLevel + " level");
				}
				currentLevel = currentLevel.childLevel();
			}
		}
	}
}
