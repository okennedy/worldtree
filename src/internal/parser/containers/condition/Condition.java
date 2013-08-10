package internal.parser.containers.condition;

import internal.parser.TokenCmpOp;
import internal.parser.containers.Datum;
import internal.parser.containers.condition.BaseCondition.ConditionType;
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
	
	public Condition(boolean not, ICondition condition) {
		this.baseCondition	= new BaseCondition(not, condition.type(), condition.property(), condition.operator(), condition.value());
		this.subCondition	= condition.subCondition();
	}
	
	public Condition(ICondition baseCondition, UnionType unionType, ICondition subCondition) {
		this.baseCondition	= baseCondition;
		this.unionType		= unionType;
		this.subCondition	= subCondition;
	}
	
	@Override
	public Boolean notFlag() {
		return baseCondition.notFlag();
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
	public Datum value() {
		return baseCondition.value();
	}
	
	@Override
	public void setValue(Datum value) {
		baseCondition.setValue(value);
	}
	
	@Override
	public ConditionType type() {
		return ConditionType.COMPLEX;
	}
	

	@Override
	public void setNotFlag(Boolean flag) {
		baseCondition.setNotFlag(flag);
	}

	@Override
	public void setProperty(Property property) {
		baseCondition.setProperty(property);
	}

	@Override
	public void setSubCondition(ICondition subCondition) {
		this.subCondition = subCondition;
	}

	@Override
	public void setOperator(String op) {
		baseCondition.setOperator(op);
	}

	@Override
	public void setType(ConditionType type) {
		baseCondition.setType(type);
	}
	
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(baseCondition + " ");
		
		if(subCondition != null)
			result.append(unionType + " " + subCondition);

		return result.toString();
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
