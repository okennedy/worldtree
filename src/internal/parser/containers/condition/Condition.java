package internal.parser.containers.condition;

import internal.parser.TokenCmpOp;
import internal.parser.containers.property.Property;

/**
 * Container class for storing a condition <br>
 * COND = BASECONDITION ((‘AND’ | ‘OR’) BASECONDITION)*
 * @author guru
 *
 */
public class Condition implements ICondition {
	private ICondition baseCondition;
	UnionType unionType;
	private ICondition subCondition;
	
	public Condition(boolean not, Property property, TokenCmpOp op, String value, UnionType unionType, ICondition subCondition) {
		this.baseCondition	= new BaseCondition(not, property, op, value);
		this.unionType		= unionType;
		this.subCondition	= subCondition;
	}
	
	public Condition(boolean not, ICondition condition) {
		this.baseCondition	= new BaseCondition(not, condition.property(), condition.operator(), condition.value());
		this.subCondition	= condition.subCondition();
	}
	
	public Condition(ICondition baseCondition, UnionType unionType, ICondition subCondition) {
		this.baseCondition	= baseCondition;
		this.unionType		= unionType;
		this.subCondition	= subCondition;
	}

	@Override
	public Property property() {
		return baseCondition.property();
	}

	@Override
	public ICondition subCondition() {
		return subCondition;
	}
	
	@Override
	public TokenCmpOp operator() {
		return baseCondition.operator();
	}

	@Override
	public String value() {
		return baseCondition.value();
	}
	
	@Override
	public String toString() {
		return baseCondition + " " + unionType + " " + subCondition;
	}

	@Override
	public String debugString() {
		StringBuffer result = new StringBuffer("CONDITION(" + baseCondition.debugString());
		
		if(subCondition != null)
			result.append(" " + unionType + " " + subCondition.debugString());

		result.append(")");
		return result.toString();
	}
}
