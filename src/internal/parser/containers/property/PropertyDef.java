package internal.parser.containers.property;

import internal.parser.containers.Reference;
import internal.parser.containers.Statement;
import internal.parser.containers.IContainer;
import internal.parser.containers.StatementType;
import internal.parser.containers.condition.ICondition;
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
	private String level, property, aggType, parent;
	private Reference reference;
	private ICondition condition;
	private IQuery query;
	private RandomSpec random;
	private Type type;

	public PropertyDef(String level, Reference reference, String property, ICondition condition, IQuery query) {
		super(StatementType.PROPERTYDEF);
		this.type		= Type.BASIC;
		this.level		= level;
		this.reference	= reference;
		this.property	= property;
		this.condition	= condition;
		this.query		= query;
	}
	
	public PropertyDef(String level, Reference reference, String property, String agg, ICondition condition, IQuery query) {
		super(StatementType.PROPERTYDEF);
		this.type		= Type.AGGREGATE;
		this.level		= level;
		this.reference	= reference;
		this.property	= property;
		this.aggType	= agg;
		this.condition	= condition;
		this.query		= query;
	}
	
	public PropertyDef(String level, Reference reference, String property, RandomSpec random, ICondition condition) {
		super(StatementType.PROPERTYDEF);
		this.type		= Type.RANDOM;
		this.level		= level;
		this.reference	= reference;
		this.property	= property;
		this.random		= random;
		this.condition	= condition;
	}
	
	public PropertyDef(String level, Reference reference, String property, String parent) {
		super(StatementType.PROPERTYDEF);
		this.type		= Type.INHERIT;
		this.level		= level;
		this.reference	= reference;
		this.property	= property;
		this.parent		= parent;
	}

	@Override
	public String debugString() {
		StringBuffer result = null;
		switch(type) {
		case AGGREGATE:
			result = new StringBuffer("PROPERTYDEF(DEFINE " + level + " ");
			result.append(reference.debugString() + "." + property + " AS " + aggType + "(" + condition.debugString() + 
					")" + " IN " + query.debugString() + ")");
			break;
		case BASIC:
			result = new StringBuffer("PROPERTYDEF(DEFINE " + level + " ");
			result.append(reference.debugString() + "." + property + " AS " + condition.debugString() + 
				" IN " + query.debugString() + ")");
			break;
		case INHERIT:
			result = new StringBuffer("PROPERTYDEF(INHERIT " + level + " ");
			result.append(reference.debugString() + "." + property + " FROM " + parent + ")");
			break;
		case RANDOM:
			result = new StringBuffer("PROPERTYDEF(DEFINE " + level + " ");
			result.append(reference.debugString() + "." + property + " AS " + random.debugString() + " WHERE " + 
				condition.debugString() + ")");
		}
		
		return result.toString();
	}
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		switch(type) {
		case AGGREGATE:
			result.append("DEFINE " + level + " " + reference + "." + property + " AS " + aggType + 
					"(" + condition + ") IN " + query);
		case BASIC:
			result.append("DEFINE " + level + " " + reference + "." + property + " AS " + 
				"(" + condition + ") IN " + query);
		case INHERIT:
			result.append("INHERIT " + level + " " + reference + "." + property + " FROM " + parent);
		case RANDOM:
			result.append("DEFINE " + level + reference + "." + property + " AS " + random + " WHERE " + condition);
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
