package internal.parser.resolve;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import internal.containers.IStatement;
import internal.containers.Relation;
import internal.containers.pattern.IPattern;
import internal.containers.query.IQuery;
import internal.space.Space.Direction;
import internal.tree.IWorldTree;
import static test.ui.UIDebugEngine.multiLine;
import static test.ui.UIDebugEngine.pad;
import static test.ui.UIDebugEngine.write;
import static internal.containers.Relation.InbuiltRelationEnum;

/**
 * ResolutionEngine is a Singleton class responsible for evaluating statements issued by the user
 * @author guru
 *
 */
public class ResolutionEngine {
	private Map<String, Method> relationMap = new HashMap<String, Method>();
	private static ResolutionEngine instance = null;
	
	protected ResolutionEngine() {
//		Prevent initialization of ResolutionEngine
	}
	
	/**
	 * Evaluate an {@code IQuery}
	 * @param node {@code IWorldTree} object upon which the {@code IQuery} is to be evaluated
	 * @param query {@code IQuery} object containing the query to evaluate
	 * @return {@code String} representing the output of the {@code IQuery}
	 */
	public static String evaluate(IWorldTree node, IQuery query) {
		if(instance == null)
			init();
		return instance.resolve(node, query);
	}
	
	/**
	 * Initialize the instance
	 */
	private static void init() {
//		TODO: Figure out a nice way to add future methods similar to the way direction is being resolved
		instance = new ResolutionEngine();
		try {
			for(Method m : InbuiltRelations.class.getMethods()) {
				if(m.isAnnotationPresent(InbuiltRelations.Proxy.class)) {
					assert(m.isAnnotationPresent(InbuiltRelations.Inbuilt.class));
					InbuiltRelations.Proxy proxy = m.getAnnotation(InbuiltRelations.Proxy.class);
					for(String proxyMethod : proxy.methods().split(" "))
						instance.relationMap.put(proxyMethod, m);
				}
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Resolve method that is private to {@code ResolutionEngine} and used to evaluate an {@code IQuery}
	 * @param node {@code IWorldTree} object upon which the {@code IQuery} is to be evaluated
	 * @param query {@code IQuery} object containing the query to evaluate
	 * @return {@code String} representing the output of the {@code IQuery}
	 */
	private String resolve(IWorldTree node, IQuery query) {
		Class<?> level		= query.level();
		IPattern pattern	= query.pattern();
		Collection<Collection<IWorldTree>> result = null;
		while(pattern != null) {
//			TODO:Join?
			result = resolve(node, level, pattern);
			pattern = pattern.subPattern();
		}
		
		return makeString(query, result);
	}

	/**
	 * Helper method used to convert {@code Collection<Collection<IWorldTree>>} to a {@code String}
	 * @param statement {@code IStatement} representing the statement that is being evaluated
	 * @param result {@code Collection<Collection<IWorldTree>>} representing the collection that needs to be flattened
	 * @return {@code String} representing the flattened version of the parameter <b>result</b>
	 */
	private String makeString(IStatement statement, Collection<Collection<IWorldTree>> result) {
		StringBuffer sb = new StringBuffer(statement.toString() + "\n" + statement.debugString() + "\n\n");
		for(Collection<IWorldTree> collection : result) {
			List<String> stringList = new ArrayList<String>();
			
			for(IWorldTree obj : collection) {
				StringBuffer visual = new StringBuffer();
				List<String> stringRep = obj.getStringRepresentation();
				for(String line : stringRep) {
					visual.append(line + "\n");
				}
				stringList.add(obj.absoluteName() + "  \n" + visual.toString());
			}
			String multiline = multiLine(stringList);
			sb.append(multiline + "\n\n");
		}
		return sb.toString();
		
	}

	/**
	 * Resolve method that is specifically designed to handle {@code IQuery}
	 * @param node {@code IWorldTree} object upon which the {@code IQuery} is to be evaluated
	 * @param level {@code Class<?>} representing the hierarchical level of WorldTree
	 * @param pattern {@code IPattern} representing the pattern to search for
	 * @return {@code Collection<Collection<IWorldTree>>} satisfying the {@code IQuery}
	 */
	private Collection<Collection<IWorldTree>> resolve(IWorldTree node, Class<?> level, IPattern pattern) {
		List<IWorldTree> nodeList   = new ArrayList<IWorldTree>();
		List<IWorldTree> objectList = new ArrayList<IWorldTree>();
		Collection<Collection<IWorldTree>> result = null;
		
		nodeList.add(node);
		
//		Get collection of relevant objects
		IWorldTree currentNode = null;
		while(nodeList.size() > 0) {
			currentNode = nodeList.get(0);
			for(IWorldTree child : currentNode.children()) {
				if(child.getClass().equals(level))
					objectList.add(child);
				else
					nodeList.add(child);
			}
			nodeList.remove(currentNode);
		}
		
		Relation relation = pattern.relation();
		switch(relation.type()) {
		case CUSTOM:
			break;
		case INBUILT:
			Method method = null;
			try {	//FIXME
				method = instance.relationMap.get(relation.name().toLowerCase());
				result = (Collection<Collection<IWorldTree>>) method.invoke(null, relation, objectList);
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
	 * Container class used to store logic for processing built-in relations
	 * @author guru
	 *
	 */
	private static class InbuiltRelations {
		
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
		 * Built-in method to handle all direction related queries
		 * @param relation {@code Relation} object specifying the relation to test for
		 * @param nodeList {@code List<IWorldTree>} containing all the relevant objects to test
		 * @return {@code <Collection<Collection<IWorldTree>>} containing the various valid sets
		 */
		@Inbuilt
		@Proxy(methods = "toeast towest tonorth tosouth")
		public static Collection<Collection<IWorldTree>> direction(Relation relation, List<IWorldTree> nodeList) {
			Collection<Collection<IWorldTree>> result = new ArrayList<Collection<IWorldTree>>();
			Map<IWorldTree, List<List<IWorldTree>>> map = new LinkedHashMap<IWorldTree, List<List<IWorldTree>>>();
			
			for(IWorldTree node : nodeList)
				map.put(node, new ArrayList<List<IWorldTree>>());

			if(!relation.regex().equals(Relation.Regex.NONE)) {
				for(IWorldTree node : nodeList) {
					map.get(node).add(new ArrayList<IWorldTree>(Arrays.asList(new IWorldTree[]{node})));
				}
			}
			
//			TODO: Decide if A toeast B resolves as B,A or A,B...Currently resolves as B,A
			for(IWorldTree node : nodeList) {
				List<IWorldTree> subResult = new ArrayList<IWorldTree>();
				subResult.add(node);
				IWorldTree dNode = null;
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
				
//				If null, we still need to handle *
				if(dNode != null) {
					subResult.add(dNode);	//Regardless of regex type, we need to add this set to the collection 
//					map.get(node).add(new ArrayList<IWorldTree>(subResult));
					switch(relation.regex()) {
					case NONE:
						map.get(node).add(new ArrayList<IWorldTree>(subResult));
						continue;	//We got a match! Continue with next node
					case PLUS:
						subResult.remove(0);	//Remove first element to avoid infinite recursion
						for(IWorldTree subNode : subResult) {	//FIXME
							Collection<Collection<IWorldTree>> recursiveResult = direction(relation, subResult);
							for(Collection<IWorldTree> col : recursiveResult) {
								List<IWorldTree> subCollectionList = new ArrayList<IWorldTree>();
								subCollectionList.add(node);
								subCollectionList.addAll(col);
								map.get(node).add(subCollectionList);
							}
						}
						break;
					case STAR:
//						Need to recursively find all recursive sets
						subResult.remove(0);	//Remove first element to avoid infinite recursion
						for(IWorldTree subNode : subResult) {	//FIXME
							Collection<Collection<IWorldTree>> recursiveResult = direction(relation, subResult);
							for(Collection<IWorldTree> col : recursiveResult) {
								List<IWorldTree> subCollectionList = new ArrayList<IWorldTree>();
								subCollectionList.add(node);
								subCollectionList.addAll(col);
								map.get(node).add(subCollectionList);
							}
						}
						break;
					}
				}
			}
			
//			The actual return logic
			for(Map.Entry<IWorldTree, List<List<IWorldTree>>> entry : map.entrySet())
				result.addAll(entry.getValue());
			
			return result;
		}
	}
}
