package internal.containers.condition;

import internal.containers.Property;

public class Condition implements ICondition {
	private ICondition baseCondition;
	UnionType unionType;
	private ICondition subCondition;
	
	public Condition(boolean not, Property property, UnionType unionType, ICondition subCondition) {
		this.baseCondition	= new BaseCondition(not, property);
		this.unionType		= unionType;
		this.subCondition	= subCondition;
	}
	
	public Condition(boolean not, ICondition condition) {
		this.baseCondition	= new BaseCondition(not, condition.property());
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
	public String toString() {
		return baseCondition + " " + unionType + " " + subCondition;
	}

	@Override
	public String debugString() {
		StringBuffer result = new StringBuffer("CONDITION(" + baseCondition.debugString());
		
		if(subCondition == null) {
			result.append(")");
			return result.toString();
		}
		else {
			result.append(" " + unionType + " " + subCondition.debugString());
			result.append(")");
			return result.toString();
		}
	}
}
