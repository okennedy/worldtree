package internal.tree;

import internal.Helper.Hierarchy;
import internal.parser.TokenCmpOp;
import internal.parser.containers.Constraint;
import internal.parser.containers.Constraint.Type;
import internal.parser.containers.Datum;
import internal.parser.containers.Datum.DatumType;
import internal.parser.containers.Datum.Flt;
import internal.parser.containers.Datum.Int;
import internal.parser.containers.Reference;
import internal.parser.containers.condition.BaseCondition;
import internal.parser.containers.condition.BaseCondition.ConditionType;
import internal.parser.containers.condition.ICondition;
import internal.parser.containers.pattern.BasePattern;
import internal.parser.containers.pattern.IPattern;
import internal.parser.containers.property.Property;
import internal.parser.containers.property.PropertyDef;
import internal.parser.containers.property.PropertyDef.RandomSpec;
import internal.parser.containers.property.PropertyDef.RandomSpec.RandomSpecType;
import internal.parser.containers.query.BaseQuery;
import internal.parser.containers.query.IQuery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import development.com.collection.range.*;
import development.com.collection.range.Range.BoundType;
import development.hierarchical_split.HierarchicalSplit;

/**
 * WorldTree is the abstract class that every object in the hierarchy extends.
 * It is used to provide a common structure and interface to all the objects in the world.
 * 
 * @author guru
 */

public abstract class WorldTree implements IWorldTree, Serializable {
	private static final long serialVersionUID = -5257914696549384766L;
	
	protected IWorldTree parent;
	protected Collection<IWorldTree> children;
	private String name;
	private Collection<Constraint> constraints;
	private Collection<PropertyDef> definitions;
	private Map<Property, Datum> properties;
	private Map<Property, RangeSet> bounds;

	protected WorldTree(String name, IWorldTree parent, Collection<Constraint> constraints) {
		this.parent 		= parent;
		this.children 		= null;
		this.name 			= name;
		this.constraints 	= constraints;
		this.properties		= new HashMap<Property, Datum>(0);
		this.bounds			= null;
		
		if(parent != null) {
			IWorldTree root = this.root();
			Collection<Constraint> rootConstraints = root.constraints();
			if(rootConstraints != null) {
				for(Constraint constraint : rootConstraints) {
					Hierarchy myLevel			= Hierarchy.parse(this.getClass());
					Hierarchy constraintLevel	= constraint.level();
					if(myLevel.equals(constraintLevel))
						constraints.add(constraint);
				}
			}
		}
	}

	public String name() {
		return name;
	}
	
	public String absoluteName() {
		Stack<String> stack = new Stack<String>();
		
		IWorldTree node = this;
		while(node.parent() != null) {
			stack.push(node.name());
			node = node.parent();
		}
		
		StringBuffer result = new StringBuffer();
		while(stack.size() > 1)
			result.append(stack.pop() + " -> ");
		result.append(stack.pop());
		
		return result.toString();
	}
	
	public IWorldTree parent() {
		return parent;
	}
	
	public Collection<IWorldTree> children() {
		return children;
	}
	
	public IWorldTree root() {
		if(this.parent == null)
			return this;
		else
			return this.parent.root();
	}
	
	@Override
	public Collection<Constraint> constraints() {
		return constraints;
	}
	
	@Override
	public Collection<PropertyDef> definitions() {
		if(this.parent == null)
			return definitions;
		else
			return this.parent.definitions();
	}
	
	@Override
	public void addConstraint(Constraint constraint) {
		assert constraints != null : "Trying to add constraint to " + name + " when " + name + ".constraints = null\n";
		constraints.add(constraint);
		this.processBounds(constraint);
	}
	
	@Override
	public boolean removeConstraint(Constraint constraint) {
		assert constraints != null : "Trying to remove constraint :\n" + constraint + "\nwhen " + name + ".constraints = null\n";
		return constraints.remove(constraint);
	}
	
	@Override
	public void addProperty(Property property, Datum value) {
		properties.put(property, value);
	}
	
	@Override
	public Map<Property, Datum> properties() {
		return properties;
	}
	
//	FIXME: Added this to solve NPE on constraints()
	@Override
	public void setConstraints(Collection<Constraint> constraints) {
		this.constraints = constraints;
	}
	
	protected void setDefinitions(Collection<PropertyDef> definitions) {
		this.definitions = definitions;
	}

