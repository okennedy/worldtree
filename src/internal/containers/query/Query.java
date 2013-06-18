package internal.containers.query;

import internal.containers.Statement;
import internal.containers.StatementType;
import internal.containers.condition.ICondition;
import internal.containers.pattern.IPattern;

/**
 * Container class for storing a query <br>
 * QUERY := AT LEVEL PATTERN (‘WHERE’ CONDITION)? (‘UNION’ QUERY)?
 * @author guru
 *
 */
public class Query extends Statement implements IQuery {
	private IQuery baseQuery;
	private IQuery subQuery;
	
	public Query(String level, IPattern pattern, ICondition condition, Query subQuery) {
		super(StatementType.QUERY);
		this.baseQuery	= new BaseQuery(level, pattern, condition);
		this.subQuery	= subQuery;
	}
	
	public Query(IQuery baseQuery, IQuery subQuery) {
		super(StatementType.QUERY);
		this.baseQuery	= baseQuery;
		this.subQuery	= subQuery;
	}
	
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(baseQuery.toString());
		
		if(subQuery != null)
			result.append(" UNION " + subQuery.toString());
		
		return result.toString();
		
	}
	
	@Override
	public String debugString() {
		StringBuffer result = new StringBuffer("QUERY(" + baseQuery.debugString());
		
		if(subQuery != null)
			result.append(" UNION " + subQuery.debugString());
		
		result.append(")");
		return result.toString();
	}


	@Override
	public IPattern pattern() {
		return baseQuery.pattern();
	}


	@Override
	public ICondition condition() {
		return baseQuery.condition();
	}


	@Override
	public IQuery subQuery() {
		return subQuery;
	}

	@Override
	public Class<?> level() {
		return baseQuery.level();
	}
}
