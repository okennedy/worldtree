package internal.containers;

import internal.containers.condition.Condition;
import internal.containers.condition.ICondition;
import internal.containers.pattern.Pattern;

public class Query {
	private Pattern pattern;
	private ICondition condition;
	
	public Query(Pattern pattern, ICondition condition) {
		this.pattern	= pattern;
		this.condition	= condition;
	}
}
