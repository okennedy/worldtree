package internal.parser.containers.expr;

import internal.parser.TokenArithOp;
import internal.parser.TokenCmpOp;
import internal.parser.containers.Datum;
import internal.parser.containers.Reference;
import internal.parser.containers.condition.ICondition;
import internal.parser.containers.property.Property;
import internal.parser.resolve.Result;
import internal.tree.IWorldTree;

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
	private IExpr subExpr;
	private String maxminType;
	private Reference reference;
	private Property property;
	private ICondition condition;
	private ExprType exprType;
	private IExpr whenExpr, elseExpr;
	

	public Expr(ExprType exprType, Datum value, IExpr baseExpr, TokenArithOp operator, IExpr subExpr, String maxminType, 
			Reference reference, Property property, ICondition condition, IExpr whenExpr, IExpr elseExpr) {
		this.exprType	= exprType;
		this.value		= value;
		this.operator	= operator;
		this.subExpr	= subExpr;
		this.maxminType	= maxminType;
		this.reference	= reference;
		this.property	= property;
		this.condition	= condition;
		this.whenExpr	= whenExpr;
		this.elseExpr	= elseExpr;
	}
	
	public Expr(Datum value) {
		this(ExprType.BASIC, value, null, null, null, null, null, null, null, null, null);
	}
	
	public Expr(Reference reference, Property property) {
		this(ExprType.BASIC, null, null, null, null, null, reference, property, null, null, null);
	}
	
	public Expr(IExpr baseExpr, TokenArithOp operator, IExpr subExpr) {
		this(ExprType.ARITH, baseExpr.value(), baseExpr, operator, subExpr, null, null, baseExpr.property(), null, null, null);
	}
	
	public Expr(String maxminType, IExpr baseExpr, IExpr subExpr) {
		this(ExprType.MAXMIN, null, baseExpr, null, subExpr, null, null, null, null, null, null);
	}
	
	public Expr(ICondition condition, IExpr whenExpr, IExpr elseExpr) {
		this(ExprType.WHEN, null, null, null, null, null, null, null, condition, whenExpr, elseExpr);
	}

	@Override
	public ExprType type() {
		return exprType;
	}
	
	@Override
	public Reference reference() {
		return reference;
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
	public Datum evaluate(IWorldTree node, Result result) {
		Datum baseValue = null;
		Datum subValue	= null;
		switch(exprType) {
		case ARITH:
			baseValue = evaluate(node, result);
			subValue 	= subExpr.evaluate(node, result);
			switch(operator) {
			case TK_DIV:
				return baseValue.divide(subValue);
			case TK_MINUS:
				return baseValue.subtract(subValue);
			case TK_MULT:
				return baseValue.multiply(subValue);
			case TK_PLUS:
				return baseValue.add(subValue);
			}
			break;
		case BASIC:
			if(value != null)
				return value;
			else {
				IWorldTree referenceNode = result.get(reference.toString()).get(0);	//FIXME: This is not the right thing to do..
				return referenceNode.properties().get(property);
			}
		case MAXMIN:
			baseValue	= evaluate(node, result);
			subValue	= subExpr.evaluate(node, result);
			if(maxminType.equalsIgnoreCase("MAX")) {
				if(baseValue.compareTo(subValue, TokenCmpOp.GT) == 0)
					return baseValue;
				else
					return subValue;
			}
			else {
				if(baseValue.compareTo(subValue, TokenCmpOp.LT) == 0)
					return baseValue;
				else
					return subValue;
			}
		case WHEN:
		default :
			throw new IllegalStateException("Unimplemented! expression type " + exprType);
		}
		return null;
	}
	
	@Override
	public String debugString() {
		StringBuffer result = null;
		switch(exprType) {
		case ARITH:
			result = new StringBuffer("EXPR( " + debugString() + " " + operator + " " + subExpr.debugString() + " )");
			break;
		case BASIC:
			if(value != null)
				result = new StringBuffer("EXPR(" + value + ")");
			else
				result = new StringBuffer("EXPR(" + reference.debugString() + "." + property.debugString() + ")");
			break;
		case MAXMIN:
			result = new StringBuffer("EXPR(" + maxminType + "(" + debugString());
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
			if(this.value != null)
				result.append(value + " " + operator + " " + subExpr);
			else if(this.reference != null && this.property != null)
				result.append(reference + "." + property + " " + operator + " " + subExpr);
			break;
		case BASIC:
			if(value != null)
				result.append(value);
			else
				result.append(reference + "." + property);
			break;
		case MAXMIN:
			result.append(maxminType + " (");	//TODO: Should something additional be appended here?
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
