package internal.parser.containers.property;

import internal.parser.containers.Statement;
import internal.parser.containers.IContainer;
import internal.parser.containers.StatementType;
import internal.parser.containers.condition.ICondition;
import internal.parser.containers.expr.IExpr;
import internal.parser.containers.query.IQuery;

/**
 * Container class for property definitions <br>
 * <pre>
 * PROPERTYDEF := 
 *           ‘DEFINE’ LEVEL REF `.’ property ‘AS’ f(REF, REF, …) ‘IN’ QUERY
 *         | ‘DEFINE’ LEVEL REF `.’ property ‘AS’ ‘AGGREGATE’ f(REF, REF, …) ‘IN’ QUERY
 *         | ‘DEFINE’ LEVEL REF `.’ property ‘AS’ ‘RANDOM’ RANDOMSPEC ‘WHERE’ CONDITION
 *         | ‘INHERIT’ LEVEL REF `.’ property ‘FROM’ ‘PARENT’</pre>
 * @author guru
 *
 */
public class PropertyDef extends Statement {
	private String level, aggType, parent;
	private Property property;
	private ICondition condition;
	private IExpr expr;
	private IQuery query;
	private RandomSpec random;
	private Type type;

	public PropertyDef(Type type, String level, Property property, String agg, RandomSpec random, 
			String parent, IExpr expr, ICondition condition, IQuery query) {
		super(StatementType.PROPERTYDEF);
		this.type		= type;
		this.level		= level;
		this.property	= property;
		this.expr		= expr;
		this.condition	= condition;
		this.aggType	= agg;
		this.random		= random;
		this.parent		= parent;
		this.query		= query;
	}
	
	public PropertyDef(String level, Property property, IExpr expr, ICondition condition, IQuery query) {
		this(Type.BASIC, level, property, null, null, null, expr, condition, query);
	}
	public PropertyDef(String level, Property property, String agg, ICondition condition, IQuery query) {
		this(Type.AGGREGATE, level, property, agg, null, null, null, condition, query);
	}
	
	public PropertyDef(String level, Property property, RandomSpec random, ICondition condition) {
		this(Type.RANDOM, level, property, null, random, null, null, condition, null);
	}
	
	public PropertyDef(String level, Property property, String parent) {
		this(Type.INHERIT, level, property, null, null, parent, null, null, null);
	}

	@Override
	public String debugString() {
		StringBuffer result = null;
		switch(type) {
		case AGGREGATE:
			result = new StringBuffer("PROPERTYDEF(DEFINE " + level + " ");
			result.append(property.debugString() + " AS " + aggType + "(" + condition.debugString() + 
					")" + " IN " + query.debugString() + ")");
			break;
		case BASIC:
			result = new StringBuffer("PROPERTYDEF(DEFINE " + level + " " + property.debugString() + "." + property + " AS ");
			if(condition != null)
				result.append(condition.debugString());
			else
				result.append(expr.debugString());
			result.append(" IN " + query.debugString() + ")");
			break;
		case INHERIT:
			result = new StringBuffer("PROPERTYDEF(INHERIT " + level + " ");
			result.append(property.debugString() + " FROM " + parent + ")");
			break;
		case RANDOM:
			result = new StringBuffer("PROPERTYDEF(DEFINE " + level + " ");
			result.append(property.debugString() + " AS " + random.debugString() + " WHERE " + 
				condition.debugString() + ")");
		}
		
		return result.toString();
	}
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		switch(type) {
		case AGGREGATE:
			result.append("DEFINE " + level + " " + property + " AS " + aggType + 
					"(" + condition + ") IN " + query);
		case BASIC:
			result.append("DEFINE " + level + " " + property + " AS " + 
				"(" + condition + ") IN " + query);
		case INHERIT:
			result.append("INHERIT " + level + " " + property + " FROM " + parent);
		case RANDOM:
			result.append("DEFINE " + level + property + " AS " + random + " WHERE " + condition);
		}
		return result.toString();
	}
	
	public enum Type {
		AGGREGATE,
		RANDOM,
		BASIC,
		INHERIT,
		;
	}
	
	public static class RandomSpec implements IContainer {
		String dataType;
		float low, high;
		
		public RandomSpec(String dataType, String low, String high) {
			this.dataType	= dataType;
			this.low		= Float.parseFloat(low);
			this.high		= Float.parseFloat(high);
		}
		
		@Override
		public String debugString() {
			return "RANDOMSPEC(UNIFORM " + dataType + " FROM " + low + " TO " + high + ")";	
		}
		
		@Override
		public String toString() {
			return "UNIFORM " + dataType + " FROM " + low + " TO " + high;
		}
	}
}
