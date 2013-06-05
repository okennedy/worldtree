package internal.containers.pattern;

import internal.containers.Reference;
import internal.containers.Relation;

public class Pattern {
	private Reference r1, r2;
	private Relation relation;
	private Pattern subPattern;
	
	Pattern(Reference r1, Relation relation, Reference r2, Pattern subPattern) {
		this.r1			= r1;
		this.relation	= relation;
		this.r2			= r2;
		this.subPattern	= subPattern;
	}
}
