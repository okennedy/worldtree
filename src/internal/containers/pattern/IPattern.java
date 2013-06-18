package internal.containers.pattern;

import internal.containers.IContainer;
import internal.containers.Reference;
import internal.containers.Relation;

public interface IPattern extends IContainer {
	
	/**
	 * Obtain the {@code Reference} on the LHS
	 * @return {@code Reference}
	 */
	public Reference lhs();
	
	/**
	 * Obtain the {@code Reference} on the RHS
	 * @return {@code Reference}
	 */
	public Reference rhs();
	
	/**
	 * Obtain the {@code Relation} between the references
	 * @return {@code Relation}
	 */
	public Relation  relation();
	
	/**
	 * Obtain the sub-pattern(if any)
	 * @return {@code IPattern} if there exists a sub-pattern <br>
	 * <b>null</b> otherwise
	 */
	public IPattern  subPattern();
}
