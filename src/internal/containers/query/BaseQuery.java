package internal.containers.query;

import internal.containers.condition.ICondition;
import internal.containers.pattern.IPattern;

public class BaseQuery implements IQuery {
	private IPattern pattern;
	private ICondition condition;
	
	public BaseQuery(IPattern pattern, ICondition condition) {
		this.pattern	= pattern;
		this.condition	= condition;
	}
	
	@Override
	public String toString() {
		return pattern.toString() + " WHERE " + condition.toString();
	}

	@Override
	public String debugString() {
		return "QUERY(" + pattern.debugString() + " WHERE " + condition.debugString() + ")";
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
}
