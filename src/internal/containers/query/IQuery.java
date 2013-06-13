package internal.containers.query;

import internal.containers.IStatement;
import internal.containers.condition.ICondition;
import internal.containers.pattern.IPattern;

public interface IQuery extends IStatement {
	public IPattern pattern();
	public ICondition condition();
	public IQuery subQuery();
}
