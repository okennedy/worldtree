package internal.tree;

import internal.Helper.Hierarchy;
import internal.parser.containers.Constraint;
import internal.parser.containers.Datum;
import internal.parser.containers.Datum.DatumType;
import internal.parser.containers.Datum.Flt;
import internal.parser.containers.property.PropertyDef;
import internal.parser.containers.property.PropertyDef.RandomSpec;
import internal.parser.containers.property.PropertyDef.RandomSpec.RandomSpecType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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

	protected WorldTree(String name, IWorldTree parent, Collection<Constraint> constraints) {
		this.parent 		= parent;
		this.children 		= null;
		this.name 			= name;
		this.constraints 	= constraints;
		this.properties		= new HashMap<String, Datum>(0);
		
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
	
	@Override
	public Collection<IWorldTree> getAllChildren() {
		List<IWorldTree> result = new ArrayList<IWorldTree>();
		
		result.addAll(this.children());
		
		IWorldTree node = null;
		int listIndex = 0;
		while(listIndex < result.size()) {
			node = result.get(listIndex);
			result.addAll(node.children());
			listIndex++;
		}
		return result;
	}
	
	@Override
	public Collection<IWorldTree> getChildrenByClass(String className) {
		List<IWorldTree> result			= new ArrayList<IWorldTree>();
		Class<?> clazz = null;
		
		className = (WorldTreeFactory.class.getName() + "$" + className);
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			System.err.println("No class found with name :" + className);
		}
		
		for(IWorldTree child : getAllChildren()) {
			if(!child.getClass().equals(clazz))
				result.add(child);
		}
		return result;
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
	
	protected void pushDownConstraints() {
		if(this.children == null || this.children.size() == 0)
			return;
		
		Hierarchy myLevel = Hierarchy.parse(this.getClass());
//		Only root contains all constraints
		IWorldTree root = this.root();
		if(root == null)
			root = this;

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
				
				List<RandomSpec> bounds = new ArrayList<RandomSpec>();
				for(IWorldTree child : this.children) {
					RandomSpec bound = child.getBounds(definition);
					bounds.add(bound);
				}
				
				switch(definition.type()) {
				case AGGREGATE:
					switch(definition.aggregateExpression().type()) {
					case COUNT:
						break;
					case MAX:
						break;
					case MIN:
						break;
					case SUM:
						break;
					default:
						break;
					
					}
					break;
				default:
					break;
				
				}
			}
		}
	}
	
	public RandomSpec getBounds(PropertyDef parentDefinition) {
		Hierarchy myLevel = Hierarchy.parse(this.getClass());
		
		IWorldTree root = this.root();
		if(root == null)
			root = this;
		
		String property = parentDefinition.property().name();
		
		Collection<PropertyDef> definitions	= root.definitions();
		
		PropertyDef definition = null;
		for(PropertyDef def : definitions) {
			if(def.property().name().equals(property) && (def.level().equals(myLevel))) {
				definition = def;
				break;
			}
		}
		
		List<RandomSpec> bounds = new ArrayList<RandomSpec>();
		for(IWorldTree child : this.children) {
			RandomSpec bound = child.getBounds(definition);
			bounds.add(bound);
		}
				
//		TODO: Perhaps we should use the in-built Datum.add method?
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		Datum.DatumType type = null;
				
		switch(definition.type()) {
		case AGGREGATE:
			switch(definition.aggregateExpression().type()) {
			case COUNT:
				return new RandomSpec(RandomSpecType.INT, new Datum.Int(0), new Datum.Int(this.children.size()));
			case MAX:
			case MIN:
				for(RandomSpec spec : bounds) {
					float maxVal = (Float) spec.high().toFlt().data();
					float minVal = (Float) spec.low().toFlt().data();
					if(maxVal > max) {
						max = maxVal;
						type = spec.high().type();
					}
					if(minVal < min) {
						min = minVal;
					}
				}
				break;
			case SUM:
				min = 0;
				max = 0;
				type = DatumType.INT;
				for(RandomSpec spec : bounds) {
					float maxVal = (Float) spec.high().toFlt().data();
					float minVal = (Float) spec.low().toFlt().data();
					if(type != DatumType.FLOAT && spec.type().equals(RandomSpecType.FLOAT))
						type = DatumType.FLOAT;
					max += maxVal;
					min += minVal;
				}
				break;
			}
			switch(type) {
			case FLOAT:
				return new RandomSpec(RandomSpecType.FLOAT, new Datum.Flt(min), new Datum.Flt(max));
			case INT:
				return new RandomSpec(RandomSpecType.INT, new Datum.Int((int)min), new Datum.Int((int)max));
			default:
				throw new IllegalStateException("Default case in allocating MAX? Type is :" + type);
			
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
