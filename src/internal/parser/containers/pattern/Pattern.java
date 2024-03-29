package internal.parser.containers.pattern;

import java.util.Collection;

import internal.parser.containers.Reference;
import internal.parser.containers.Relation;

/**
 * Container class for storing a pattern <br>
 * PATTERN := REFERENCE (RELATION REFERENCE (, REFERENCE RELATION REFERENCE)* )?
 * @author guru
 *
 */
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
	public Reference lhs() {
		return basePattern.lhs();
	}

	@Override
	public Reference rhs() {
		return basePattern.rhs();
	}

	@Override
	public Relation relation() {
		return basePattern.relation();
	}

	@Override
	public IPattern subPattern() {
		return subPattern;
	}

	@Override
	public Collection<Reference> references() {
		return basePattern.references();
	}

	
	@Override
	public void setLhs(Reference reference) {
		basePattern.setLhs(reference);
	}

	@Override
	public void setRhs(Reference reference) {
		basePattern.setRhs(reference);
	}

	@Override
	public void setRelation(Relation relation) {
		basePattern.setRelation(relation);
	}

	@Override
	public void setSubPattern(IPattern subPattern) {
		basePattern.setSubPattern(subPattern);
	}
	
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(basePattern.toString());
		
		if(subPattern != null)
			result.append(" , " + subPattern.toString());
		
		return result.toString();
	}
	
	@Override
	public String debugString() {
		StringBuffer result = new StringBuffer("PATTERN(");
		
		result.append(basePattern.debugString());
		
		if(subPattern != null)
			result.append(" , " + subPattern.debugString());
		
		result.append(")");
		return result.toString();
	}
}
