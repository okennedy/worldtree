package development.com.collection.range;

import internal.parser.TokenCmpOp;
import internal.parser.containers.Datum;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import development.com.collection.range.Range.BoundType;

public class RangeSet extends TreeSet<Range> {
	private TreeMap<String, Range> rangeMap;
	private TreeMap<String, Integer> rangeCountMap;
	private static final long serialVersionUID = 1L;


	public RangeSet() {
		super(new setComparator());
		rangeMap		= new TreeMap<String, Range>();
		rangeCountMap 	= new TreeMap<String, Integer>();
	}
	
	@Override
	public boolean add(Range newRange) {
		boolean merged 	= false;
		boolean result	= false;
		
		Range upperRange	= newRange.clone();
		upperRange.setLowerBoundType(BoundType.CLOSED);
		upperRange.setUpperBoundType(BoundType.CLOSED);
		upperRange.setLowerBound(upperRange.upperBound());
		
		Set<Range> headSet = this.headSet(upperRange, true);
		
		for(Range range : headSet) {
			if(range.contains(newRange.lowerBound()) && range.contains(newRange.upperBound())) {
				merged = true;
				break;
			}
			else {
				Range intersection = range.intersection(newRange);
				if(intersection != null) {
//					if(intersection.contains(range.lowerBound())) {
////						newRange finishes outside
//						range.setUpperBound(newRange.upperBound());
//						range.setUpperBoundType(newRange.upperBoundType());
//					}
//					else if(intersection.contains(range.upperBound())) {
////						newRange starts outside
//						range.setLowerBound(newRange.lowerBound());
//						range.setLowerBoundType(newRange.lowerBoundType());
//					}
					this.remove(range);
					range = range.span(newRange);
					this.add(range);
					merged = true;
				}
			}
		}
		if(!merged)
			result = super.add(newRange);
		
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
		int sumOfWeights = 0;
		for(Map.Entry<String, Integer> entry : rangeCountMap.entrySet())
			sumOfWeights += entry.getValue();
		
		int random	= (new Random()).nextInt(sumOfWeights);
		int index	= random;
		
		Range randomRange = null;
		for(Map.Entry<String, Integer> entry : rangeCountMap.entrySet()) {
			if(index == 0 || (index - entry.getValue()) <= 0) {
				randomRange = rangeMap.get(entry.getKey());
				break;
			}
			else
				index -= entry.getValue();
		}
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
