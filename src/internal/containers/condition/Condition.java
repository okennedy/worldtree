package internal.containers.condition;

import internal.containers.Property;

public class Condition implements ICondition {
	private ICondition baseCondition;
	private ICondition subCondition;
	
	public Condition(boolean not, Property property, ICondition subCondition) {
		this.baseCondition	= new BaseCondition(not, property);
		this.subCondition	= subCondition;
	}
	
	public Condition(boolean not, ICondition condition) {
		this.baseCondition	= new BaseCondition(not, condition.property());
		this.subCondition	= condition.subCondition();
	}
	
	public Condition(ICondition baseCondition, ICondition subCondition) {
		this.baseCondition	= baseCondition;
		this.subCondition	= subCondition;
	}

	@Override
	public String statement() {
		return baseCondition.statement() + " " + subCondition.statement();
	}

	@Override
	public Property property() {
		return baseCondition.property();
	}

	@Override
	public ICondition subCondition() {
		return subCondition;
	}
}
