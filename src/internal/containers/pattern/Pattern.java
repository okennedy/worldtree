package internal.containers.pattern;

import internal.containers.Property;
import internal.containers.Reference;
import internal.containers.Relation;
import internal.containers.condition.ICondition;

public class Pattern implements IPattern {
	private Reference r1, r2;
	private Relation relation;
	private Pattern subPattern;
	
	public Pattern(Reference r1, Relation relation, Reference r2, Pattern subPattern) {
		this.r1			= r1;
		this.relation	= relation;
		this.r2			= r2;
		this.subPattern	= subPattern;
	}

	@Override
	public String debugString() {
		StringBuffer result = new StringBuffer("PATTERN(");
		
		result.append(r1.debugString());
		
		if(relation == null) {
			result.append(")");
			return result.toString();
		}
		
		else
			result.append(" " + relation.debugString() + " " + r2.debugString());
		
		if(subPattern == null) {
			result.append(")");
			return result.toString();
		}
		
		else {
			result.append(" " + subPattern.debugString());
			result.append(")");
			return result.toString();
		}
	}
}
