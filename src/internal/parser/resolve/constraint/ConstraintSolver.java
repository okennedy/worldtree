package internal.parser.resolve.constraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import development.com.collection.range.RangeSet;
import internal.Helper.Hierarchy;
import internal.parser.containers.Constraint;
import internal.parser.containers.condition.ICondition;
import internal.parser.containers.expr.Expr;
import internal.parser.containers.expr.IExpr;
import internal.parser.containers.property.Property;
import internal.parser.containers.property.PropertyDef;
import internal.parser.containers.query.IQuery;
import internal.tree.IWorldTree;

public class ConstraintSolver {
	private static IConstraintSolver solver = null;
	private static Map<Hierarchy, Map<Property, PropertyDef>> propertyDefMap = 
			new HashMap<Hierarchy, Map<Property, PropertyDef>>();
	private static Map<Hierarchy, Map<Property, Collection<Property>>> propertyDependencyMap = 
			new HashMap<Hierarchy, Map<Property, Collection<Property>>>();
	private static Map<Property, Collection<Property>> relatedPropertiesMap = 
			new HashMap<Property, Collection<Property>>();
	
	public static void pushDownConstraints(IWorldTree node) {
		resolveDefinitionDependencies(node);
		validateDefinitions(node);
		sortDefinitions(node);
		resolveRelatedProperties(node);
		solver = new BasicSolver(propertyDependencyMap, propertyDefMap, relatedPropertiesMap);
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
	
	/**
	 * Ensures that all constraints have valid definitions.
	 * This method ensures that each constraint property has a set of definitions defined at all levels
	 * @param node {@code IWorldTree} to obtain definitions from
	 * @throws IllegalStateException if a constraint does not have a valid set of definitions
	 */
	private static void validateDefinitions(IWorldTree node) {
		IWorldTree root = node.root();
		
		for(Constraint constraint : root.constraints()) {
			Property constraintProperty	= constraint.condition().property();
			Hierarchy constraintLevel	= constraint.level();
			try {
				validateDefinition(constraintLevel, propertyDefMap, constraintProperty);
			} catch(IllegalStateException e) {
				throw new IllegalStateException("Constraint '" + constraint + "' is not fully defined. " + e.getMessage());
			}
		}
	}
	
	/**
	 * Recursive method of validating a particular definition. <br>
	 * It works by forming a collection of dependencies and then resolving each of those.
	 * @param level {@code Hierarchy}
	 * @param propertyDefMap {@code Map<Hierarchy, Map<Property, PropertyDef>>}
	 * @param property {@code Property}
	 * @throws IllegalStateException if there is no definition for a given property at the specified level
	 */
	private static void validateDefinition(Hierarchy level, 
			Map<Hierarchy, Map<Property, PropertyDef>> propertyDefMap, Property property) throws IllegalStateException {

		PropertyDef definition				= propertyDefMap.get(level).get(property);
		if(definition == null)
			throw new IllegalStateException("'" + property + "' has no definition at " + level + " level");
		
		Collection<Property> dependencies 	= new HashSet<Property>();
		
		switch(definition.type()) {
		case AGGREGATE:
			IExpr expr = definition.aggregateExpression().expr();
			while(expr != null) {
				if(expr.property() != null)
					dependencies.add(expr.property());
				expr = expr.subExpr();
			}
			level = level.childLevel();
			break;
		case BASIC:
			if(definition.expression() != null) {
				expr = definition.expression();
				while(expr != null) {
					if(expr.property() != null)
						dependencies.add(expr.property());
					expr = expr.subExpr();
				}
			}
			else if(definition.condition() != null) {
				ICondition condition = definition.condition();
				while(condition != null) {
					if(condition.property() != null)
						dependencies.add(condition.property());
					condition = condition.subCondition();
				}
			}
			break;
		case RANDOM:
			break;
		case INHERIT:
//			TODO: Implement this
		default:
			throw new IllegalStateException("Unimplemented definition type " + definition.type());
		}
		
		for(Property p : dependencies) {
			validateDefinition(level, propertyDefMap, p);
		}		
	}
	
	/**
	 * Sort definitions based on dependencies
	 * @param root {@code IWorldTree} node containing definitions
	 */
	private static void sortDefinitions(IWorldTree root) {
//		Now that we have the dependencies-per-level, we iterate over each level and resolve property order
		Collection<PropertyDef> definitions = root.definitions();
		List<PropertyDef> orderedDefinitions = new ArrayList<PropertyDef>();
		
		for(Hierarchy level : Hierarchy.values()) {
			List<Property> levelOrderedProperties = new ArrayList<Property>();
			resolvePropertyOrder(propertyDependencyMap.get(level), null, null, levelOrderedProperties);
//			We now have the order in which definitions need to be instantiated
			for(Property property: levelOrderedProperties) {
				Map<Property, PropertyDef> localDefMap = propertyDefMap.get(level);
				orderedDefinitions.add(localDefMap.get(property));
			}
		}
//		At this point, we have an ordered list of definitions
		assert(definitions.size() == orderedDefinitions.size() && definitions.containsAll(orderedDefinitions)) : 
			"definitions and orderedDefinitions are not equivalent!\n";
		root.setDefinitions(orderedDefinitions);
	}
	
	/**
	 * Resolve dependencies among definitions
	 * @param root {@code IWorldTree} node containing definitions
	 * @param propertyDependencyMap {@code Map<Hierarchy, Map<Property, Collection<Property>>} to be filled with the dependency chain
	 * @param propertyDefMap {@code Map<Hierarchy, Map<Property, PropertyDef>>} optional. 
	 * Is used to obtain mapping of property -> property definition in each hierarchy level
	 */
	private static void resolveDefinitionDependencies(IWorldTree root) {
		Collection<PropertyDef> definitions = root.definitions();
		
//		We now find dependencies among property definitions
		for(Hierarchy level : Hierarchy.values()) {
			HashMap<Property, Collection<Property>> levelDependencyMap = new HashMap<Property, Collection<Property>>();
			HashMap<Property, PropertyDef> levelPropertyDefMap = new HashMap<Property, PropertyDef>();
			for(PropertyDef definition : definitions) {
				if(!definition.level().equals(level))
					continue;
				Property baseProperty = definition.property();
				levelPropertyDefMap.put(baseProperty, definition);
				Set<Property> dependencies = new HashSet<Property>();	//TODO: Figure out whether we care about the order here
				switch(definition.type()) {
				case AGGREGATE:
//					While these properties are not needed as per-level definition dependencies, they are still part of related properties
//					So add them to the related properties map
					IExpr expr = definition.aggregateExpression().expr();
					while(expr != null ){
						if(expr.property() != null) {
							if(!relatedPropertiesMap.containsKey(baseProperty))
								relatedPropertiesMap.put(baseProperty, new HashSet<Property>());
//							Add self
							relatedPropertiesMap.get(baseProperty).add(baseProperty);
							relatedPropertiesMap.get(baseProperty).add(expr.property());
						}
						expr = expr.subExpr();
					}
					break;
				case BASIC:
					if(definition.expression() != null) {
						expr = definition.expression();
						while(expr != null) {
							if(expr.property() != null)
								dependencies.add(expr.property());
							expr = expr.subExpr();
						}
					}
					else if(definition.condition() != null) {
						ICondition condition = definition.condition();
						while(condition != null) {
							if(condition.property() != null)
								dependencies.add(condition.property());
							condition = condition.subCondition();
						}
					}
					break;
				case RANDOM:
					break;
				case INHERIT:
//					TODO: Need to implement this
				default:
					throw new IllegalStateException("Unimplemented PropertyDef type " + definition.type());
				}
//				Handle the query
				IQuery query = definition.query();
				if(query != null) {
					while(query != null) {
						if(query.level().equals(definition.level())) {
							ICondition queryCondition = query.condition();
							while(queryCondition != null) {
								if(queryCondition.property() != null)
									dependencies.add(queryCondition.property());
								queryCondition = queryCondition.subCondition();
							}
						}
						query = query.subQuery();
					}
				}
				
//				We now have all the dependencies
				levelDependencyMap.put(baseProperty, dependencies);
			}
			propertyDefMap.put(level, levelPropertyDefMap);
			propertyDependencyMap.put(level, levelDependencyMap);
		}
	}
	
	/**
	 * Resolve the order of properties given the dependency map. <br>
	 * This is a hacky recursive function that orders the properties. <br>
	 * It works by maintaining a collection a visited properties until there are no more unvisited dependencies. <br>
	 * @param dependencyMap {@code Map<Property, Collection<Property>>} containing the dependencies for each property
	 * @param property {@code Property} should be <b>null</b> in the initial call. The function internally recurses on the base case.
	 * @param visited {@code Collection<Property>} should be <b>null</b> in the initial call. The function uses this internally.
	 * @param orderedProperties {@code List<Property>} containing properties in sorted order based on dependencies.
	 */
	private static void resolvePropertyOrder(Map<Property, Collection<Property>> dependencyMap, Property property, 
			Collection<Property> visited, List<Property> orderedProperties) {
		if(property == null) {
//			First call..iterate over map and start solving
			for(java.util.Map.Entry<Property, Collection<Property>> entry : dependencyMap.entrySet()) {
				List<Property> newVisited = new LinkedList<Property>();	//FIXME: This should ideally be a HashSet
				Property baseProperty = entry.getKey();
				newVisited.add(baseProperty);
				resolvePropertyOrder(dependencyMap, baseProperty, newVisited, orderedProperties);
			}
		}
		else {
			if(dependencyMap.containsKey(property)) {
				for(Property dependency : dependencyMap.get(property)) {
					if(visited.contains(dependency)) {
//						Fatal! We have a circular dependency...
//						TODO: Add some nice visual showing the circular dependency
						StringBuilder sb = new StringBuilder();
						Iterator<Property> iter = visited.iterator();
						while(iter.hasNext()) {
							Property p = iter.next();
							sb.append(p.toString());
							if(iter.hasNext())
								sb.append(" -> ");
						}
						throw new IllegalStateException("Circular dependency detected on property '" + property + "'\n" +
								sb.toString() + "\n");
					}
					visited.add(dependency);
					resolvePropertyOrder(dependencyMap, dependency, visited, orderedProperties);
				}
			}
			if(!orderedProperties.contains(property))
				orderedProperties.add(property);
		}
	}
	
	private static void resolveRelatedProperties(IWorldTree node) {
		Collection<Constraint> constraints = node.root().constraints();
		Map<Property, Collection<Constraint>> propertyConstraintMap = new HashMap<Property, Collection<Constraint>>();
		for(Constraint constraint : constraints) {
			Property property = constraint.condition().property();
			if(!propertyConstraintMap.containsKey(property))
				propertyConstraintMap.put(property, new HashSet<Constraint>());
			propertyConstraintMap.get(property).add(constraint);
		}
		
		for(Constraint constraint : constraints) {
			Collection<Property> dependencies = new HashSet<Property>();
			Property property = constraint.condition().property();
			resolveConstraintDependencies(constraint, propertyConstraintMap, dependencies);
			relatedPropertiesMap.put(property, dependencies);

//			Now add all definition dependencies
			Hierarchy currentLevel = constraint.level();
			while(currentLevel != null) {
				Collection<Property> definitionDependencies = propertyDependencyMap.get(currentLevel).get(property);
				relatedPropertiesMap.get(property).addAll(definitionDependencies);
				currentLevel = currentLevel.childLevel();
			}
		}
	}
	
	private static void resolveConstraintDependencies(Constraint constraint, Map<Property, Collection<Constraint>> propertyConstraintMap, 
			Collection<Property> dependencies) {
		int oldDependenciesSize = dependencies.size();
//		Now, we add all properties referenced within this constraint
//		TODO: Figure out if we really need to add *all* the properties referenced
//		First, the query
		IQuery query = constraint.query();
		while(query != null) {
			ICondition condition = query.condition();
			while(condition != null) {
				if(condition.property() != null)
					dependencies.add(condition.property());
				condition = condition.subCondition();
			}
			query = query.subQuery();
		}
		
//		Now, the constraint condition itself..we may be repeating the base property..but that's okay since we're using a HashSet
		ICondition condition = constraint.condition();
		while(condition != null) {
			if(condition.property() != null)
				dependencies.add(condition.property());
			condition = condition.subCondition();
		}
		
		int newDependenciesSize = dependencies.size();
		if(oldDependenciesSize == newDependenciesSize)
//			We didn't really do anything new..just return
			return;
//		Now we have all the properties, create a list of all the corresponding constraints, and get their dependencies
		Collection<Constraint> dependentConstraints = new HashSet<Constraint>();
		for(Property property : dependencies)
			dependentConstraints.addAll(propertyConstraintMap.get(property));
		
		Iterator<Constraint> iter = dependentConstraints.iterator();
		while(iter.hasNext()) {
			Constraint c = iter.next();
			resolveConstraintDependencies(c, propertyConstraintMap, dependencies);
			iter.remove();
		}
	}
}
