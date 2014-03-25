package internal.parser.resolve.constraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import internal.Helper.Hierarchy;
import internal.parser.TokenCmpOp;
import internal.parser.containers.Constraint;
import internal.parser.containers.Datum;
import internal.parser.containers.Datum.Bool;
import internal.parser.containers.Datum.DatumType;
import internal.parser.containers.Reference;
import internal.parser.containers.condition.ICondition;
import internal.parser.containers.expr.Expr;
import internal.parser.containers.expr.IExpr;
import internal.parser.containers.property.Property;
import internal.parser.containers.property.PropertyDef;
import internal.parser.containers.query.IQuery;
import internal.parser.resolve.Column;
import internal.parser.resolve.Result;
import internal.parser.resolve.query.QueryResolutionEngine;
import internal.tree.IWorldTree;
import internal.tree.IWorldTree.IMap;
import development.com.collection.range.Range;
import development.com.collection.range.RangeSet;

public class BasicSolver implements IConstraintSolver {

	private Datum evaluate(IWorldTree node, PropertyDef definition) {
		Property definitionProperty = definition.property();
		
		Column column = null;
		Collection<Reference> references = new HashSet<Reference>();
		Collection<Property> expressionProperties = new LinkedList<Property>();
		switch(definition.type()) {
		case AGGREGATE:
			IExpr expr = definition.aggregateExpression().expr();
			while(expr != null) {
				if(expr.property() != null) {
					expressionProperties.add(expr.property());
					references.add(expr.reference());
				}
				expr = expr.subExpr();
			}
			
			break;
		case BASIC:
			if(definition.expression() != null) {
				expr = definition.expression();
				while(expr != null) {
					if(expr.property() != null) {
						expressionProperties.add(expr.property());
						references.add(expr.reference());
					}
					expr = expr.subExpr();
				}
			}
			else if(definition.condition() != null) {
				ICondition condition = definition.condition();
				while(condition != null) {
					if(condition.property() != null) {
						expressionProperties.add(condition.property());
						references.add(condition.reference());
					}
					condition = condition.subCondition();
				}
			}
			break;
		case INHERIT:
			break;
		case RANDOM:
			return node.properties().get(definitionProperty);
		}
		Result result = QueryResolutionEngine.evaluate(node, definition.query());
		column = result.get(references.iterator().next().toString());	//FIXME: Temporary hack
		
		Map<IWorldTree, List<Datum>> childMap = new LinkedHashMap<IWorldTree, List<Datum>>(column.size());
		for(IWorldTree child : column) {
			List<Datum> values = new LinkedList<Datum>();
			for(Property p : expressionProperties) {
				Datum value = child.properties().get(p);
				if(value != null)	//TODO: Is this valid?
					values.add(value);
			}
			if(values.size() > 0)	//TODO: Is this valid?
				childMap.put(child, values);
		}
		
		Datum propertyValue = null;
		List<Datum> values = new ArrayList<Datum>(childMap.size());
		if(definition.expression() != null) {
			for(Map.Entry<IWorldTree, List<Datum>> entry : childMap.entrySet()) {
//				The following code below handles manual evalulation of an expression
				Datum childValue = evaluateExpression(definition.expression(), entry.getValue());
				values.add(childValue);
			}
		}
		else if(definition.condition() != null) {
//			TODO: This is boolean stuff
		}
		else if(definition.aggregateExpression() != null) {
//			FIXME: This is repeated code..similar to definition.expression() != null case..handle this properly
			for(Map.Entry<IWorldTree, List<Datum>> entry : childMap.entrySet()) {
				Datum childValue = evaluateExpression(definition.aggregateExpression().expr(), entry.getValue());
				values.add(childValue);
			}
		}
			
		switch(definition.type()) {
		case AGGREGATE:
			for(Datum value : values) {
				switch(definition.aggregateExpression().type()) {
				case COUNT:
					if(propertyValue == null)
						propertyValue = new Datum.Int(0);
					propertyValue = new Datum.Int(childMap.size());
					break;
				case MAX:
					if(propertyValue == null)
						propertyValue = value.clone();
					if(value.compareTo(propertyValue, TokenCmpOp.GT) == 0)
						propertyValue = value.clone();
					break;
				case MIN:
					if(propertyValue == null)
						propertyValue = value.clone();
					if(value.compareTo(propertyValue, TokenCmpOp.LT) == 0)
						propertyValue = value.clone();
					break;
				case SUM:
					if(propertyValue == null)
						propertyValue = value.clone();
					else
						propertyValue = propertyValue.add(value);
					break;
				default:
					break;
				}
				break;
			}
			break;
		case BASIC:
//			FIXME: Assumes that the BASIC expr type always has the form 'DEFINE X.property AS EXPR in LEVEL X'
//			Given the above assumption, we've already computed this value..so just pull it out..
			int idx = -1;
			for(Map.Entry<IWorldTree, List<Datum>> entry : childMap.entrySet()) {
				idx++;
				if(entry.getKey().equals(node))
					break;
				
			}
			if(idx == -1)
				return null;
			return values.get(idx);
		case INHERIT:
			break;
		case RANDOM:
			break;
		default:
			break;
		}		
		return propertyValue;
	}
	
