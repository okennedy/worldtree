package development.com.collection.range;

import internal.parser.containers.Datum;

/**
 * Range class is used to store a range
 * The various types of ranges are as follows : <br>
 * (a..b) = {x | a <  x <  b } <br>
 * [a..b] = {x | a <= x <= b } <br>
 * [a..b) = {x | a <= x <  b } <br>
 * (a..b] = {x | a <  x <= b } <br>
 * <br>
 * An object of type Range is obtained by calling one of the static methods provided.
 * @author guru
 *
 */
public abstract class Range {
	private Datum lowerBound, upperBound;
	private BoundType lowerBoundType, upperBoundType;
	
	protected Range() {}
	
	protected Range(Datum lowerBound, BoundType lowerBoundType, Datum upperBound, BoundType upperBoundType) {
		this.lowerBoundType	= lowerBoundType;
		this.upperBoundType	= upperBoundType;
		this.lowerBound		= lowerBound;
		this.upperBound		= upperBound;
	}
	
	public Datum lowerBound() {
		return lowerBound;
	}
	
	public Datum upperBound() {
		return upperBound;
	}
	
	public BoundType lowerBoundType() {
		return lowerBoundType;
	}
	
	public BoundType upperBoundType() {
		return upperBoundType;
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		switch(lowerBoundType) {
		case CLOSED:
			result.append("[");
			break;
		case OPEN:
			result.append("(");
			break;
		}
		
		result.append(lowerBound + " , " + upperBound);
		
		switch(upperBoundType) {
		case CLOSED:
			result.append("]");
			break;
		case OPEN:
			result.append(")");
			break;
		}
		
		return result.toString();
	}
	
	
	public abstract Range add(Range range);
	public abstract boolean contains(Datum datum);
	public abstract Range intersection(Range range);
	public abstract Datum generateRandom();
	public abstract Range span(Range range);
	public abstract Range clone();
	
	public void setUpperBound(Datum upperBound) {
		this.upperBound	= upperBound;
	}
	
	public void setLowerBound(Datum lowerBound) {
		this.lowerBound	= lowerBound;
	}
	
	public void setUpperBoundType(BoundType upperBoundType) {
		this.upperBoundType	= upperBoundType;
	}
	
	public void setLowerBoundType(BoundType lowerBoundType) {
		this.lowerBoundType	= lowerBoundType;
	}
	
	public enum BoundType {
		OPEN,
		CLOSED,
		;
	}
}