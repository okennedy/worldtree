package internal.containers.pattern;

import internal.containers.Reference;
import internal.containers.Relation;

public class Pattern implements IPattern {
	private IPattern basePattern;
	private IPattern subPattern;
	
	public Pattern(Reference r1, Relation relation, Reference r2, Pattern subPattern) {
		this.basePattern	= new BasePattern(r1, relation, r2);
		this.subPattern		= subPattern;
	}
	
	public Pattern(IPattern basePattern, IPattern subPattern) {
		this.basePattern	= basePattern;
		this.subPattern		= subPattern;
	}

	@Override
	public String debugString() {
		StringBuffer result = new StringBuffer("PATTERN(");
		
		result.append(basePattern.debugString());
		
		if(subPattern == null) {
			result.append(")");
			return result.toString();
		}
		
		else {
			result.append(" , " + subPattern.debugString());
			result.append(")");
			return result.toString();
		}
	}
}