	private Datum evaluateExpression(IExpr expression, List<Datum> values) {
		Datum result = null;
		IExpr expr = expression;
		Datum subExprValue = null;
		if(expr.value() != null && expr.value().type() != DatumType.STRING)
			result = expr.value();
		else
			result = values.remove(0);

		if(expr.subExpr() != null)
			subExprValue = evaluateExpression(expr.subExpr(), values);
		
		if(subExprValue != null) {
			switch(expr.operator()) {
			case TK_DIV:
				result = result.divide(subExprValue);
				break;
			case TK_MINUS:
				result = result.subtract(subExprValue);
				break;
			case TK_MULT:
				result = result.multiply(subExprValue);
				break;
			case TK_PLUS:
				result = result.add(subExprValue);
				break;
			default:
				throw new IllegalStateException("Unknown operator " + expr.operator());
			}
		}
		return result;
	}

	private boolean satisfies(IWorldTree node, ICondition condition, PropertyDef definition) {
		if(condition == null)
			return true;
		ICondition subCondition = condition.subCondition();
		
		boolean result = false;
		switch(condition.type()) {
		case BASIC:
			Datum propertyValue = evaluate(node, definition);
			if(condition.property().equals(definition.property())) {
				Datum conditionValue = condition.value();
				switch(condition.operator()) {
				case EQ:
					if(propertyValue.compareTo(conditionValue, TokenCmpOp.EQ) == 0)
						result = true;
					break;
				case GE:
					if(propertyValue.compareTo(conditionValue, TokenCmpOp.GE) == 0)
						result = true;
					break;
				case GT:
					if(propertyValue.compareTo(conditionValue, TokenCmpOp.GT) == 0)
						result = true;
					break;
				case LE:
					if(propertyValue.compareTo(conditionValue, TokenCmpOp.LE) == 0)
						result = true;
					break;
				case LT:
					if(propertyValue.compareTo(conditionValue, TokenCmpOp.LT) == 0)
						result = true;
					break;
				case NOTEQ:
					if(propertyValue.compareTo(conditionValue, TokenCmpOp.NOTEQ) == 0)
						result = true;
					break;
				}
			}
			break;
		case BOOLEAN:
			break;
		case COMPLEX:
			break;
		}
		
		if(subCondition != null) {
			switch(condition.unionType()) {
			case AND:
				return result & satisfies(node, subCondition, definition);
			case OR:
//				TODO: Not sure how to handle or
				break;
			}
		}
		return result;
	}
	
	@Override
	public void pushDownConstraints(IWorldTree node) {
		sortDefinitions(node);
		List<IWorldTree> nodes = new ArrayList<IWorldTree>();
		IMap map = ((IMap) node.root());	//FIXME: Hack
		nodes.add(map);
		nodes.addAll(map.getNodesByLevel(Hierarchy.Room));
		nodes.addAll(map.getNodesByLevel(Hierarchy.Region));
		nodes.addAll(map.getNodesByLevel(Hierarchy.Tile));
		
		List<IWorldTree> nodesCopy = new ArrayList<IWorldTree>(nodes);
		IWorldTree currentNode = null;
		while(true) {
			boolean satisfied = true;
			iterativePushDown(node);
			while(nodesCopy.size() > 0) {
				currentNode = nodesCopy.get(0);
				for(Constraint constraint : node.constraints()) {
					if(constraint.level().equals(Hierarchy.parse(currentNode.getClass()))) {
						Result result = QueryResolutionEngine.evaluate(currentNode, constraint);
						if(result.get(constraint.query().pattern().lhs().toString()).contains(currentNode)) {
							Property property = constraint.condition().property();
							PropertyDef definition = null;
							for(PropertyDef def : currentNode.root().definitions()) {
								if(def.level().equals(Hierarchy.parse(currentNode.getClass())) && def.property().equals(property)) {
									definition = def;
									break;
								}
							}
							satisfied &= satisfies(currentNode, constraint.condition(), definition);
						}
					}
				}
				if(!satisfied) {
					nodesCopy.clear();
					nodesCopy.addAll(nodes);
					break;
				}
				else
					nodesCopy.remove(0);
			}
			if(satisfied)
				break;
		}
	}
	
