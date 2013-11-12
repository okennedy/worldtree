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

	public Datum generateRandom() {
		int size = this.size();
		int random = (new Random()).nextInt(size);
		
		Iterator<Range> iter = this.iterator();
		for(int i = 0; i < random; i++)
			iter.next();
		Range range = iter.next();
		return range.generateRandom();
	}
	
	public boolean contains(Datum value) {
		for(Range r : this) {
			if(r.contains(value))
				return true;
		}
		return false;
	}
	
	public Range get(int index) {
		Iterator<Range> iter = this.iterator();
		for(int i = 0; i < index; i++)
			iter.next();
		return iter.next();
	}
	
	private static class setComparator implements Comparator<Range> {

		@Override
		public int compare(Range o1, Range o2) {
			if (o1.lowerBound().compareTo(o2.lowerBound(), TokenCmpOp.LT) == 0) {
				return o1.upperBound().compareTo(o2.upperBound(), TokenCmpOp.LT);
			}
			else
				return o1.lowerBound().compareTo(o2.lowerBound(), TokenCmpOp.LT);
		}
	}
}
