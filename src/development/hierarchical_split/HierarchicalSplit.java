package development.hierarchical_split;

import internal.parser.TokenCmpOp;
import internal.parser.containers.Constraint;
import internal.parser.containers.Datum;
import internal.parser.containers.Reference;
import internal.parser.containers.expr.IExpr;
import internal.parser.containers.property.PropertyDef;
import internal.parser.resolve.Result;
import internal.parser.resolve.constraint.ConstraintSolver;
import internal.parser.resolve.query.QueryResolutionEngine;
import internal.tree.IWorldTree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import development.com.collection.range.Range;
import development.com.collection.range.RangeSet;

public class HierarchicalSplit {

	public static Map<IWorldTree, Datum> split(IWorldTree node, Constraint constraint, PropertyDef definition) {
		constraint 					= processConstraint(constraint);
		Map<IWorldTree, RangeSet> childRanges = new HashMap<IWorldTree, RangeSet>();
		Result queryResult 			= QueryResolutionEngine.evaluate(node, definition.query());
		Reference column		= null;
		if(definition.aggregateExpression().expr() != null) {
			IExpr aggExpr = definition.aggregateExpression().expr();
			if(aggExpr.property() != null)
				column			= definition.aggregateExpression().expr().reference();
			else
				column 			= definition.query().pattern().lhs();
		}
		else
			column 				= definition.query().pattern().lhs();
		
		List<IWorldTree> children 	= queryResult.get(column);
		for(IWorldTree child : children) {
			RangeSet bounds = ConstraintSolver.getBounds(child, definition);
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
			node.setDefinition(definition);
			node.setObject(child, ranges);
			nodeList.add(node);
		}
		
		while(nodeList.size() > 1) {
			Node node 		= new Node(null);
			Node listHead	= nodeList.get(0);
			node.setDefinition(definition);
			node.insert(listHead);
			nodeList.remove(0);
			
			if(nodeList.size() > 0) {
				listHead	= nodeList.get(0);
				node.insert(listHead);
				nodeList.remove(0);
			}
			nodeList.add(node);
		}
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
//			if(this.parent == null)
				return this.definition;
//			else
//				return this.root().definition;
		}
		
		public void insert(Node node) {
			if(this.lhs == null) {
				this.setLHS(node);
				this.setRanges(this.lhs.ranges());
			}
			else if(this.rhs == null) {
				this.setRHS(node);
				switch(definition().type()) {
				case AGGREGATE:
					switch(definition().aggregateExpression().type()) {
					case COUNT:
					case SUM:
						this.ranges = this.ranges().sum(this.rhs.ranges());
						break;
					case MAX:
					case MIN:
//						TODO: Write proper logic for this
						throw new IllegalStateException("Unimplemented logic!\n");
					default:
						throw new IllegalStateException("Tree can't have a node with 2 objects somewhere below, but not be an aggregate!");
					}
				case BASIC:
					break;
				case INHERIT:
					break;
				case RANDOM:
					break;
				default:
					break;
				}
			}
		}

		
		public RangeSet ranges() {
			return this.ranges;
		}
		
		public void split(Map<IWorldTree, Datum> values, Datum requiredValue) {
			Datum lhsValue 	= null;
			Datum rhsValue	= null;
			if(object == null) {
				RangeSet lhsRanges		= this.lhs.ranges();
				RangeSet rhsRanges		= this.rhs.ranges();
				RangeSet validRanges	= new RangeSet();
				
				switch(definition().type()) {
				case AGGREGATE:
					switch(definition().aggregateExpression().type()) {
					case MAX:
						if(Math.random() >= 0.5) {
							for(Range lhsRange : lhsRanges) {
								if(lhsRange.contains(requiredValue))
									validRanges.add(lhsRange);
							}
							
							RangeSet validRhsRanges = new RangeSet();
							for(Range rhsRange : rhsRanges) {
								if(rhsRange.contains(requiredValue)) {
									if(requiredValue.compareTo(rhsRange.upperBound(), TokenCmpOp.GT) == 0) {
										rhsRange = rhsRange.clone();
										rhsRange.setUpperBound(requiredValue);
									}
									validRhsRanges.add(rhsRange);
								}
							}
							lhsValue = validRanges.generateRandom();
							rhsValue = validRhsRanges.generateRandom();
						}
						else {
							for(Range rhsRange : rhsRanges) {
								if(rhsRange.contains(requiredValue))
									validRanges.add(rhsRange);
							}
							
							RangeSet validLhsRanges = new RangeSet();
							for(Range lhsRange : lhsRanges) {
								if(lhsRange.contains(requiredValue)) {
									if(requiredValue.compareTo(lhsRange.upperBound(), TokenCmpOp.GT) == 0)
										lhsRange.setUpperBound(requiredValue);
									validLhsRanges.add(lhsRange);
								}
								else if(lhsRange.upperBound().compareTo(requiredValue, TokenCmpOp.LE) == 0) {
									validLhsRanges.add(lhsRange);
								}
							}
							rhsValue = validRanges.generateRandom();
							lhsValue = validLhsRanges.generateRandom();
						}
						break;
					case MIN:
//						TODO
						break;
					case COUNT:
					case SUM:
						lhsRanges 	= this.lhs.ranges();
						rhsRanges 	= this.rhs.ranges();
						validRanges	= new RangeSet();
						
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
						RangeSet bounds = ConstraintSolver.getBounds(object, this.definition());
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
