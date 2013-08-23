package internal.parser.containers.query;

import internal.Helper.Hierarchy;
import internal.parser.containers.IStatement;
import internal.parser.containers.condition.ICondition;
import internal.parser.containers.pattern.IPattern;

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
	 * @return {@code Hierarchy} specifying the hierarchical level in the WorldTree 
	 */
	public Hierarchy level();
	
	
//	Setters
	
	/**
	 * Set the pattern of this {@code IQuery}
	 * @param pattern {@code IPattern} to be set
	 */
	public void setPattern(IPattern pattern);
	
	/**
	 * Set the condition defined in this {@code IQuery}
	 * @param condition {@code ICondition} to be set
	 */
	public void setCondition(ICondition condition);
	
	/**
	 * Set the sub-query(if any)
	 * @param subQuery {@code IQuery} to be set as sub-query
	 */
	public void setSubQuery(IQuery subQuery);
}
