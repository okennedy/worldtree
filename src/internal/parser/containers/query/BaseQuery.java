package internal.parser.containers.query;

import internal.Helper.Hierarchy;
import internal.parser.containers.Statement;
import internal.parser.containers.StatementType;
import internal.parser.containers.condition.ICondition;
import internal.parser.containers.pattern.IPattern;

/**
 * Container class for storing a query <br>
 * QUERY := AT LEVEL PATTERN (‘WHERE’ CONDITION)? (‘UNION’ QUERY)?
 * @author guru
 * 
 */
public class BaseQuery extends Statement implements IQuery {
	private Hierarchy level;
	private IPattern pattern;
	private ICondition condition;
	
	public BaseQuery(Hierarchy level, IPattern pattern, ICondition condition) {
		super(StatementType.QUERY);
		this.level		= level;
		this.pattern	= pattern;
		this.condition	= condition;
	}
	
	@Override
	public IPattern pattern() {
		return pattern;
	}

	@Override
	public ICondition condition() {
		return condition;
	}

	@Override
	public IQuery subQuery() {
		return null;
	}

	@Override
	public Hierarchy level() {
		return level;
	}

	
	@Override
	public void setPattern(IPattern pattern) {
		this.pattern = pattern;
	}

	@Override
	public void setCondition(ICondition condition) {
		this.condition = condition;
	}

	@Override
	public void setSubQuery(IQuery subQuery) {
		System.err.println("Calling method setSubQuery on type " + this.getClass().getName() + " is not supported");
	}
	
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(level + " " + pattern.toString());
		if(condition != null)
			result.append(" WHERE " + condition.toString());
		
		return result.toString();
	}

	@Override
	public String debugString() {
		StringBuffer result = new StringBuffer("QUERY(" + level + " " + pattern.debugString());
		
		if(condition != null)
			result.append(" WHERE " + condition.debugString() + ")");
		
		return result.toString();
	}

}
