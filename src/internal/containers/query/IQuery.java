package internal.containers.query;

import internal.containers.IContainer;
import internal.containers.condition.ICondition;
import internal.containers.pattern.IPattern;

public interface IQuery extends IContainer {
	public IPattern pattern();
	public ICondition condition();
	public IQuery subQuery();
}
