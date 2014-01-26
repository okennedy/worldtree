package internal.parser.resolve.constraint;

import internal.Helper.Hierarchy;
import internal.parser.TokenCmpOp;
import internal.parser.containers.Constraint;
import internal.parser.containers.Constraint.Type;
import internal.parser.containers.Datum;
import internal.parser.containers.Datum.DatumType;
import internal.parser.containers.Reference;
import internal.parser.containers.condition.BaseCondition;
import internal.parser.containers.condition.BaseCondition.ConditionType;
import internal.parser.containers.condition.ICondition;
import internal.parser.containers.pattern.BasePattern;
import internal.parser.containers.pattern.IPattern;
import internal.parser.containers.property.Property;
import internal.parser.containers.property.PropertyDef;
import internal.parser.containers.query.BaseQuery;
import internal.parser.containers.query.IQuery;
import internal.tree.IWorldTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import development.com.collection.range.FloatRange;
import development.com.collection.range.IntegerRange;
import development.com.collection.range.Range;
import development.com.collection.range.Range.BoundType;
import development.com.collection.range.RangeSet;
import development.hierarchical_split.HierarchicalSplit;

public class ComplexSolver implements IConstraintSolver {
	public void pushDownConstraints(IWorldTree node) {
//		FIXME: The following lines has been commented because of quadratic-ish behaviour
//		if(node.children() == null || node.children().size() == 0) {
			Hierarchy myLevel = Hierarchy.parse(node.getClass());
			if(myLevel == Hierarchy.Tile) {
				Collection<Constraint> constraints 	= node.constraints();
				for(Constraint constraint : constraints) {
					if(constraint.type() == Constraint.Type.PROGRAM_GENERATED) {
						ICondition constraintCondition = constraint.condition();
						Property property 		= constraintCondition.property();
						Datum datum				= constraintCondition.value();
						
						PropertyDef definition	= null;
						for(PropertyDef def : node.definitions()) {
							if(def.property().equals(property) && def.level().equals(myLevel)) {
								definition = def;
								break;
							}
						}
						
						Datum value = getBounds(node, definition).generateRandom();
						node.addProperty(property, value);
					}
				}
				return;
			}
			
//		Only root contains all constraints
		IWorldTree root = node.root();

		Collection<Constraint> constraints 	= node.constraints();
		Collection<PropertyDef> definitions	= root.definitions();
		
		for(Constraint constraint : constraints) {
			if(!myLevel.equals(constraint.level()))
				continue;
			Property property = constraint.condition().property();
			
			PropertyDef definition = null;
			for(PropertyDef def : definitions) {
				if(def.property().equals(property) && (def.level().equals(myLevel))) {
					definition = def;
					break;
				}
			}
			if(definition == null)
				throw new IllegalStateException("Property " + property + " has no definition!\n");
			
			Map<IWorldTree, Datum> childConstraintValues = HierarchicalSplit.split(node, constraint, definition);
			
			
			for(Map.Entry<IWorldTree, Datum> entry : childConstraintValues.entrySet()) {
				IWorldTree child		= entry.getKey();
				Datum value 			= childConstraintValues.get(child);
				
				Hierarchy childLevel 	= Hierarchy.parse(child.getClass());
				Constraint childConstraint = null;
				{
					IPattern pattern		= new BasePattern(new Reference("this"), null, null);
					Reference reference		= new Reference("this");
					Property childProperty	= property;
					TokenCmpOp operator		= constraint.condition().operator();
					ICondition condition	= new BaseCondition(false, ConditionType.BASIC, reference, childProperty, operator, value);
					IQuery query = new BaseQuery(childLevel, pattern, null);
					childConstraint = new Constraint(Type.PROGRAM_GENERATED, childLevel, query, condition);
				}
				child.addConstraint(childConstraint);
				RangeSet newPropertyRanges = processBounds(child, childConstraint);
				child.bounds().put(property, newPropertyRanges);
			}
		}
		
//		Now push down the children
//		node.children() should *NOT* be null at this point..check anyway
		if(node.children() == null)
			return;
		for(IWorldTree child : node.children()) {
			pushDownConstraints(child);
		}
	}

	
	public RangeSet processBounds(IWorldTree node, Constraint constraint) {
		Hierarchy myLevel		= Hierarchy.parse(node.getClass());
		
		Property property 		= constraint.condition().property();

		Collection<PropertyDef> definitions	= node.root().definitions();
		PropertyDef definition	= null;
		for(PropertyDef def : definitions) {
			if(def.level().equals(myLevel) && def.property().equals(property)) {
				definition = def;
				break;	//FIXME: Should we break here?
			}
		}
		
		RangeSet propertyRanges		= getBounds(node, definition);
		RangeSet newPropertyRanges 	= new RangeSet();
		
//		TODO: Should we be handling 'where' clauses here?
		Datum value				= constraint.condition().value();
		DatumType type 			= value.type();
		
		if(!(type == DatumType.INT || type == DatumType.FLOAT))
			throw new IllegalStateException("Pre-processing bounds: Cannot handle constraint value type :" + type);
		
		switch(constraint.condition().operator()) {
		case EQ:
			assert propertyRanges.contains(value) : "Pre-processing bounds: Range " + propertyRanges + " does not contain " + value;
			for(Range range : propertyRanges) {
				if(range.contains(value)) {
					switch(type) {
					case FLOAT:
						range = FloatRange.closed(value, value);
						break;
					case INT:
						range = IntegerRange.closed(value, value);
						break;
					}
					newPropertyRanges.add(range);
				}
			}
			break;
		case GE:
			for(Range range : propertyRanges) {
				Range newRange = range.clone();
				if(range.contains(value)) {
					if(value.compareTo(range.lowerBound(), TokenCmpOp.GT) == 0) {
						switch(type) {
						case FLOAT:
							newRange.setLowerBound(value.toFlt().subtract(new Datum.Flt(Float.MIN_VALUE)));
							newRange.setLowerBoundType(BoundType.OPEN);		//FIXME: Should this be there?
							break;
						case INT:
							newRange.setLowerBound(value.toInt());
							newRange.setLowerBoundType(BoundType.CLOSED);	//FIXME: Should this be there?
							break;
						}
					}
				}
				else if(range.upperBound().compareTo(value, TokenCmpOp.LT) == 0)
					continue;
				newPropertyRanges.add(newRange);
			}
			break;
		case GT:
			for(Range range : propertyRanges) {
				Range newRange = range.clone();
				if(range.contains(value)) {
					if(value.compareTo(range.lowerBound(), TokenCmpOp.GE) == 0) {
						switch(type) {
						case FLOAT:
							newRange.setLowerBound(value.toFlt());
							newRange.setLowerBoundType(BoundType.OPEN);		//FIXME: Should this be there?
							break;
						case INT:
							newRange.setLowerBound(value.toInt().add(new Datum.Int(1)));
							newRange.setLowerBoundType(BoundType.CLOSED);	//FIXME: Should this be there?
							break;
						}
					}
				}
				else if(range.upperBound().compareTo(value, TokenCmpOp.LE) == 0)
					continue;
				newPropertyRanges.add(newRange);
			}
			break;
		case LE:
			for(Range range : propertyRanges) {
				Range newRange = range.clone();
				if(range.contains(value)) {
					if(value.compareTo(range.upperBound(), TokenCmpOp.LT) == 0) {
						switch(type) {
						case FLOAT:
							newRange.setUpperBound(value.toFlt().subtract(new Datum.Flt(Float.MIN_VALUE)));
							newRange.setUpperBoundType(BoundType.OPEN);		//FIXME: Should this be there?
							break;
						case INT:
							newRange.setUpperBound(value.toInt());
							newRange.setUpperBoundType(BoundType.CLOSED);	//FIXME: Should this be there?
							break;
						}
					}
				}
				else if(range.lowerBound().compareTo(value, TokenCmpOp.GT) == 0)
					continue;
				newPropertyRanges.add(newRange);
			}
			break;
		case LT:
			for(Range range : propertyRanges) {
				Range newRange = range.clone();
				if(range.contains(value)) {
					if(value.compareTo(range.upperBound(), TokenCmpOp.LE) == 0) {
						switch(type) {
						case FLOAT:
							newRange.setUpperBound(value.toFlt());
							newRange.setUpperBoundType(BoundType.OPEN);		//FIXME: Should this be there?
							break;
						case INT:
							newRange.setUpperBound(value.toInt().subtract(new Datum.Int(1)));
							newRange.setUpperBoundType(BoundType.CLOSED);	//FIXME: Should this be there?
							break;
						}
					}
				}
				else if(range.lowerBound().compareTo(value, TokenCmpOp.GT) == 0)
					continue;
				newPropertyRanges.add(newRange);
			}
			break;
		case NOTEQ:
			for(Range range : propertyRanges) {
				Range rangeClone1 = range.clone();
				if(range.contains(value)) {
//					If this is a range [x - x] where the constraint says != x, then ignore this range
					if(range.upperBound().compareTo(range.lowerBound(), TokenCmpOp.EQ) == 0)
						continue;
					Range rangeClone2	= range.clone();
					if(range.lowerBound().compareTo(value, TokenCmpOp.EQ) == 0) {
						switch(type) {
						case FLOAT:
							rangeClone1.setLowerBound(value.toFlt().add(new Datum.Flt(Float.MIN_VALUE)));
							rangeClone1.setLowerBoundType(BoundType.CLOSED);
							break;
						case INT:
							rangeClone1.setLowerBound(value.add(new Datum.Int(1)));
							rangeClone1.setLowerBoundType(BoundType.CLOSED);
							break;
						}
					}
					else if(range.upperBound().compareTo(value, TokenCmpOp.EQ) == 0) {
						switch(type) {
						case FLOAT:
							rangeClone1.setUpperBoundType(BoundType.OPEN);
							break;
						case INT:
							rangeClone1.setUpperBound(value.subtract(new Datum.Int(1)));
							rangeClone1.setUpperBoundType(BoundType.CLOSED);
							break;
						}
					}
					else {
						switch(type) {
						case FLOAT:
							rangeClone2.setUpperBound(value.toFlt().subtract(new Datum.Flt(Float.MIN_VALUE)));
							rangeClone2.setUpperBoundType(BoundType.CLOSED);
							rangeClone1.setLowerBound(value.toFlt().add(new Datum.Flt(Float.MIN_VALUE)));
							rangeClone1.setLowerBoundType(BoundType.CLOSED);
							newPropertyRanges.add(rangeClone2);
							break;
						case INT:
							rangeClone2.setUpperBound(value.subtract(new Datum.Int(1)));
							rangeClone2.setUpperBoundType(BoundType.CLOSED);
							rangeClone1.setLowerBound(value.add(new Datum.Int(1)));
							rangeClone1.setLowerBoundType(BoundType.CLOSED);
							newPropertyRanges.add(rangeClone2);
							break;
						}
					}
				}
				newPropertyRanges.add(rangeClone1);
			}
			break;
		}
		return newPropertyRanges;
	}
	
