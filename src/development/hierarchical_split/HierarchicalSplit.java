package development.hierarchical_split;

import internal.parser.TokenCmpOp;
import internal.parser.containers.Constraint;
import internal.parser.containers.Datum;
import internal.parser.containers.Datum.Int;
import internal.parser.containers.expr.IExpr;
import internal.parser.containers.property.PropertyDef;
import internal.parser.containers.property.PropertyDef.RandomSpec;
import internal.parser.resolve.ResolutionEngine;
import internal.parser.resolve.Result;
import internal.tree.IWorldTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import development.com.collection.range.IntegerRange;
import development.com.collection.range.Range;
import development.com.collection.range.Range.BoundType;
import development.com.collection.range.RangeSet;

public class HierarchicalSplit {

	public static Map<IWorldTree, Datum> split(IWorldTree node, Constraint constraint, PropertyDef definition) {
		constraint 					= processConstraint(constraint);
		Map<IWorldTree, RangeSet> childRanges = new HashMap<IWorldTree, RangeSet>();
		Result queryResult 			= ResolutionEngine.evaluate(node, definition.query());
		String columnName			= null;
		if(definition.aggregateExpression().expr() != null) {
			IExpr aggExpr = definition.aggregateExpression().expr();
			if(aggExpr.property() != null)
				columnName			= definition.aggregateExpression().expr().property().reference().toString();
			else
				columnName 			= definition.query().pattern().lhs().toString();
		}
		else
			columnName 				= definition.query().pattern().lhs().toString();
		
		List<IWorldTree> children 	= queryResult.get(columnName);
		for(IWorldTree child : children) {
			RangeSet bounds = child.getBounds(definition);
			childRanges.put(child, bounds);
		}
		
		Map<IWorldTree, Datum> result = new HashMap<IWorldTree, Datum>();
		
		Node root = buildTree(childRanges, definition);
		
		Datum requiredValue = constraint.condition().value();
		root.split(result, requiredValue);
		return result;
	}

	private static Node buildTree(Map<IWorldTree, RangeSet> childRanges, PropertyDef definition) {
		List<Node> nodeList		= new LinkedList<Node>();
		
		for(Map.Entry<IWorldTree, RangeSet> entry : childRanges.entrySet()) {
			IWorldTree child	= entry.getKey();
			RangeSet ranges		= entry.getValue();
			Node node 			= new Node(null);
			node.setObject(child, ranges);
			nodeList.add(node);
		}
		
		while(nodeList.size() > 1) {
			Node node 		= new Node(null);
			
			Node listHead	= nodeList.get(0);
			node.insert(listHead);
			nodeList.remove(0);
			
			if(nodeList.size() > 0) {
				listHead	= nodeList.get(0);
				node.insert(listHead);
				nodeList.remove(0);
			}
			nodeList.add(node);
		}
		Node root = nodeList.get(0);
		root.setDefinition(definition);
		return nodeList.get(0);
	}
	
	/**
	 * This method is a hack. It is used to change constraints of type 
	 * 'ASSERT prop [< | >] val'
	 * into
	 * 'ASSERT prop [<= | >=] val1'
	 * @param constraint {@code Constraint} to be changed
	 * @return modified {@code Constraint} 
	 */
	public static Constraint processConstraint(Constraint constraint) {
		Constraint newConstraint = new Constraint(constraint.type(), constraint.level(), constraint.query(), constraint.condition());
		
		Datum value = newConstraint.condition().value();
		switch(newConstraint.condition().operator()) {
		case GT:
			newConstraint.condition().setOperator(">=");
			switch(newConstraint.condition().value().type()) {
			case FLOAT:
				newConstraint.condition().setValue(value.add(new Datum.Flt(Float.MIN_VALUE)));
				break;
			case INT:
				newConstraint.condition().setValue(value.add(new Datum.Int(1)));
				break;
			case BOOL:
			case STRING:
			default:
				throw new IllegalStateException("processConstraint: Cannot handle type :" + value.type());
			}
			break;
		case LT:
			newConstraint.condition().setOperator("<=");
			switch(newConstraint.condition().value().type()) {
			case FLOAT:
				newConstraint.condition().setValue(value.subtract(new Datum.Flt(Float.MIN_VALUE)));
				break;
			case INT:
				newConstraint.condition().setValue(value.subtract(new Datum.Int(1)));
				break;
			case BOOL:
			case STRING:
			default:
				throw new IllegalStateException("processConstraint: Cannot handle type :" + value.type());
			}
			break;
		case EQ:
		case GE:
		case NOTEQ:
		case LE:
		default:
			break;
		
		}
		return newConstraint;
	}
	
