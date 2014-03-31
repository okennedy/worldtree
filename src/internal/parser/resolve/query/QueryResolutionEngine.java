package internal.parser.resolve.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import internal.Helper.Hierarchy;
import internal.parser.TokenCmpOp;
import internal.parser.containers.Constraint;
import internal.parser.containers.Datum;
import internal.parser.containers.IStatement;
import internal.parser.containers.Reference;
import internal.parser.containers.Relation;
import internal.parser.containers.Relation.InbuiltRelationEnum;
import internal.parser.containers.condition.ICondition;
import internal.parser.containers.pattern.IPattern;
import internal.parser.containers.property.Property;
import internal.parser.containers.property.PropertyDef;
import internal.parser.containers.query.IQuery;
import internal.parser.resolve.Column;
import internal.parser.resolve.Result;
import internal.piece.TileInterfaceType;
import internal.space.Space.Direction;
import internal.tree.IWorldTree;
import internal.tree.IWorldTree.ITile;

/**
 * ResolutionEngine is a Singleton class responsible for evaluating statements issued by the user
 * @author guru
 *
 */
public class QueryResolutionEngine {
	private Map<String, Method> relationMap = new HashMap<String, Method>();
	private static QueryResolutionEngine instance = null;
	
	protected QueryResolutionEngine() {
//		Prevent initialization of ResolutionEngine
	}
	
	/**
	 * Evaluate an {@code IStatement}
	 * @param node {@code IWorldTree} object upon which the {@code IStatement} is to be evaluated
	 * @param query {@code IStatement} object containing the statement to evaluate
	 * @return {@code Result} representing the output of the {@code IStatement}
	 */
	public static Result evaluate(IWorldTree node, IStatement statement) {
		if(instance == null)
			init();
		return instance.resolveQueryStatement(node, statement);
	}

	/**
	 * Resolve method that is private to {@code ResolutionEngine} and used to evaluate an {@code IStatement}
	 * @param node {@code IWorldTree} object upon which the {@code IStatement} is to be evaluated
	 * @param statement {@code IStatement} object containing the statement to evaluate
	 * @return {@code Result} representing the output of the {@code IStatement}
	 */
	private Result resolveQueryStatement(IWorldTree node, IStatement statement) {
		Result result = new Result();
		Result oldResult = null;
		switch(statement.getType()) {
		case CONSTRAINT: {
//			We're handling a constraint query
			Constraint constraint = (Constraint) statement;
			result = resolveQueryStatement(node, constraint.query());
			break;
		}
		case PROPERTYDEF: {
//			We're handling a PropertyDef query
			PropertyDef definition = (PropertyDef) statement;
			result = resolveQueryStatement(node, definition.query());
			break;
		}
		case QUERY: {
			IQuery query = (IQuery) statement;
			Hierarchy level		= query.level();
			IPattern pattern	= query.pattern();
			List<IWorldTree> objectList = getObjects(node, level);
			
			while(query != null) {
				level		= query.level();
				pattern		= query.pattern();
				result		= new Result();
				while(pattern != null) {
					Reference rhsColumnRef	= null;
					if(pattern.rhs() == null) {
//						FIXME
						result.add(new Column(pattern.lhs(), objectList));
//						return result;
					}
					else {
						rhsColumnRef 			= pattern.rhs();
						Column rhsColumn		= result.get(rhsColumnRef);
						if(rhsColumn == null)
							rhsColumn			= new Column(rhsColumnRef, objectList);
						
						result = resolveQuery(node, level, pattern, result, rhsColumn);
					}
//					Filter based on conditions
					validateCondition(result, query.condition());
					pattern = pattern.subPattern();
				}
//				Check if we need to union
				if(oldResult != null) {
//					First check for semantics
					assert result.size() == oldResult.size() : "Cannot union " + result.toString() + "\n AND \n" + oldResult.toString();
					int index = 0;
					while(index < result.size()) {	//We have already verified that both results have same number of columns
						assert result.get(index).name().equals(oldResult.get(index).name()) : "Cannot union " + result.toString() + "\n AND \n" + oldResult.toString();
						index++;
					}
					
//					Now do the actual merge
					List<IWorldTree> row = null;
					for(index = 0; index < oldResult.get(0).size(); index++) {
						row = oldResult.getRow(index);
//						if(!result.contains(row))
							result.add(row);
					}
				}
				query = query.subQuery();
				oldResult = result;
			}
			break;
		}
		default:
			break;
		}
//		return makeString(statement, result);
		return result;
	}

