package development.com.collection.range;

import internal.parser.TokenCmpOp;
import internal.parser.containers.Datum;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import development.com.collection.range.Range.BoundType;

public class RangeSet extends TreeSet<Range> {
	private static final long serialVersionUID = 1L;


	public RangeSet() {
		super(new setComparator());
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
	
	public Range get(int index) {
		if(index >= this.size())
			throw new IndexOutOfBoundsException("RangeSet: Index: " + index + " Size: " + this.size());
		Iterator<Range> iter = this.iterator();
		for(int i = 0; i < index; i++)
			iter.next();
		return iter.next();
	}
	
	public boolean contains(Datum value) {
		Iterator<Range> iter = this.iterator();
		while(iter.hasNext()) {
			Range range = iter.next();
			if(range.contains(value))
				return true;
		}
		return false;
	}
	
	public Datum generateRandom() {
		int index	= (new Random()).nextInt(this.size());
		
		Range randomRange = null;
		Iterator<Range> iter = this.iterator();
		for(int i = 0; i < index; i++)
			iter.next();
		randomRange = iter.next();
		return randomRange.generateRandom();
	}
	
	public RangeSet clone() {
		RangeSet result = new RangeSet();
		for(Range range : this) {
			result.add(range.clone());
		}
		return result;
	}
	
	public RangeSet sum(RangeSet set) {
		RangeSet result = new RangeSet();
		for(Range range1 : this) {
			for(Range range2 : set) {
				result.add(range1.add(range2));
			}
		}
		return result;
	}
	
	private static class setComparator implements Comparator<Range> {

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