	private static class Node {
		private Node parent;
		private Node lhs;
		private Node rhs;
		private IWorldTree object;
		private RangeSet ranges;
		private PropertyDef definition;
		
		public Node(Node parent) {
			this.parent		= parent;
			this.lhs		= null;
			this.rhs		= null;
			this.object		= null;
			this.ranges		= null;
			this.definition	= null;
		}

		public void setDefinition(PropertyDef definition) {
			this.definition	= definition;
		}

		public void setObject(IWorldTree object, RangeSet ranges) {
			this.object	= object;
			this.ranges	= ranges;
		}
		
		public void setRanges(RangeSet ranges) {
			this.ranges	= ranges;
		}
		
		private void setLHS(Node node) {
			node.parent	= this;
			this.lhs	= node;
		}
		
		private void setRHS(Node node) {
			node.parent	= this;
			this.rhs	= node;
		}
		
		public Node parent() {
			return parent;
		}
		
		public Node root() {
			if(this.parent == null)
				return this;
			else
				return parent.root();
		}
		
		public PropertyDef definition() {
			if(this.parent == null)
				return this.definition;
			else
				return this.root().definition;
		}
		
		public void insert(Node node) {
			if(this.lhs == null)
				this.setLHS(node);
			else if(this.rhs == null)
				this.setRHS(node);
		}

		
		public RangeSet ranges() {
			if(lhs == null)
				return ranges;
			else if(rhs == null)
				return lhs.ranges();
			else {
				RangeSet resultRanges = new RangeSet();
				switch(definition().type()) {
				case AGGREGATE:
					switch(definition().aggregateExpression().type()) {
					case COUNT:
					case SUM:
						for(Range range1 : this.lhs.ranges()) {
							for(Range range2 : this.rhs.ranges()) {
								Range resultRange = range1.add(range2);
								resultRanges.add(resultRange);
							}
						}
						return resultRanges;
					case MAX:
//						TODO: Determine whether min = minVal when maxVal > max
						for(Range range1 : this.lhs.ranges()) {
							for(Range range2 : this.rhs.ranges()) {
								Datum lowerBound 	= range1.lowerBound();
								Datum upperBound	= range1.upperBound();
								if(range1.lowerBound().compareTo(range2.lowerBound(), TokenCmpOp.LT) == 0)
									lowerBound 	= range2.lowerBound();
								
								if(range1.upperBound().compareTo(range2.upperBound(), TokenCmpOp.LT) == 0)	//TODO: Validate this..
									upperBound	= range2.upperBound();
								Range resultRange = range1.clone();
								resultRange.setLowerBound(lowerBound);
								resultRange.setUpperBound(upperBound);
								resultRanges.add(resultRange);	//FIXME: Assumes that the ranges overlap..fix this!
							}
						}
						return resultRanges;
					case MIN:
////					TODO: Determine whether max = maxVal when minVal < min
						for(Range range1 : this.lhs.ranges()) {
							for(Range range2 : this.rhs.ranges()) {
								Datum lowerBound 	= range1.lowerBound();
								Datum upperBound	= range1.upperBound();
								if(range1.lowerBound().compareTo(range2.lowerBound(), TokenCmpOp.GT) == 0)
									lowerBound 	= range2.lowerBound();
								
								if(range1.upperBound().compareTo(range2.upperBound(), TokenCmpOp.GT) == 0)	//TODO: Validate this..
									upperBound	= range2.upperBound();
								Range resultRange = range1.clone();
								resultRange.setLowerBound(lowerBound);
								resultRange.setUpperBound(upperBound);
								resultRanges.add(resultRange);	//FIXME: Assumes that the ranges overlap..fix this!
							}
						}
						return resultRanges;
					default:
						throw new IllegalStateException("Tree can't have a node with 2 objects somewhere below, but not be an aggregate!");
					}
				}
			}
			throw new IllegalStateException("Shouldn't be trying to return null");
		}
		