	private void validateCondition(Result result, ICondition condition) {
//		TODO: Handle AND | OR
		while(condition != null) {
			boolean inbuiltProperty	= false;
			Reference columnRef	= condition.reference();
			Property property	= condition.property();
			if (Property.InbuiltPropertyEnum.check(property) != null)
				inbuiltProperty = true;
			Column column		= result.get(columnRef);
			if(column == null)
				throw new IllegalArgumentException("Reference " + columnRef + " is not defined!");
			Column columnCopy	= new Column(column.name(), column);
			for(IWorldTree object : columnCopy) {
				if(inbuiltProperty) {
					Method method = instance.relationMap.get(property.toString().toLowerCase());
					try {
						boolean satisfies = (Boolean) method.invoke(null, object, condition);
						if(!satisfies) {
							int rowIndex = column.indexOf(object);
							result.removeRow(rowIndex);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else {
					if(!object.properties().containsKey(property)) {
						int rowIndex = column.indexOf(object);
						result.removeRow(rowIndex);
					}
					else {
						Datum value 		= condition.value();
						Datum objectValue	= object.properties().get(property);
						if(objectValue.compareTo(value, condition.operator()) != 0) {
							int rowIndex 	= column.indexOf(object);
							result.removeRow(rowIndex);
						}
					}
				}
			}
			condition = condition.subCondition();
		}
	}
	
	/**
	 * Resolve method that is specifically designed to handle {@code IQuery}
	 * @param node {@code IWorldTree} object upon which the {@code IQuery} is to be evaluated
	 * @param level {@code Hierarchy} representing the hierarchical level of WorldTree
	 * @param pattern {@code IPattern} representing the pattern to search for
	 * @param result {@code Result} object containing previous query results(if any)
	 * @param objects {@code Column} containing the objects to iterate over while resolving this {@code IQuery}
	 * @return {@code Result} containing tuples satisfying the {@code IQuery}
	 */
	private Result resolveQuery(IWorldTree node, Hierarchy level, IPattern pattern, Result result, Column objects) {
		Relation relation = pattern.relation();
		switch(relation.type()) {
		case CUSTOM:
			break;
		case INBUILT:
			Method method = null;
			try {
				method = instance.relationMap.get(relation.name().toLowerCase());
				result = (Result) method.invoke(null, pattern, result, objects);
				
				if(pattern.relation().regex().equals(Relation.Regex.PLUS)) {
//					FIXME: Remove * entries
					Column lhs = result.get(pattern.lhs());
					Column rhs = result.get(pattern.rhs());
					int index = 0;
					assert(lhs.size() == rhs.size());
					while(index < lhs.size()) {
						if(lhs.get(index).equals(rhs.get(index))) {	//FIXME
							result.removeRow(index);
							continue;
						}
						index++;
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			break;
		default:
			throw new IllegalStateException("Cannot have a type that does not exist in " + Relation.Type.values());
		}
		return result;
	}
	
	/**
	 * Obtain a collection of all objects in the specified hierarchy level from given {@code IWorldTree} instance <br>
	 * The tree is traversed using the argument <b> node </b> as root
	 * @param node {@code IWorldTree} 'root' of the tree
	 * @param level {@code Class<?>} hierarchy level with which objects are filtered
	 * @return {@code Collection<IWorldTree>} containing all nodes in the tree at <b> level </b> having <b> node </b> as root 
	 */
	private List<IWorldTree> getObjects(IWorldTree node, Hierarchy level) {
		List<IWorldTree> nodeList	= new LinkedList<IWorldTree>();
		List<IWorldTree> objectList	= new LinkedList<IWorldTree>();
//		Get collection of relevant objects
		Hierarchy nodeLevel = Hierarchy.parse(node.getClass());
		if(nodeLevel.equals(level)) {
//			node is on the same level as the objects we want..
//			FIXME: We currently ask node's parent for all its peers..we *may* want to change this to return just node
			if(node.parent() == null) {
//				We're at the top of the hierarchy and this is the level that is requested
				objectList.add(node);
				return objectList;
			}
			else
				node = node.parent();
		}
		nodeList.add(node);
		IWorldTree currentNode = null;
		while(nodeList.size() > 0) {
			currentNode = nodeList.get(0);
			if(currentNode.children() != null) {
				for(IWorldTree child : currentNode.children()) {
					Hierarchy childLevel = Hierarchy.parse(child.getClass());
//					If the child level is already lower than what we want, just break
					if(childLevel.compareTo(level) > 0)
						break;	//TODO: Verify that this works as expected
					else if(childLevel.equals(level))
						objectList.add(child);
					else
						nodeList.add(child);
				}
			}
			nodeList.remove(currentNode);
		}
		return objectList;
	}
	
	/**
	 * Initialize the instance
	 */
	private static void init() {
//		TODO: Figure out a nice way to add future methods similar to the way direction is being resolved
		instance = new QueryResolutionEngine();
		try {
			for(Method m : InbuiltRelations.class.getMethods()) {
				if(m.isAnnotationPresent(Proxy.class)) {
					assert(m.isAnnotationPresent(Inbuilt.class));
					Proxy proxy = m.getAnnotation(Proxy.class);
					for(String proxyMethod : proxy.methods().split(" "))
						instance.relationMap.put(proxyMethod, m);
				}
			}
			for(Method m : InbuiltProperties.class.getMethods()) {
				if(m.isAnnotationPresent(Proxy.class)) {
					assert(m.isAnnotationPresent(Inbuilt.class));
					Proxy proxy = m.getAnnotation(Proxy.class);
					for(String proxyMethod : proxy.methods().split(" "))
						instance.relationMap.put(proxyMethod, m);
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Annotation to suggest that a method is used as a proxy for other methods
	 * @author guru
	 *
	 */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Proxy {
		String methods() default "";
	}
	
	
	/**
	 * Annotation to suggest that a method is built-in
	 * @author guru
	 *
	 */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Inbuilt {
	}
	
	
	/**
	 * Container class used to store logic for processing built-in relations
	 * @author guru
	 *
	 */
	private static class InbuiltRelations {
		
		/**
		 * Built-in method to handle all direction related queries
		 * @param pattern {@code IPattern} object specifying the pattern to test for
		 * @param result {@code Result} containing previous results
		 * @param objectList {@code Column} containing objects to iterate over 
		 * @return {@code Result} containing the various valid sets
		 */
		@Inbuilt
		@Proxy(methods = "toeast towest tonorth tosouth")
		public static Result direction(IPattern pattern, Result result, Column objectList) {
			Relation relation 	= pattern.relation();
			Result subResult 	= Result.newCopy(result);
			
//			Copy over any missing columns
			for(Reference r : pattern.references()) {
				if(!subResult.contains(r.toString()))
					subResult.add(new Column(r));
			}
			
//			Obtain one of the columns
			int columnIndex = subResult.indexOf(pattern.lhs().toString());
			
			
			if(!relation.regex().equals(Relation.Regex.NONE)) {
				List<IWorldTree> row = new ArrayList<IWorldTree>(subResult.size());
				int rowIndex = 0;
				while(rowIndex < objectList.size()) {
					IWorldTree node = objectList.get(rowIndex);
					
					if(result.size() >= 2) {
						row.addAll(result.getRow(rowIndex));
						row.add(columnIndex, node);
						assert subResult.size() == row.size() : "Trying to insert " + row.toString() + " into \n" + result.toString();
					}
					else {
						row.add(node);
						row.add(node);
					}
					
					subResult.add(row);
					row.clear();
					rowIndex++;
				}
			}
			
			int objIndex = 0;
			while(objIndex < objectList.size()) {
				IWorldTree node = objectList.get(objIndex);
				
				IWorldTree dNode = null;
				
//				Find the neighbour..we are the LHS..so we invert the directions
//				In A TOEAST B, LHS = A, thus B is actually to the west
				switch(InbuiltRelationEnum.check(relation.name())) {
				case BEGIN:
					break;
				case END:
					break;
				case TO_EAST:
					dNode = node.neighbour(Direction.E);
					break;
				case TO_NORTH:
					dNode = node.neighbour(Direction.N);
					break;
				case TO_SOUTH:
					dNode = node.neighbour(Direction.S);
					break;
				case TO_WEST:
					dNode = node.neighbour(Direction.W);
					break;
				default:
					throw new IllegalStateException(relation.name() + " resolved to inbuilt?!\n");
				}
				
				if(dNode != null) {
					switch(relation.regex()) {
					case NONE:
						List<IWorldTree> row = new ArrayList<IWorldTree>(subResult.size());
						if(result.size() >= 2) {
							row.addAll(result.getRow(objIndex));
							row.add(columnIndex, dNode);
						}
						else {
							row.add(columnIndex, dNode);
							row.add(node);
						}
						subResult.add(row);
						break;
					case PLUS:
					case STAR:
						Column recursiveList 	= new Column(objectList.name());
						recursiveList.add(dNode);
						Result recursiveResult 	= direction(pattern, result, recursiveList);
						int rows = recursiveResult.get(0).size();
						
						row = new ArrayList<IWorldTree>(subResult.size());
						for(int index = 0; index < rows; index++) {
							if(result.size() >= 2) {
								row.addAll(result.getRow(objIndex));
								row.add(recursiveResult.getRow(index).get(columnIndex));
							}
							else {
								row.add(columnIndex, recursiveResult.get(columnIndex).get(index));
								row.add(node);
							}
							subResult.add(row);
							row.clear();
						}
						break;
					default:
						break;
					}
				}
				objIndex++;
			}
			return subResult;
		}
	}
	
	
	/**
	 * Container class used to store logic for processing built-in properties
	 * @author guru
	 *
	 */
	private static class InbuiltProperties {
		
		/**
		 * Built-in method to handle all in-built properties related conditions
		 * @param node {@code IWorldTree} which is to be checked
		 * @param property {@code Property} to check for 
		 * @return <b> true </b> if node contains the specified property <br>
		 * <b> false </b> otherwise
		 * 
		 */
		@Inbuilt
		@Proxy(methods = "passableeast passablewest passablenorth passablesouth")
		public static boolean passable(IWorldTree node, ICondition condition) {
			Property property = condition.property();
			switch(Property.InbuiltPropertyEnum.check(property)) {
			case PASSABLE_EAST:
				ITile tile = (ITile) node;
				return tile.piece().hasInterface(TileInterfaceType.R);
			case PASSABLE_WEST:
				tile = (ITile) node;
				return tile.piece().hasInterface(TileInterfaceType.L);
			case PASSABLE_NORTH:
				tile = (ITile) node;
				return tile.piece().hasInterface(TileInterfaceType.U);
			case PASSABLE_SOUTH:
				tile = (ITile) node;
				return tile.piece().hasInterface(TileInterfaceType.D);
			default:
				throw new IllegalStateException("Should not be reaching default case in switch!");
			}
		}
		
		@Inbuilt
		@Proxy(methods = "name absolutename")
		public static boolean name(IWorldTree node, ICondition condition) {
			Property property = condition.property();
			TokenCmpOp operator = condition.operator();
			switch(Property.InbuiltPropertyEnum.check(property)) {
			case NAME:
				switch(operator) {
				case EQ:
					if(node.name().equals(condition.value()))
						return true;
					break;
				case NOTEQ:
					if(!node.name().equals(condition.value()))
						return true;
					break;
				default:
					throw new IllegalStateException("Inbuilt property 'name' currently does not handle operator " + operator);
				}
				break;
			case ABSOLUTENAME:
				switch(operator) {
				case EQ:
					if(node.absoluteName().equals(condition.value()))
						return true;
					break;
				case NOTEQ:
					if(!node.absoluteName().equals(condition.value()))
						return true;
					break;
				default:
					throw new IllegalStateException("Inbuilt property 'name' currently does not handle operator " + operator);
				}
				break;
			default:
				throw new IllegalStateException("Invalid method for handling property " + property);
			
			}
			return false;
		}
	}
}
