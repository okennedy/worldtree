package internal.parser.containers.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import internal.parser.containers.Reference;
import internal.parser.containers.Relation;

/**
 * Container class for storing a pattern <br>
 * PATTERN := REFERENCE (RELATION REFERENCE (, REFERENCE RELATION REFERENCE)* )?
 * @author guru
 *
 */
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
		StringBuffer result = new StringBuffer(r1.toString() + " ");
		if(relation != null) {
			result.append(relation.toString() + " " + r2.toString());
		}
		
		return result.toString();
	}
	
	@Override
	public String debugString() {
		StringBuffer result = new StringBuffer("PATTERN(" + r1.debugString() + ")"); 
		
		if(relation != null) {
			result.append(" " + relation.debugString() + " " + r2.debugString() + ")");
		}
		return result.toString();
	}

	@Override
	public Reference lhs() {
		return r1;
	}

	@Override
	public Reference rhs() {
		return r2;
	}

	@Override
	public Relation relation() {
		return relation;
	}

	@Override
	public IPattern subPattern() {
		return null;
	}

	@Override
	public Collection<Reference> references() {
		return new ArrayList<Reference>(Arrays.asList(new Reference[]{r1, r2}));
	}

}
