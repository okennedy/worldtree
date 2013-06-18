package internal.containers.condition;

import internal.containers.property.Property;

/**
 * Container class for storing a condition <br>
 * BASECONDITION := PROPERTY | ‘NOT’ BASECONDITION | ‘(’ CONDITION ‘)’
 * @author guru
 *
 */
public class BaseCondition implements ICondition {
	private boolean not;
	private Property property;
	
	public BaseCondition(boolean not, Property property) {
		this.not		= not;
		this.property	= property;
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
		
		returnString.append(property.toString());
		return returnString.toString();
	}

	@Override
	public String debugString() {
		StringBuffer result = new StringBuffer("CONDITION(");
		
		if(not)
			result.append("NOT ");
		
		result.append(property.debugString());
		result.append(")");
		
		return result.toString();
	}
}
