package internal.parser.resolve.constraint;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import internal.Helper.Hierarchy;
import internal.parser.TokenCmpOp;
import internal.parser.containers.Constraint;
import internal.parser.containers.Datum;
import internal.parser.containers.Datum.DatumType;
import internal.parser.containers.Datum.Flt;
import internal.parser.containers.Reference;
import internal.parser.containers.condition.ICondition;
import internal.parser.containers.expr.IExpr;
import internal.parser.containers.property.Property;
import internal.parser.containers.property.PropertyDef;
import internal.parser.containers.query.IQuery;
import internal.parser.resolve.Column;
import internal.parser.resolve.Result;
import internal.parser.resolve.query.QueryResolutionEngine;
import internal.tree.IWorldTree;
import internal.tree.IWorldTree.IMap;
import development.com.collection.range.FloatRange;
import development.com.collection.range.IntegerRange;
import development.com.collection.range.Range;
import development.com.collection.range.RangeSet;
import development.com.collection.range.Range.BoundType;

public class BasicSolver implements IConstraintSolver {
	private Map<Hierarchy, Map<Property, PropertyDef>> hierarchicalDefMap = null;
	private Map<Hierarchy, Map<Property, Collection<Constraint>>> hierarchicalConstraintMap = null;
	private Map<Hierarchy, Map<Property, Collection<Property>>> hierarchicalDepMap = null;
	private Map<Property, Collection<Property>> relatedPropertiesMap = null;
	
	public BasicSolver(
			Map<Hierarchy, Map<Property, PropertyDef>> hierarchicalDefMap,
			Map<Hierarchy, Map<Property, Collection<Constraint>>> hierarchicalConstraintMap,
			Map<Hierarchy, Map<Property, Collection<Property>>> hierarchicalDependencyMap,
			Map<Property, Collection<Property>> relatedPropertiesMap) {
		
		this.hierarchicalDefMap			= hierarchicalDefMap;
		this.hierarchicalConstraintMap	= hierarchicalConstraintMap;
		this.hierarchicalDepMap			= hierarchicalDependencyMap;
		this.relatedPropertiesMap		= relatedPropertiesMap;
	}

	/**
	 * Evaluate a definition on a specific node and return the value computed
	 * @param node {@code IWorldTree} 
	 * @param definition {@code PropertyDef}
	 * @return {@code Datum} containing the value of the {@code Property} defined by {@code definition}
	 */
	private Datum evaluate(IWorldTree node, PropertyDef definition) {
		Property definitionProperty = definition.property();
		
		Column column = null;
		
//		First we collect all the references and properties that are part of the expression of this definition
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
		case RANDOM:
			return node.properties().get(definitionProperty);
		case INHERIT:
		default:
			throw new IllegalStateException("Unimplemented definition type :" + definition.type());
		}
		
//		Now, we evaluate the query and narrow down on the columns based on the references
		Result result = QueryResolutionEngine.evaluate(node, definition.query());
		column = result.get(references.iterator().next().toString());	//FIXME: Temporary hack
		
//		Since we are probably going to have to evaluate an expression, we store the values of properties referenced in the expression
		Map<IWorldTree, List<Datum>> childMap = new LinkedHashMap<IWorldTree, List<Datum>>(column.size());
		for(IWorldTree child : column) {
			List<Datum> values = new LinkedList<Datum>();
			for(Property p : expressionProperties) {
				Datum value = child.properties().get(p);
				if(value != null)	//XXX: Is this valid?
					values.add(value);
			}
			if(values.size() > 0)	//XXX: Is this valid?
				childMap.put(child, values);
		}
		
//		Now, we evaluate the expression value for each child
		List<Datum> values = new ArrayList<Datum>(childMap.size());
		if(definition.expression() != null) {
			for(Map.Entry<IWorldTree, List<Datum>> entry : childMap.entrySet()) {
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

//		Now, we apply the definition type and obtain the result as propertyValue
		Datum propertyValue = null;
		switch(definition.type()) {
		case AGGREGATE:
//			Since we already have all the 'children', and since we have already computed the expression, just iterate over the values and apply the aggregation logic
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
			}
			break;
		case BASIC:
//			XXX: Assumes that the BASIC expr type always has the form 'DEFINE X.property AS EXPR in LEVEL X'
//			XXX: Given the above assumption, we've already computed this value..so just pull it out..
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
//			TODO: Need to implement this
		case RANDOM:
//			TODO: Need to implement this
//			TODO: Figure out if and when this can occur 
		default:
			throw new IllegalStateException("Unimplemented definition type :" + definition.type());
		}		
		return propertyValue;
	}
	
