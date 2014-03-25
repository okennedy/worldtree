package internal.parser.containers.expr;

import internal.parser.TokenArithOp;
import internal.parser.containers.Datum;
import internal.parser.containers.IContainer;
import internal.parser.containers.Reference;
import internal.parser.containers.condition.ICondition;
import internal.parser.containers.expr.Expr.ExprType;
import internal.parser.containers.property.Property;
import internal.parser.resolve.Result;
import internal.tree.IWorldTree;

public interface IExpr extends IContainer {
	
	/**
	 * Obtain the {@code ExprType} of this {@code IExpr}
	 * @return {@code ExprType}
	 */
	public ExprType type();
	
	/**
	 * Obtain the reference (if any)
	 * @return {@code Reference} 
	 */
	public Reference reference();
	
	/**
	 * Obtain the property (if any)
	 * @return {@code Property}
	 */
	public Property property();

	/**
	 * Obtain the arithmetic operator (if any) used in this expression
	 * @return {@code TokenArithOp}
	 */
	public TokenArithOp operator();

	/**
	 * Obtain the value (if any) specified in this expression
	 * @return {@code Datum}
	 */
	public Datum value();

	/**
	 * Obtain the sub-expression (if any)
	 * @return {@code IExpr}
	 */
	public IExpr subExpr();

	/**
	 * Obtain the max-min type if relevant
	 * @return {@code String}
	 */
	public String maxminType();

	/**
	 * Obtain the when-expression (if any)
	 * @return {@code IExpr}
	 */
	public IExpr whenExpr();

	/**
	 * Obtain the else-expression (if any)
	 * @return {@code IExpr}
	 */
	public IExpr elseExpr();

	/**
	 * Obtain the condition (if any)
	 * @return {@code ICondition}
	 */
	public ICondition condition();
	
//	FIXME: This method should be moved out of IExpr to maintain clean separation
	public Datum evaluate(IWorldTree node, Result result);
}
