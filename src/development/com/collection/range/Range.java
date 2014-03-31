package development.com.collection.range;

import internal.parser.TokenCmpOp;
import internal.parser.containers.Datum;
import internal.parser.containers.Datum.DatumType;

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
		switch(lowerBoundType) {
		case CLOSED:
			switch(upperBoundType) {
			case CLOSED:
				assert(lowerBound.compareTo(upperBound, TokenCmpOp.LE) == 0) : "Trying to create invalid range! " + "[" + lowerBound + " - " + upperBound + "]";
				break;
			case OPEN:
				assert(lowerBound.compareTo(upperBound, TokenCmpOp.LT) == 0) : "Trying to create invalid range! " + "[" + lowerBound + " - " + upperBound + ")";
				break;
			}
			break;
		case OPEN:
			assert(lowerBound.compareTo(upperBound, TokenCmpOp.LT) == 0) : "Trying to create invalid range! " + "(" + lowerBound + " - " + upperBound;
			break;
		}
		
		this.lowerBoundType	= lowerBoundType;
		this.upperBoundType	= upperBoundType;
		this.lowerBound		= lowerBound;
		this.upperBound		= upperBound;
	}
	
	public static Range createRange(Datum lowerBound, BoundType lowerBoundType, Datum upperBound, BoundType upperBoundType) {
		DatumType rangeType = null;
		if(!lowerBound.type().equals(upperBound.type()))
			rangeType = DatumType.FLOAT;
		else
			rangeType = lowerBound.type();
		
		Range returnRange = null;
		switch(rangeType) {
		case FLOAT:
			returnRange = new FloatRange(lowerBound, lowerBoundType, upperBound, upperBoundType);
			break;
		case INT:
			returnRange = new IntegerRange(lowerBound, lowerBoundType, upperBound, upperBoundType);
			break;
		case BOOL:
		case STRING:
		default:
			throw new IllegalStateException("Unimplemented datum type " + rangeType);
		}
		return returnRange;
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
	
	/**
	 * Adds a {@code Range} to this range.
	 * @param range {@code Range} specifying the range to add
	 * @return new {@code Range} denoting the result of the addition
	 */
	public abstract Range add(Range range);
	
	/**
	 * Checks to see whether this range contains the {@code Datum}
	 * @param datum {@code Datum} object containing the value to check
	 * @return <b>true</b> if it does contain {@code datum}<br>
	 * <b>false</b> otherwise
	 */
	public abstract boolean contains(Datum datum);
	
	/**
	 * Return the intersection with another {@code Range}
	 * @param range {@code Range} object to intersect with
	 * @return new {@code Range} object specifying the intersection
	 */
	public abstract Range intersection(Range range);
	
	/**
	 * Generate a random value from this range
	 * @return {@code Datum} containing the random value
	 */
	public abstract Datum generateRandom();
	
	/**
	 * Find span of two ranges
	 * @param range {@code Range} specifying the second range 
	 * @return new {@code Range} expressing the span
	 */
	public abstract Range span(Range range);
	
	/**
	 * Create a new {@code Range} object that is a deep-copy clone of the current {@code Range}
	 */
	public abstract Range clone();
	
	public void setUpperBound(Datum upperBound) {
		this.upperBound	= upperBound;
		if (this.upperBoundType.equals(BoundType.OPEN)) {
				assert upperBound.compareTo(this.lowerBound, TokenCmpOp.GT) == 0 : "Trying to create invalid range :" + toString();
		}
		else {
			assert upperBound.compareTo(this.lowerBound, TokenCmpOp.GE) == 0 : "Trying to create invalid range :" + toString();
		}
	}
	
	public void setLowerBound(Datum lowerBound) {
		this.lowerBound	= lowerBound;
		if (this.lowerBoundType.equals(BoundType.OPEN)) {
			assert lowerBound.compareTo(this.upperBound, TokenCmpOp.LT) == 0 : "Trying to create invalid range :" + toString();
		}
		else {
			assert lowerBound.compareTo(this.upperBound, TokenCmpOp.LE) == 0 : "Trying to create invalid range :" + toString();
		}
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