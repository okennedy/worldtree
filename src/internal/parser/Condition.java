package internal.parser;

public class Condition {
	private boolean not;
	private Property property;
	private Condition subCondition;
	
	Condition(boolean not, Property property, Condition subCondition) {
		this.property		= property;
		this.subCondition	= subCondition;
	}
	
	Condition(boolean not, Condition condition) {
		this.not			= true;
		this.property		= condition.property;
		this.subCondition	= condition.subCondition;
	}
}