		public void split(Map<IWorldTree, Datum> values, Datum requiredValue) {
			Datum lhsValue 	= null;
			Datum rhsValue	= null;
			if(object == null) {
				switch(definition().type()) {
				case AGGREGATE:
					switch(definition().aggregateExpression().type()) {
					case COUNT:
//						TODO
						break;
					case MAX:
//						TODO
						break;
					case MIN:
//						TODO
						break;
					case SUM:
						RangeSet lhsRanges = this.lhs.ranges();
						RangeSet rhsRanges = this.rhs.ranges();
						RangeSet validRanges = new RangeSet();
						
						for(Range lhsRange : lhsRanges) {
							Datum lhsLowerBound	= lhsRange.lowerBound();
							Datum lhsUpperBound	= lhsRange.upperBound();
							for(Range rhsRange : rhsRanges) {
								Range lhsRangeClone = lhsRange.clone();
									
								Datum rhsLowerBound = rhsRange.lowerBound();
								Datum rhsUpperBound = rhsRange.upperBound();
									
								Datum lowerBoundSum	= lhsLowerBound.add(rhsLowerBound);
								Datum upperBoundSum	= lhsUpperBound.add(rhsUpperBound);
								if(requiredValue.compareTo(lowerBoundSum, TokenCmpOp.GE) == 0 && requiredValue.compareTo(upperBoundSum, TokenCmpOp.LE) == 0) {	//FIXME: GE, LE only handles closed ranges
									if(rhsUpperBound.compareTo(requiredValue, TokenCmpOp.GT) == 0) {
										rhsUpperBound = requiredValue;
									}
									if(lhsLowerBound.add(rhsUpperBound).compareTo(requiredValue, TokenCmpOp.LT) == 0) {
										Datum newLhsLowerBound		= requiredValue.subtract(rhsUpperBound);
										assert lhsRange.contains(newLhsLowerBound) : "lhsRange " + lhsRange + " does not contain newLhsLowerBound - " + newLhsLowerBound;
										lhsRangeClone.setLowerBound(newLhsLowerBound);
									}
									if(lhsUpperBound.add(rhsLowerBound).compareTo(requiredValue, TokenCmpOp.GT) == 0) {
										Datum newLhsUpperBound		= requiredValue.subtract(rhsLowerBound);
										assert lhsRange.contains(newLhsUpperBound) : "lhsRange " + lhsRange + " does not contain newLhsUpperBound - " + newLhsUpperBound;
										lhsRangeClone.setUpperBound(newLhsUpperBound);
									}
									validRanges.add(lhsRangeClone);
								}
							}
						}
						assert validRanges.size() >= 1 : "There seems to be no valid range!\n";
						lhsValue = validRanges.generateRandom();
						rhsValue = requiredValue.subtract(lhsValue);
						if(this.rhs != null)
							assert rhsRanges.contains(rhsValue) : "ranges does not contain rhsValue :" + rhsValue + "  - " + ranges;	//TODO: Verify whether this should be validRanges
						break;
					}
				case BASIC:
//					TODO
					break;
				case INHERIT:
//					TODO
					break;
				case RANDOM:
//					TODO
					break;
				}
			}
			
			else {
				if(definition().type().equals(PropertyDef.Type.AGGREGATE)) {
					switch(definition().aggregateExpression().type()) {
					case COUNT:
					case MAX:
					case MIN:
					case SUM:
						RangeSet bounds = object.getBounds(this.definition());
						assert bounds.contains(requiredValue) : "Trying to set " + requiredValue + "\nwhen bounds are :" + bounds;
						values.put(object, requiredValue);
						break;
					}
				}
			}
			if(lhs != null)
				this.lhs.split(values, lhsValue);
			if(rhs != null)
				this.rhs.split(values, rhsValue);
		}
	}
}