	/**
	 * Evaluate an expression. The list {@code values} is to be used to substitute for any variable encountered in the {@code expression}
	 * @param expression {@code IExpr} expression to evaluate
	 * @param values {@code List<Datum>} list of values to use for substitution
	 * @return {@code Datum} containing the result of the evaluation
	 */
	private Datum evaluateExpression(IExpr expression, List<Datum> values) {
		Datum result = null;
		IExpr expr = expression;
		Datum subExprValue = null;
		
//		We opt for a simple recursive solution
		if(expr.value() != null)
			result = expr.value();
		else {
			assert values.size() > 0 : expr.property() + " has no substitution in provided list\n";
			result = values.remove(0);
		}

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

	/**
	 * Checks whether a condition was satisfied on a given node for a given property
	 * @param node {@code IWorldTree} to check
	 * @param condition {@code ICondition} to satisfy
	 * @param property {@code Property} whose value is checked
	 * @return <b>true</b> is the {@code condition} was satisfied <br>
	 * <b>false</b> otherwise
	 */
	private boolean satisfies(IWorldTree node, ICondition condition, Property property) {
		if(condition == null)
			return true;
		ICondition subCondition = condition.subCondition();
		
		boolean result = false;
		switch(condition.type()) {
		case BASIC:
//			Datum propertyValue = evaluate(node, definition);
			Datum propertyValue = node.properties().get(property);
			if(condition.property().equals(property)) {
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
		case COMPLEX:
		default:
			throw new IllegalStateException("Unimplemented condition type :" + condition.type());
		}
		
		if(subCondition != null) {
			switch(condition.unionType()) {
			case AND:
				return result & satisfies(node, subCondition, property);
			case OR:
//				TODO: Not sure how to handle or
				throw new IllegalStateException("Unimplemented union type :" + condition.unionType());
			}
		}
		return result;
	}
	
	
	/**
	 * Iteratively materialize definitions starting from {@code node}.
	 * Since recursive calls lead to massive memory consumption, we opt for an iterative approach to materializing constraints.
	 * @param node {@code IWorldTree} from which the push down is to be performed
	 */
	private void iterativePushDown(IWorldTree node, Collection<PropertyDef> definitions) {
//		FIXME:	Currently assumes that properties need to be materialized only at the lowest level..all other levels are aggregates
//		TODO:	Need to handle dependent properties in the right order
		if(node.children() != null) {
			for(IWorldTree child : node.children())
				iterativePushDown(child, definitions);
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
//					We have an expression at the Tile level
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
				case RANDOM:
					range = definition.randomspec().range();
					value = range.generateRandom();
					break;
				case INHERIT:
				default:
					throw new IllegalStateException("Unimplemented definition type :" + definition.type());
				}
				if(value != null) {
					node.properties().put(property, value);
//					updateParent(node, property);
				}
			}
		}
	}

	/**
	 * Update a {@code node}'s property map with the property defined in {@code definition}
	 * @param node {@code IWorldTree} to update
	 * @param definition {@code PropertyDef} containing the {@code Property} to update
	 */
	private void updateNode(IWorldTree node, PropertyDef definition) {
		Datum value = evaluate(node, definition);
		node.properties().put(definition.property(), value);
	}
	
//	TODO: There was a reason for opting for this expensive update method..don't quite recall what it was..try and figure it out
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
	public void pushDownConstraints(IWorldTree node) {
		IMap map = ((IMap) node.root());	//FIXME: Hack
		Collection<PropertyDef> definitions = node.root().definitions();
		List<IWorldTree> nodes = new ArrayList<IWorldTree>();
		
		nodes.add(map);
		nodes.addAll(map.getNodesByLevel(Hierarchy.Room));
		nodes.addAll(map.getNodesByLevel(Hierarchy.Region));
		nodes.addAll(map.getNodesByLevel(Hierarchy.Tile));
		
		List<IWorldTree> nodesCopy = new LinkedList<IWorldTree>(nodes);
		IWorldTree currentNode = null;
		while(true) {
			boolean satisfied = true;
			iterativePushDown(node, definitions);
			while(nodesCopy.size() > 0) {
				currentNode = nodesCopy.get(0);
				Property failedProperty = null;
				
//				Get the level of this node
				Hierarchy nodeLevel = Hierarchy.parse(currentNode.getClass());

//				Get the constraints present in this level
				Map<Property, Collection<Constraint>> levelConstraintMap = hierarchicalConstraintMap.get(nodeLevel);

//				Iterate over each entry of the map
				for(Map.Entry<Property, Collection<Constraint>> entry : levelConstraintMap.entrySet()) {
					Property constraintProperty = entry.getKey();
					Collection<Constraint> propertyConstraints = entry.getValue();

//					Check to see which constraint-conditions from the collection of constraints were satisfied
					BitSet bits = new BitSet(propertyConstraints.size());
					Collection<Constraint> satisfiedConstraints = new LinkedList<Constraint>();
					int idx = 0;
					for(Constraint constraint : propertyConstraints) {
						IQuery query = constraint.query();
						Result result = QueryResolutionEngine.evaluate(currentNode, query);
						if(result.get(constraint.query().pattern().lhs().toString()).contains(currentNode)) {
							bits.set(idx);
							satisfiedConstraints.add(constraint);
						}
					}
					
//					Check to see if the combining those conditions results in a valid constraint condition
					RangeSet validRanges = mergeConstraints(currentNode, constraintProperty, satisfiedConstraints);
					if(validRanges.contains(currentNode.properties().get(constraintProperty)))
						satisfied = true;
					else {
						satisfied = false;
						failedProperty = constraintProperty;
						break;
					}
				}
				if(!satisfied) {
					nodesCopy.clear();
					nodesCopy.addAll(nodes);
					definitions = new HashSet<PropertyDef>();
					for(Property property : relatedPropertiesMap.get(failedProperty)) {
						for(Hierarchy level : Hierarchy.values()) {
							PropertyDef definition = hierarchicalDefMap.get(level).get(property);
							if(definition != null)
								definitions.add(definition);
						}
					}
					break;
				}
				else
					nodesCopy.remove(0);
			}
			if(satisfied)
				break;
		}
	}

