package internal.parser;

public class Pattern {
	Reference r1, r2;
	Relation relation;
	Pattern subPattern;
	
	Pattern(Reference r1, Relation relation, Reference r2, Pattern subPattern) {
		this.r1			= r1;
		this.relation	= relation;
		this.r2			= r2;
		this.subPattern	= subPattern;
	}
}
