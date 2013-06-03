package internal.parser;

public class Condition {
	private Property property;
	private Condition subCondition;
	
	Condition(Property property, Condition subCondition) {
		this.property		= property;
		this.subCondition	= subCondition;
	}
}
