package internal.parser.containers.condition;

import internal.parser.TokenCmpOp;
import internal.parser.containers.Datum;
import internal.parser.containers.property.Property;

/**
 * Container class for storing a condition <br>
 * BASECONDITION := PROPERTY | ‘NOT’ BASECONDITION | ‘(’ CONDITION ‘)’
 * @author guru
 *
 */
public class BaseCondition implements ICondition {
	private boolean not;
	private ConditionType type;
	private Property property;
	private TokenCmpOp cmpOp;
	private Datum value;
	
	public BaseCondition(boolean not, ConditionType type, Property property, TokenCmpOp op, Datum value) {
		this.not		= not;
		this.type		= type;
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
	public ConditionType type() {
		return type;
	}
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		if(not)
			result.append("NOT ");
		
		result.append(property.toString());
		switch(type) {
		case BASIC:
			result.append(" " + cmpOp + " " + value);
			break;
		case BOOLEAN:
			break;
		case COMPLEX:
			break;
		default:
			break;
		}
		
		return result.toString();
	}

	@Override
	public String debugString() {
		StringBuffer result = new StringBuffer("CONDITION(");
		
		if(not)
			result.append("NOT ");
		
		result.append(property.debugString());
		switch(type) {
		case BASIC:
			result.append(" " + cmpOp + " " + value);
			break;
		case BOOLEAN:
			break;
		case COMPLEX:
			break;
		default:
			break;
		}
		
		return result.toString();
	}

	@Override
	public TokenCmpOp operator() {
		return cmpOp;
	}

	@Override
	public Datum value() {
		return value;
	}
	
	public enum ConditionType {
		BASIC,
		BOOLEAN,
		COMPLEX,
		;
	}
}
