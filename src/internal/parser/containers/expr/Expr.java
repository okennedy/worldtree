package internal.parser.containers.expr;

import internal.parser.TokenArithOp;
import internal.parser.containers.Datum;
import internal.parser.containers.condition.ICondition;
import internal.parser.containers.property.Property;

/**
 * Container class for expressions <br>
 * <pre>
 *   EXPR := 
 *     EXPR (�+� | �*� | �-� | �/�) EXPR
 *  | CONSTANT | REFERENCE �.� PROPERTY
 *  | (�MAX� | �MIN�) �(� EXPR (�,� EXPR)+ �)�
 *  | �CASE� (�WHEN� CONDITION �THEN� EXPR)* �ELSE� EXPR
 * </pre>
 * @author guru
 *
 */
public class Expr implements IExpr {
	private Datum value;
	private TokenArithOp operator;
	private IExpr baseExpr, subExpr;
	private String maxminType;
	private Property property;
	private ICondition condition;
	private ExprType exprType;
	private IExpr whenExpr, elseExpr;
	

	public Expr(ExprType exprType, Datum value, IExpr baseExpr, TokenArithOp operator, IExpr subExpr, String maxminType, 
			Property property, ICondition condition, IExpr whenExpr, IExpr elseExpr) {
		this.exprType	= exprType;
		this.value		= value;
		this.baseExpr	= baseExpr;
		this.operator	= operator;
		this.subExpr	= subExpr;
		this.maxminType	= maxminType;
		this.property	= property;
		this.condition	= condition;
		this.whenExpr	= whenExpr;
		this.elseExpr	= elseExpr;
	}
	
	public Expr(Datum value) {
		this(ExprType.BASIC, value, null, null, null, null, null, null, null, null);
	}
	
	public Expr(Property property) {
		this(ExprType.BASIC, null, null, null, null, null, property, null, null, null);
	}
	
	public Expr(IExpr baseExpr, TokenArithOp operator, IExpr subExpr) {
		this(ExprType.ARITH, null, baseExpr, operator, subExpr, null, null, null, null, null);
	}
	
	public Expr(String maxminType, IExpr baseExpr, IExpr subExpr) {
		this(ExprType.MAXMIN, null, baseExpr, null, subExpr, null, null, null, null, null);
	}
	
	public Expr(ICondition condition, IExpr whenExpr, IExpr elseExpr) {
		this(ExprType.WHEN, null, null, null, null, null, null, condition, whenExpr, elseExpr);
	}

	@Override
	public ExprType type() {
		return exprType;
	}
	
	@Override
	public Property property() {
		return property;
		
	}
	
	@Override
	public TokenArithOp operator() {
		return operator;
	}
	
	@Override
	public Datum value() {
		if(value != null)
			return value;
		else	//FIXME: This might cause issues as we return a new object every time value() is requested
			return new Datum.Str(property.toString());
	}

	@Override
	public IExpr subExpr() {
		return subExpr;
	}

	@Override
	public String maxminType() {
		return maxminType;
	}

	@Override
	public IExpr whenExpr() {
		return whenExpr;
	}

	@Override
	public IExpr elseExpr() {
		return elseExpr;
	}
	
	@Override
	public String debugString() {
		StringBuffer result = null;
		switch(exprType) {
		case ARITH:
			result = new StringBuffer("EXPR( " + baseExpr.debugString() + " " + operator + " " + subExpr.debugString() + " )");
			break;
		case BASIC:
			if(value != null)
				result = new StringBuffer("EXPR(" + value + ")");
			else
				result = new StringBuffer("EXPR(" + property + ")");
			break;
		case MAXMIN:
			result = new StringBuffer("EXPR(" + maxminType + "(" + baseExpr.debugString());
			if(subExpr != null)
				result.append(" " + subExpr.debugString() + " )");
			break;
		case WHEN:
			result = new StringBuffer("EXPR(CASE WHEN " + condition.debugString() + " THEN " + whenExpr.debugString() + 
					" ELSE " + elseExpr.debugString() + " )");
			break;
		}
		
		return result.toString();
	}
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		switch(exprType) {
		case ARITH:
			result.append(baseExpr + " " + operator + " " + subExpr);
			break;
		case BASIC:
			if(value != null)
				result.append(value);
			else
				result.append(property);
			break;
		case MAXMIN:
			result.append(maxminType + " (" + baseExpr);
			if(subExpr != null)
				result.append(" , " + subExpr);
			result.append(" )");
			break;
		case WHEN:
			result.append("CASE WHEN " + condition + " THEN " + whenExpr + " ELSE " + elseExpr);
			break;
		}
		return result.toString();
	}
	
	public enum ExprType {
		ARITH,
		BASIC,
		MAXMIN,
		WHEN,
		;
	}
}
