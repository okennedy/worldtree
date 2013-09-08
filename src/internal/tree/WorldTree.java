package internal.tree;

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
	private Map<String, Datum> properties;
	private Map<String, RandomSpec> bounds;

	protected WorldTree(String name, IWorldTree parent, Collection<Constraint> constraints) {
		this.parent 		= parent;
		this.children 		= null;
		this.name 			= name;
		this.constraints 	= constraints;
		this.properties		= new HashMap<String, Datum>(0);
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
	}
	
	@Override
	public boolean removeConstraint(Constraint constraint) {
		assert constraints != null : "Trying to remove constraint :\n" + constraint + "\nwhen " + name + ".constraints = null\n";
		return constraints.remove(constraint);
	}
	
	@Override
	public void addProperty(String name, Datum value) {
		properties.put(name, value);
	}
	
	@Override
	public Map<String, Datum> properties() {
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
	
	
	private Datum pickValue(Datum.DatumType type, float lowerBound, float upperBound, Constraint constraint) {
		ICondition constraintCondition	= constraint.condition();
		Datum datum						= constraintCondition.value();
		
		float constraintValue	= (Float) datum.toFlt().data();
		float value = 0;
		switch(constraintCondition.operator()) {
		case EQ:
			value = (Float) datum.toFlt().data();
			break;
		case GE:
			value = (float) (constraintValue + (float) (Math.random() * (upperBound - constraintValue)));
			break;
		case GT:
			while(value <= constraintValue)
				value = (float) (constraintValue + (float) (Math.random() * (upperBound - constraintValue)));
			break;
		case LE:
			value = (float) (lowerBound + (float) (Math.random() * (constraintValue - lowerBound)));
			break;
		case LT:
			value = constraintValue;
			if(constraintValue == lowerBound) {
//				FIXME: This is a hack. 
//				HierarchicalSplit is not supposed to pick a value equal to lowerBound when Operator is '<'
//				We cannot go lesser than the lowerBound...
				break;
			}
			while(value >= constraintValue)
				value = (float) (lowerBound + (float) (Math.random() * (constraintValue - lowerBound)));
			break;
		case NOTEQ:
			value = constraintValue;
			while(value == constraintValue)
				value = (float) (lowerBound + (float) (Math.random() * (upperBound - lowerBound)));
			break;
		}
		switch(type) {
		case FLOAT:
			return new Datum.Flt(value);
		case INT:
			return new Datum.Int((int) value);
		default:
			System.err.println("Warning: No code to initialize " + datum.type() + " at the leaves");
			return null;
		}
	}
	
	public void pushDownConstraints() {
		if(this.children() == null || this.children().size() == 0) {
			Hierarchy myLevel = Hierarchy.parse(this.getClass());
			if(myLevel == Hierarchy.Tile) {
				Collection<Constraint> constraints 	= this.constraints();
				for(Constraint constraint : constraints) {
					if(constraint.type() == Constraint.Type.PROGRAM_GENERATED) {
						ICondition constraintCondition = constraint.condition();
						String property 		= constraintCondition.property().name();
						Datum datum				= constraintCondition.value();
						
						PropertyDef definition	= null;
						for(PropertyDef def : this.definitions()) {
							if(def.property().name().equals(property) && def.level().equals(myLevel)) {
								definition = def;
								break;
							}
						}
						
						RandomSpec randomSpec 			= definition.randomspec();
						assert randomSpec != null : "No randomspec at leaves of the hierarchy!\n";
						float randomSpecHigh	= (Float) randomSpec.range().upperBound().toFlt().data();
						float randomSpecLow		= (Float) randomSpec.range().lowerBound().toFlt().data();
						
						Datum value = pickValue(datum.type(), randomSpecLow, randomSpecHigh, constraint);
						this.addProperty(property, value);
					}
				}
			}
			else {
				System.err.println("Warning: pushDownConstraints has been called when the entire skeleton has not been initialized");
			}
			return;
		}
		
		Hierarchy myLevel = Hierarchy.parse(this.getClass());
//		Only root contains all constraints
		IWorldTree root = this.root();

		Collection<Constraint> constraints 	= this.constraints();
		Collection<PropertyDef> definitions	= root.definitions();
		
		for(Constraint constraint : constraints) {
			if(myLevel.equals(constraint.level())) {
				String property = constraint.condition().property().name();
				
				PropertyDef definition = null;
				for(PropertyDef def : definitions) {
					if(def.property().name().equals(property) && (def.level().equals(myLevel))) {
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
						Property childProperty	= new Property(new Reference("this"), property);
						TokenCmpOp operator		= constraint.condition().operator();
						ICondition condition	= new BaseCondition(false, ConditionType.BASIC, childProperty, operator, value);
						IQuery query = new BaseQuery(childLevel, pattern, null);
						childConstraint = new Constraint(Type.PROGRAM_GENERATED, childLevel, query, condition);
					}
					child.addConstraint(childConstraint);
				}
			}
		}
	}

	public RandomSpec getBounds(PropertyDef parentDefinition) {
		Hierarchy myLevel = Hierarchy.parse(this.getClass());
		
		IWorldTree root = this.root();
		
		String property = parentDefinition.property().name();
		
		Collection<PropertyDef> definitions	= root.definitions();
		
		PropertyDef definition = null;
		for(PropertyDef def : definitions) {
			if(def.property().name().equals(property) && (def.level().equals(myLevel))) {
				definition = def;
				break;
			}
		}
		
		if(bounds != null && bounds.get(property) != null)
			return bounds.get(property);
		
		List<RandomSpec> bounds = new ArrayList<RandomSpec>();
		if(this.children() != null) {
			for(IWorldTree child : this.children()) {
				RandomSpec bound = child.getBounds(definition);
				bounds.add(bound);
			}
		}
				
//		TODO: Perhaps we should use the in-built Datum.add method?
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		
		Range resultRange = null;
		switch(definition.type()) {
		case AGGREGATE:
			Datum.DatumType type = bounds.get(0).range().lowerBound().type();
			switch(definition.aggregateExpression().type()) {
			case COUNT:
				resultRange = IntegerRange.openClosed(0, this.children().size());
				return new RandomSpec(RandomSpecType.INT, resultRange);
			case MAX:
//				TODO: Determine whether min = minVal when maxVal > max
				for(RandomSpec spec : bounds) {
					float maxVal = (Float) spec.range().upperBound().toFlt().data();
					if(maxVal > max) {
						max = maxVal;
						resultRange = spec.range();
					}
				}
				break;
			case MIN:
//				TODO: Determine whether max = maxVal when minVal < min
				for(RandomSpec spec : bounds) {
					float minVal = (Float) spec.range().lowerBound().toFlt().data();
					if(minVal < min) {
						min = minVal;
						resultRange = spec.range();
					}
				}
				break;
			case SUM:
				min = 0;
				max = 0;
				resultRange = null;
				
				switch(type) {
				case FLOAT:
					resultRange = FloatRange.closed(0, 0);
					break;
				case INT:
					resultRange = IntegerRange.closed(0, 0);
					break;
				default:
					System.err.println("Warning: Trying to sum INT/FLOAT with type :" + type);
					break;
				}
				
//				We assume that all bounds are of the same 'type'
				for(RandomSpec spec : bounds) {
					resultRange = resultRange.add(spec.range());
				}
				max = (Float) resultRange.upperBound().toFlt().data();
				min = (Float) resultRange.lowerBound().toFlt().data();
				
				break;
			}
			
//			We need to save bounds..allocate if null
			if(this.bounds == null)
				this.bounds = new HashMap<String, RandomSpec>(0);
			switch(type) {
			case FLOAT:
				RandomSpec bound = new RandomSpec(RandomSpecType.FLOAT, resultRange);
				this.bounds.put(property, bound);
				return this.bounds.get(property);
			case INT:
				bound = new RandomSpec(RandomSpecType.FLOAT, resultRange);
				this.bounds.put(property, bound);
				return this.bounds.get(property);
			default:
				throw new IllegalStateException("Default case in allocating type is :" + type);
			
			}
		case BASIC:
//			TODO
			break;
		case INHERIT:
//			TODO
			break;
		case RANDOM:
			return definition.randomspec();
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
