package development.com.collection.range;

import internal.parser.TokenCmpOp;
import internal.parser.containers.Datum;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

public class RangeSet extends TreeSet<Range> {
	private static final long serialVersionUID = 1L;


	public RangeSet() {
		super(new setComparator());
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
		int size 	= this.size();
		int index	= (new Random()).nextInt(size);
		
		Iterator<Range> iter = this.iterator();
		for(int i = 0; i < index; i++)
			iter.next();
		
		Range range = iter.next();
		return range.generateRandom();
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
			if (o1.lowerBound().compareTo(o2.lowerBound(), TokenCmpOp.LT) == 0) {
				return (Integer) o1.upperBound().subtract(o2.upperBound()).toInt().data();
			}
			else
				return (Integer) o1.lowerBound().subtract(o2.lowerBound()).toInt().data();
		}
	}
}
