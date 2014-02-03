package internal.parser.containers.expr;

import internal.parser.TokenArithOp;
import internal.parser.containers.Datum;
import internal.parser.containers.IContainer;
import internal.parser.containers.Reference;
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
	 * Obtain the sub-expression(if any)
	 * @return {@code IExpr} if there exists a sub-expression <br>
	 * <b>null</b> otherwise
	 */
	
	public Reference reference();
	
	public Property property();
	
	public TokenArithOp operator();
	
	public Datum value();
	
	public IExpr subExpr();
	
	public String maxminType();
	
	public IExpr whenExpr();
	
	public IExpr elseExpr();

//	FIXME: This method should be moved out of IExpr to maintain clean separation
	public Datum evaluate(IWorldTree node, Result result);
}
