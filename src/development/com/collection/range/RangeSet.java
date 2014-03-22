package development.com.collection.range;

import internal.parser.TokenCmpOp;
import internal.parser.containers.Datum;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import development.com.collection.range.Range.BoundType;

/**
 * The {@code RangeSet} class extends {@code TreeSet<Range>} and is provided as a container 
 * for storing several related {@code Range} objects.
 * @author guru
 *
 */
public class RangeSet extends TreeSet<Range> {
	private static final long serialVersionUID = 1L;
	private static SetComparator comparator = new SetComparator();


	public RangeSet() {
		super(comparator);
	}
	
	@Override
	public boolean add(Range newRange) {
		boolean merged 	= false;
		boolean result	= false;
		
		Range upperRange	= newRange.clone();
		upperRange.setLowerBoundType(BoundType.CLOSED);
		upperRange.setUpperBoundType(BoundType.CLOSED);
		upperRange.setLowerBound(upperRange.upperBound());
		
		Set<Range> headSet		= this.headSet(upperRange, true).descendingSet();
		Iterator<Range> setIter	= headSet.iterator();
		
		Range resultRange = null;
		while(setIter.hasNext()) {
			Range range = setIter.next();
			if(range.contains(newRange.lowerBound()) && range.contains(newRange.upperBound())) {
				merged = true;
				break;
			}
			else {
				Range intersection = range.intersection(newRange);
				if(intersection != null) {
					setIter.remove();
					if(resultRange == null)
						resultRange = range.span(newRange);
					else
						resultRange = range.span(resultRange);
					merged = true;
				}
				else
					break;
			}
		}
		if(!merged)
			result = super.add(newRange);
		else {
			if(resultRange != null)
				this.add(resultRange);
		}
		return result;
	}
	
	/**
	 * Get a {@code Range} by its index
	 * @param index {@code int} the index of the required {@code Range}
	 * @return {@code Range} corresponding to the index specified
	 */
	public Range get(int index) {
		if(index >= this.size())
			throw new IndexOutOfBoundsException("RangeSet: Index: " + index + " Size: " + this.size());
		Iterator<Range> iter = this.iterator();
		for(int i = 0; i < index; i++)
			iter.next();
		return iter.next();
	}
	
	/**
	 * Check to see if this {@code RangeSet} contains the specified <tt>value</tt>
	 * @param value {@code Datum} the value to check for
	 * @return <b>true</b> if it contains the <tt>value</tt> <br>
	 * <b>false</b> otherwise
	 */
	public boolean contains(Datum value) {
		Iterator<Range> iter = this.iterator();
		while(iter.hasNext()) {
			Range range = iter.next();
			if(range.contains(value))
				return true;
		}
		return false;
	}
	
	/**
	 * Generate a random value from one of the underlying {@code Range}s
	 * @return {@code Datum} containing the randomly generated value
	 */
	public Datum generateRandom() {
		int index	= (new Random()).nextInt(this.size());
		
		Range randomRange = null;
		Iterator<Range> iter = this.iterator();
		for(int i = 0; i < index; i++)
			iter.next();
		randomRange = iter.next();
		return randomRange.generateRandom();
	}
	
	/**
	 * Create a deep-copy of this {@code RangeSet}
	 */
	public RangeSet clone() {
		RangeSet result = new RangeSet();
		for(Range range : this) {
			result.add(range.clone());
		}
		return result;
	}

	/**
	 * Nested-loop addition of two {@code RangeSet}s
	 * @param set {@code RangeSet} the set to add
	 * @return new {@code RangeSet} containing the result of the addition 
	 */
	public RangeSet sum(RangeSet set) {
		RangeSet result = new RangeSet();
		if(this.size() == 0)
			result.addAll(set);
		else {
			for(Range range1 : this) {
				for(Range range2 : set) {
					result.add(range1.add(range2));
				}
			}
		}
		return result;
	}

	/**
	 * The comparator to store {@code Range}s in the right order in the underlying {@code TreeSet<Range>} <br>
	 * This is currently set to be in ascending order
	 * @author guru
	 *
	 */
	private static class SetComparator implements Comparator<Range> {

		@Override
		public int compare(Range o1, Range o2) {
			if(o1.lowerBound().compareTo(o2.lowerBound(), TokenCmpOp.EQ) == 0) {
				if(!o1.lowerBoundType().equals(o2.lowerBoundType())) {
					switch(o1.lowerBoundType()) {
					case CLOSED:
						return -1;
					case OPEN:
						return 1;
					}
				}
				else {
					if(o1.upperBound().compareTo(o2.upperBound(), TokenCmpOp.EQ) == 0) {
						if(!o1.upperBoundType().equals(o2.upperBoundType())) {
							switch(o1.upperBoundType()) {
							case CLOSED:
								return 1;
							case OPEN:
								return -1;
							}
						}
						else
							return 0;
					}
					else
						return (Integer) o1.upperBound().subtract(o2.upperBound()).toInt().data();
				}
			}
			return (Integer) o1.lowerBound().subtract(o2.lowerBound()).toInt().data();
		}
	}
}