	private RangeSet mergeConstraints(IWorldTree node, Property constraintProperty, Collection<Constraint> satisfiedConstraints) {
		RangeSet validRanges = new RangeSet();
		
		DatumType type = node.properties().get(constraintProperty).type();
		switch(type) {
		case FLOAT:
			validRanges.add(FloatRange.closed(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY));
			break;
		case INT:
			validRanges.add(IntegerRange.closed(Integer.MIN_VALUE, Integer.MAX_VALUE));
			break;
		case BOOL:
		case STRING:
		default:
			throw new IllegalStateException("Unimplemented type " + type);
		}

//		TODO: Handle condition unions
		for(Constraint constraint : satisfiedConstraints) {
			ICondition condition = constraint.condition();
			while(condition != null) {
				RangeSet conditionRangeSet = new RangeSet();
				Datum conditionValue = condition.value();
				switch(condition.type()) {
				case BASIC:
//					XXX: This only works for integer ranges..
					switch(condition.operator()) {
					case EQ:
						Range conditionRange = Range.createRange(conditionValue, BoundType.CLOSED,
								conditionValue, BoundType.CLOSED);
						conditionRangeSet.add(conditionRange);
						break;
					case GE:
						conditionRange = Range.createRange(conditionValue, BoundType.CLOSED,
								new Datum.Int(Integer.MAX_VALUE), BoundType.CLOSED);
						conditionRangeSet.add(conditionRange);
						break;
					case GT:
						conditionRange = Range.createRange(conditionValue, BoundType.OPEN,
								new Datum.Int(Integer.MAX_VALUE), BoundType.CLOSED);
						conditionRangeSet.add(conditionRange);
						break;
					case LE:
						conditionRange = Range.createRange(new Datum.Int(Integer.MIN_VALUE), BoundType.CLOSED,
								condition.value(), BoundType.CLOSED);
						conditionRangeSet.add(conditionRange);
						break;
					case LT:
						conditionRange = Range.createRange(new Datum.Int(Integer.MAX_VALUE), BoundType.CLOSED,
								condition.value(), BoundType.OPEN);
						conditionRangeSet.add(conditionRange);
						break;
					case NOTEQ:
						Range conditionRange1 = Range.createRange(new Datum.Int(Integer.MIN_VALUE), BoundType.CLOSED,
								condition.value(), BoundType.OPEN);
						Range conditionRange2 = Range.createRange(condition.value(), BoundType.OPEN,
								new Datum.Int(Integer.MAX_VALUE), BoundType.CLOSED);
						conditionRangeSet.add(conditionRange1);
						conditionRangeSet.add(conditionRange2);
						break;
					default:
						throw new IllegalStateException("Unimplemented operator " + condition.operator());
					}
					RangeSet validRangesClone = new RangeSet();
					RangeSet resultRanges = new RangeSet();
					validRangesClone.addAll(validRanges);
					for(Range validRange : validRangesClone) {
						for(Range range : conditionRangeSet) {
							Range resultRange = validRange.intersection(range);
							if(resultRange != null)
								resultRanges.add(resultRange);
						}
						validRanges.remove(validRange);
					}
					validRanges = resultRanges;
					break;
				case BOOLEAN:
//					TODO
					break;
				case COMPLEX:
//					TODO
					break;
				default:
					throw new IllegalStateException("Unimplemented condition type " + condition.type());
				}
				condition = condition.subCondition();
			}
		}
		return validRanges;
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
}
