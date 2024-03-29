package internal.parser.containers.pattern;

import java.util.Collection;

import internal.parser.containers.IContainer;
import internal.parser.containers.Reference;
import internal.parser.containers.Relation;

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
	
	/**
	 * Obtain a {@code Collection<Reference>} of the references referred by this {@code IPattern}
	 * @return {@code Collection<Reference>} containing all the references referred to by this {@code IPattern}
	 */
	public Collection<Reference> references();
	
	
//	Setters
	
	/**
	 * Set the {@code Reference} for the LHS
	 * @param reference {@code Reference} to be set
	 */
	public void setLhs(Reference reference);
	
	/**
	 * Set the {@code Reference} for the RHS
	 * @param reference {@code Reference} to be set
	 */
	public void setRhs(Reference reference);
	
	/**
	 * Set the {@code Relation} between the two references
	 * @param relation {@code Relation} to be set
	 */
	public void setRelation(Relation relation);
	
	/**
	 * Set the sub-pattern(if any)
	 * @param subPattern {@code IPattern} to be set as sub-pattern
	 */
	public void	setSubPattern(IPattern subPattern);
}
