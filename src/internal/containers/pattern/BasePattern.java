package internal.containers.pattern;

import internal.containers.Reference;
import internal.containers.Relation;

public class BasePattern implements IPattern {
	private Reference r1, r2;
	private Relation relation;
	
	public BasePattern(Reference r1, Relation relation, Reference r2) {
		this.r1			= r1;
		this.relation	= relation;
		this.r2			= r2;
	}
	
	@Override
	public String toString() {
		return r1.toString() + " " + relation.toString() + " " + r2.toString();
	}
	
	@Override
	public String debugString() {
		return "PATTERN(" + r1.debugString() + " " + relation.debugString() + " " + r2.debugString() + ")";
	}

}