	private void iterativePushDown(IWorldTree node) {
		Collection<PropertyDef> definitions = node.definitions();
//		FIXME:	Currently assumes that properties need to be materialized only at the lowest level..all other levels are aggregates
//		TODO:	Need to handle dependent properties in the right order
		if(node.children() != null) {
			for(IWorldTree child : node.children())
				iterativePushDown(child);
			for(PropertyDef definition : definitions) {
				if(!definition.level().equals(Hierarchy.parse(node.getClass())))
					continue;
				updateNode(node, definition);
			}
//				TODO: Enable query parsing on constraint.query()
//				Result result = QueryResolutionEngine.evaluate(node, constraint.query());
//				Column column = result.get(constraint.query().pattern().lhs().toString());
//				if(column.contains(node)) {
//				}
		}
		else {
			if(definitions == null)
				return;
			for(PropertyDef definition : definitions) {
				if(!definition.level().equals(Hierarchy.parse(node.getClass())))
					continue;
				Property property = definition.property();
				Range range = null;
				Datum value = null;
				
				switch(definition.type()) {
				case BASIC:
					IQuery query = definition.query();
					Result result = QueryResolutionEngine.evaluate(node, query);
					Column column = result.get(definition.reference().toString());
					if(column.contains(node)) {
						IExpr expr = definition.expression();
						ICondition condition = definition.condition();
						if(expr != null)
							value = expr.evaluate(node, result);
						else if(condition != null)
							value = condition.evaluate(node, result);
					}
					break;
				case INHERIT:
					break;
				case RANDOM:
					range = definition.randomspec().range();
					value = range.generateRandom();
					break;
				default:
					break;
				}
				if(value != null) {
					node.properties().put(property, value);
//					updateParent(node, property);
				}
			}
		}
	}

	private void updateNode(IWorldTree node, PropertyDef definition) {
		Datum value = evaluate(node, definition);
		node.properties().put(definition.property(), value);
	}
	
//	TODO: Figure out the reason for this expensive update method
	private void updateParent(IWorldTree node, Property property) {
		IWorldTree parent = node.parent();
		PropertyDef definition = null;
		if(node.parent() == null)
			return;
		
		for(PropertyDef def : node.definitions()) {
			if(def.level().equals(Hierarchy.parse(parent.getClass())) && def.property().equals(property)) {
				definition = def;
				break;
			}
		}
		if(definition != null) {
			Datum value = evaluate(parent, definition);
			parent.properties().put(property, value);
			updateParent(parent, property);
		}
		else {
//			Parent has no definition of this property..ignore updating parent
		}
	}
	
	@Override
	public RangeSet getBounds(IWorldTree node, PropertyDef definition) {
		return null;
	}

	@Override
	public void initializeBounds(IWorldTree node) {
	}

	@Override
	public RangeSet processBounds(IWorldTree node, Constraint constraint) {
		return null;
	}
	
	
	private void sortDefinitions(IWorldTree root) {
		Map<Hierarchy, Map<Property, Collection<Property>>> propertyDependencyMap = 
				new HashMap<Hierarchy, Map<Property, Collection<Property>>>();
		Map<Hierarchy, Map<Property, PropertyDef>> propertyDefMap = 
				new HashMap<Hierarchy, Map<Property, PropertyDef>>();
		
		resolveDefinitionDependencies(root, propertyDependencyMap, propertyDefMap);
		
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
	
	private void resolveDefinitionDependencies(IWorldTree root, Map<Hierarchy, Map<Property, Collection<Property>>> propertyDependencyMap,
			Map<Hierarchy, Map<Property, PropertyDef>> propertyDefMap) {
		Collection<PropertyDef> definitions = root.definitions();
		
//		We now find dependencies among property definitions
		if(propertyDefMap == null)
			propertyDefMap = new HashMap<Hierarchy, Map<Property, PropertyDef>>();
		
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
//					TODO: Figure out whether we're only looking at lower levels when we're aggregating..if not, we need some code here
					break;
				case BASIC:
					if(definition.expression() != null) {
						IExpr expr = definition.expression();
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
				case INHERIT:
//					TODO: Need to implement this
					break;
				case RANDOM:
					break;
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
	
	private void resolvePropertyOrder(Map<Property, Collection<Property>> dependencyMap, Property property, 
			Collection<Property> visited, List<Property> orderedProperties) {
		if(property == null) {
//			First call..iterate over map and start solving
			for(java.util.Map.Entry<Property, Collection<Property>> entry : dependencyMap.entrySet()) {
				List<Property> newVisited = new LinkedList<Property>();
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
	
	
}