	public void pushDownConstraints() {
//		FIXME: The following lines has been commented because of quadratic-ish behaviour
//		if(this.children() == null || this.children().size() == 0) {
			Hierarchy myLevel = Hierarchy.parse(this.getClass());
			if(myLevel == Hierarchy.Tile) {
				Collection<Constraint> constraints 	= this.constraints();
				for(Constraint constraint : constraints) {
					if(constraint.type() == Constraint.Type.PROGRAM_GENERATED) {
						ICondition constraintCondition = constraint.condition();
						Property property 		= constraintCondition.property();
						Datum datum				= constraintCondition.value();
						
						PropertyDef definition	= null;
						for(PropertyDef def : this.definitions()) {
							if(def.property().equals(property) && def.level().equals(myLevel)) {
								definition = def;
								break;
							}
						}
						
						Datum value = this.getBounds(definition).generateRandom();
						this.addProperty(property, value);
					}
				}
				return;
			}
			
//		Only root contains all constraints
		IWorldTree root = this.root();

		Collection<Constraint> constraints 	= this.constraints();
		Collection<PropertyDef> definitions	= root.definitions();
		
		for(Constraint constraint : constraints) {
			if(myLevel.equals(constraint.level())) {
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
				
				Map<IWorldTree, Datum> childConstraintValues = HierarchicalSplit.split(this, constraint, definition);
				
				
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
				}
			}
		}
		
//		Now push down the children
//		this.children() should *NOT* be null at this point..check anyway
		if(this.children() == null)
			return;
		for(IWorldTree child : this.children()) {
			child.pushDownConstraints();
		}
	}

	
	private void processBounds(Constraint constraint) {
		Hierarchy myLevel		= Hierarchy.parse(this.getClass());
		
		Property property 		= constraint.condition().property();

		Collection<PropertyDef> definitions	= this.root().definitions();
		PropertyDef definition	= null;
		for(PropertyDef def : definitions) {
			if(def.level().equals(myLevel) && def.property().equals(property)) {
				definition = def;
				break;	//FIXME: Should we break here?
			}
		}
		
		RangeSet propertyRanges		= this.getBounds(definition);
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
		this.bounds.put(property, newPropertyRanges);
	}
	
	public void processBounds() {
		if(this.children() != null) {
			for(IWorldTree child : this.children()) {
				child.processBounds();
			}
		}
		
		Hierarchy myLevel = Hierarchy.parse(this.getClass());
		
		if(this.bounds == null) {
//			We have never called preProcessBounds before..Process user-defined constraints
			this.bounds = new HashMap<Property, RangeSet>(0);
			Collection<Constraint> constraints 	= this.root().constraints();
			
			for(Constraint c : constraints) {
				if(c.level().equals(myLevel))
					processBounds(c);
			}
		}
	}
	
	public RangeSet getBounds(PropertyDef parentDefinition) {
		if(this.bounds == null)
			processBounds();
		
		Hierarchy myLevel = Hierarchy.parse(this.getClass());
		
		IWorldTree root = this.root();
		
		Property property = parentDefinition.property();
		
		Collection<PropertyDef> definitions	= root.definitions();
		
		PropertyDef definition = null;
		for(PropertyDef def : definitions) {
			if(def.property().equals(property) && (def.level().equals(myLevel))) {
				definition = def;
				break;
			}
		}
		
		if(bounds != null && bounds.get(property) != null)
			return bounds.get(property);
		
		List<RangeSet> bounds = new ArrayList<RangeSet>();
		if(this.children() != null) {
			for(IWorldTree child : this.children()) {
				RangeSet ranges = child.getBounds(definition);
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
				this.bounds.put(property, resultRanges);
				return this.bounds.get(property);
			case INT:
				this.bounds.put(property, resultRanges);
				return this.bounds.get(property);
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
	
	/* -------------------------------------------  String methods  ------------------------------------------- */
	public List<String> getStringRepresentation() {
		List<String> stringRepresentation = initString();
		return stringRepresentation;
	}
	
	@Override
	public String toString() {
		List<String> stringList = getStringRepresentation();
		StringBuffer result = new StringBuffer();
		for(String string : stringList)
			result.append(string + System.getProperty("line.separator"));
		
		return result.toString();
	}
	
	/**
	 * Method provided to initialize the string representation of this instance.
	 * This method is to be called once all it's children have been initialized.
	 * <p>
	 * The logic is as follows:<br>
	 * Every child is expected to have a {@code List<String>} representing each line of its visual.<br>
	 * We need to concatenate each line of every child together and then CR+LF onto the next line.<br>
	 * {@code listStringList} contains the list of stringLists (2-D {@code ArrayList}).<br>
	 * We append every line of every {@code List<List<String>>} before moving onto the next index.<br>
	 * @return List<String> containing the string representation
	 */
	protected List<String> initString() {
		Collection<IWorldTree> children = this.children();
		List<String> stringRepresentation = new ArrayList<String>();
		
		if(children == null)
			return stringRepresentation;
		List<List<String>> listStringList = new ArrayList<List<String>>();
		for(IWorldTree child : children) {
			listStringList.add(child.getStringRepresentation());
		}
		
//		Check for equal lines
		int maxListSize = 0;
		for(List<String> list : listStringList) {
			maxListSize = maxListSize > list.size() ? maxListSize : list.size();
		}
		
//		Make sure all lists are of the same size
		for(List<String> list : listStringList) {
			if(list.size() < maxListSize) {
//				Add new strings of largest length to this list
				int maxLength = 0;
				for(String s :  list) {
					maxLength = maxLength > s.length() ? maxLength : s.length();
				}
				StringBuffer emptySB = new StringBuffer();
				while(emptySB.length() < maxLength)
					emptySB.append(" ");
				
//				Now add them to the list
				while(list.size() < maxListSize)
					list.add(0, emptySB.toString());
			}
		}
		
		int lineCount = 0;
		for(List<String> stringList : listStringList)
				lineCount = lineCount > stringList.size() ? lineCount : stringList.size();
		for(int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
			StringBuffer fullLine = new StringBuffer();
			for(List<String> stringList : listStringList) {
				if(stringList.size() > lineIndex)
					fullLine.append(stringList.get(lineIndex) + " ");
			}
//			if(!stringRepresentation.contains(fullLine.toString()))
				stringRepresentation.add(fullLine.toString());
		}
//		We have obtained every line as we should. We now need to own everything that is within is.
		prepareToString(stringRepresentation);
		
		return stringRepresentation;
	}
	
	/**
	 * Helper method that wraps around the initialized string representation to print ownership in the visual.
	 */
	protected void prepareToString(List<String> stringRepresentation) {
//		The string representation is in place. find the maximum length and wrap around it to own it.\
//		Make the top part of the outer shell that wraps around all of this instance's children.
		List<String> newStringRepresentation = new ArrayList<String>();
		int maxLineLength = 0;
		for(String string : stringRepresentation)
			maxLineLength = string.length() > maxLineLength ? string.length() : maxLineLength;
		StringBuffer header = new StringBuffer();
		maxLineLength += this.name.length();
		maxLineLength += (maxLineLength % 2 == 0) ? 0 : 1;
		header.append("+");
		for(int i = 0; i < maxLineLength; i++)
			header.append("-");
		header.append("+");
		newStringRepresentation.add(header.toString());
		header.delete(0, header.length());
		header.append("|" + this.name);
		for(int i = 0; i < maxLineLength - name.length(); i++)
			header.append(" ");
		header.append("|");
		maxLineLength = header.toString().length();
		
		newStringRepresentation.add(header.toString());
		header = null;
		
//		Now that the top is done, add the middle components (string representation of children)
		for(String string : stringRepresentation) {
			int spaces = maxLineLength - string.length() - 2;	//The 2 is because of the starting and ending '|'
			StringBuffer line = new StringBuffer("|");
			for(int i = 0; i <= spaces / 2; i++)
				line.append(" ");
			line.append(string);
			while(line.length() < maxLineLength - 1)
				line.append(" ");
			line.append("|");
			if(line.length() != newStringRepresentation.get(0).length())
				throw new IllegalStateException();
			newStringRepresentation.add(line.toString());
//			System.out.println(newStringRepresentation.toString().replaceAll("(\\[|\\]|,  )", ""));
		}
		
//		Add the bottom part of the outer shell that wraps around all of this instance's children
		StringBuffer footer = new StringBuffer();
		footer.append("|");
		for(int i = 0; i < maxLineLength - 2; i++)
			footer.append(" ");
		footer.append("|");
		newStringRepresentation.add(footer.toString());
		String line = footer.toString();
		footer = new StringBuffer();
		
		line = line.replace(" ", "-").replace("|", "+");
		footer.append(line);
		newStringRepresentation.add(footer.toString());
		footer = null;
		
//		Update reference
		stringRepresentation.clear();
		for(String s : newStringRepresentation) {
			stringRepresentation.add(s);
		}
	}
}
