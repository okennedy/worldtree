package internal.parser.containers.expr;

import internal.parser.TokenArithOp;
import internal.parser.containers.IContainer;
import internal.parser.containers.expr.Expr.ExprType;

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
	
	public TokenArithOp operator();
	
	public String value();
	
	public IExpr subExpr();
	
	public String maxminType();
	
	public IExpr whenExpr();
	
	public IExpr elseExpr();
}
