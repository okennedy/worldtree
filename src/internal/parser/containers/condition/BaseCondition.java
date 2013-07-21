package internal.parser.containers.condition;

import internal.parser.TokenCmpOp;
import internal.parser.containers.property.Property;

/**
 * Container class for storing a condition <br>
 * BASECONDITION := PROPERTY | ‘NOT’ BASECONDITION | ‘(’ CONDITION ‘)’
 * @author guru
 *
 */
public class BaseCondition implements ICondition {
	private boolean not;
	private Property property;
	private TokenCmpOp cmpOp;
	private String value;
	
	public BaseCondition(boolean not, Property property, TokenCmpOp op, String value) {
		this.not		= not;
		this.property	= property;
		this.cmpOp		= op;
		this.value		= value;
	}
	
	@Override
	public Property property() {
		return property;
	}

	@Override
	public ICondition subCondition() {
		return null;
	}
	
	@Override
	public String toString() {
		StringBuffer returnString = new StringBuffer();
		if(not)
			returnString.append("NOT ");
		
		returnString.append(property.toString() + " " + cmpOp + " " + value);
		return returnString.toString();
	}

	@Override
	public String debugString() {
		StringBuffer result = new StringBuffer("CONDITION(");
		
		if(not)
			result.append("NOT ");
		
		result.append(property.debugString() + " " + cmpOp + " " + value);
		result.append(")");
		
		return result.toString();
	}

	@Override
	public TokenCmpOp operator() {
		return cmpOp;
	}

	@Override
	public String value() {
		return value;
	}
}