	public void initializeBounds(IWorldTree node) {
		if(node.children() != null) {
			for(IWorldTree child : node.children()) {
				initializeBounds(child);
			}
		}
		
		Hierarchy myLevel = Hierarchy.parse(node.getClass());
		
		if(node.bounds() == null) {
//			We have never called preProcessBounds before..Process user-defined constraints
			node.setBounds(new HashMap<Property, RangeSet>(0));
			Collection<Constraint> constraints 	= node.root().constraints();
			
			for(Constraint c : constraints) {
				if(c.level().equals(myLevel))
					processBounds(node, c);
			}
		}
	}
	
	public RangeSet getBounds(IWorldTree node, PropertyDef parentDefinition) {
		if(node.bounds() == null) {
			initializeBounds(node);
		}
		Hierarchy myLevel = Hierarchy.parse(node.getClass());
		
		IWorldTree root = node.root();
		
		Property property = parentDefinition.property();
		
		Collection<PropertyDef> definitions	= root.definitions();
		
		PropertyDef definition = null;
		for(PropertyDef def : definitions) {
			if(def.property().equals(property) && (def.level().equals(myLevel))) {
				definition = def;
				break;
			}
		}
		
		if(node.bounds() != null && node.bounds().get(property) != null)
			return node.bounds().get(property);
		
		List<RangeSet> bounds = new ArrayList<RangeSet>();
		if(node.children() != null) {
			for(IWorldTree child : node.children()) {
				RangeSet ranges = getBounds(child, definition);
				bounds.add(ranges);
			}
		}
				
//		TODO: Perhaps we should use the in-built Datum.add method?
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		
		RangeSet resultRanges = new RangeSet();
		
		switch(definition.type()) {
		case AGGREGATE:
			Datum.DatumType type = bounds.get(0).get(0).lowerBound().type();
			
			switch(definition.aggregateExpression().type()) {
			case COUNT:
//				TODO: Validate this
				Range resultRange = IntegerRange.closed(0, bounds.size());
				resultRanges.add(resultRange);

			case MAX:
//				TODO: Determine whether min = minVal when maxVal > max
				break;
			
			case MIN:
//				TODO: Determine whether max = maxVal when minVal < min
				break;
			case SUM:
//				TODO: Handle float-int interaction - either here or natively in range classes
				for(RangeSet set : bounds) {
					resultRanges = resultRanges.sum(set);
				}
			}
					
			switch(type) {
			case FLOAT:
				node.bounds().put(property, resultRanges);
				return node.bounds().get(property);
			case INT:
				node.bounds().put(property, resultRanges);
				return node.bounds().get(property);
			default:
				throw new IllegalStateException("Default case in allocating type is :" + type);
			}
			case BASIC:
//				TODO
				break;
			case INHERIT:
//				TODO
				break;
			case RANDOM:
				resultRanges.add(definition.randomspec().range().clone());
				return resultRanges;
			default:
			throw new IllegalStateException("Default case in definition type? Type is :" + definition.type());
		}
		return null;
	}

}
