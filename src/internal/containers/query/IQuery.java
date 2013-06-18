package internal.containers.query;

import internal.containers.IStatement;
import internal.containers.condition.ICondition;
import internal.containers.pattern.IPattern;

public interface IQuery extends IStatement {
	
	/**
	 * Obtain the pattern of this {@code IQuery}
	 * @return {@code IPattern} containing the pattern stored in this {@code IQuery}
	 */
	public IPattern pattern();
	
	/**
	 * Obtain the condition defined in this {@code IQuery}
	 * @return {@code ICondition} containing the condition stored in this {@code IQuery}
	 */
	public ICondition condition();
	
	/**
	 * Obtain the sub-query(if any)
	 * @return {@code IQuery} if there exists a sub-query <br>
	 * <b>null</b> otherwise
	 */
	public IQuery subQuery();
	
	/**
	 * Obtain the level on which this {@code IQuery} is to be resolved
	 * @return {@code Class<?>} specifying the hierarchical level in the WorldTree 
	 */
	public Class<?> level();
}
