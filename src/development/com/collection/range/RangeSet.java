package development.com.collection.range;

import internal.parser.TokenCmpOp;

import java.util.Comparator;
import java.util.TreeSet;

public class RangeSet extends TreeSet<Range> {
	private static final long serialVersionUID = 1L;


	public RangeSet() {
		super(new setComparator());
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
