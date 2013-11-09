package development.com.collection.range;

import internal.parser.containers.Datum;
import internal.parser.containers.Datum.DatumType;
import internal.parser.containers.Datum.Int;

public class IntegerRange extends Range {
	
	protected IntegerRange() {
		super();
	}
	
	protected IntegerRange(int lowerBound, BoundType lowerBoundType, int upperBound, BoundType upperBoundType) {
		super(new Datum.Int(lowerBound), lowerBoundType, new Datum.Int(upperBound), upperBoundType);
	}
	
	public static IntegerRange open(int lowerBound, int upperBound) {
		return new IntegerRange(lowerBound, BoundType.OPEN, upperBound, BoundType.OPEN);
	}
	
	public static IntegerRange open(Datum lowerBound, Datum upperBound) {
		assert (lowerBound.type() == DatumType.INT && upperBound.type() == DatumType.INT) : "IntegerRange: Datum type is not INT";
		return new IntegerRange((Integer) lowerBound.data(), BoundType.OPEN, (Integer) upperBound.data(), BoundType.OPEN);
	}
	
	
	public static IntegerRange closed(int lowerBound, int upperBound) {
		return new IntegerRange(lowerBound, BoundType.CLOSED, upperBound, BoundType.CLOSED);
	}
	
	public static IntegerRange closed(Datum lowerBound, Datum upperBound) {
		assert (lowerBound.type() == DatumType.INT && upperBound.type() == DatumType.INT) : "IntegerRange: Datum type is not INT";
		return new IntegerRange((Integer) lowerBound.data(), BoundType.CLOSED, (Integer) upperBound.data(), BoundType.CLOSED);
	}
	
	
	public static IntegerRange openClosed(int lowerBound, int upperBound) {
		return new IntegerRange(lowerBound, BoundType.OPEN, upperBound, BoundType.CLOSED);
	}
	
	public static IntegerRange openClosed(Datum lowerBound, Datum upperBound) {
		assert (lowerBound.type() == DatumType.INT && upperBound.type() == DatumType.INT) : "IntegerRange: Datum type is not INT";
		return new IntegerRange((Integer) lowerBound.data(), BoundType.OPEN, (Integer) upperBound.data(), BoundType.CLOSED);
	}
	
	
	public static IntegerRange closedOpen(int lowerBound, int upperBound) {
		return new IntegerRange(lowerBound, BoundType.CLOSED, upperBound, BoundType.OPEN);
	}
	
	public static IntegerRange closedOpen(Datum lowerBound, Datum upperBound) {
		assert (lowerBound.type() == DatumType.INT && upperBound.type() == DatumType.INT) : "IntegerRange: Datum type is not INT";
		return new IntegerRange((Integer) lowerBound.data(), BoundType.CLOSED, (Integer) upperBound.data(), BoundType.OPEN);
	}
	
	
	public IntegerRange intersection(Range range) {
		int lowerBoundData			= (Integer) this.lowerBound().data();
		int upperBoundData			= (Integer) this.upperBound().data();
		int rangeLowerBoundData		= (Integer) range.lowerBound().data();
		int rangeUpperBoundData		= (Integer) range.upperBound().data();
		
		int lowerBound 				= lowerBoundData > rangeLowerBoundData ? lowerBoundData : rangeLowerBoundData;
		int upperBound 				= upperBoundData < rangeUpperBoundData ? upperBoundData : rangeUpperBoundData;
		
		BoundType lowerBoundType	= BoundType.CLOSED;
		BoundType upperBoundType	= BoundType.CLOSED;
		
		if(this.lowerBoundType() == BoundType.OPEN || range.lowerBoundType() == BoundType.OPEN)
			lowerBoundType = BoundType.OPEN;
		if(this.upperBoundType() == BoundType.OPEN || range.upperBoundType() == BoundType.OPEN)
			upperBoundType = BoundType.OPEN;
		
		return new IntegerRange(lowerBound, lowerBoundType, upperBound, upperBoundType);
	}
	
	@Override
	public Range add(Range range) {
		int lowerBound				= (Integer) this.lowerBound().add(range.lowerBound()).data();
		int upperBound				= (Integer) this.upperBound().add(range.upperBound()).data();
		
		if(this.lowerBoundType() == BoundType.OPEN)
			lowerBound++;
		if(this.upperBoundType() == BoundType.OPEN)
			upperBound--;
		
		if(range.lowerBoundType() == BoundType.OPEN)
			lowerBound++;
		if(range.upperBoundType() == BoundType.OPEN)
			upperBound--;
		
		return new IntegerRange(lowerBound, BoundType.CLOSED, upperBound, BoundType.CLOSED);
	}
	
	@Override
	public boolean contains(Datum datum) {
		Integer value = null;
		
		Integer lowerBoundData	= (Integer) lowerBound().data();
		Integer upperBoundData	= (Integer) upperBound().data();
		
		try {
			value 			= (Integer) datum.data();
		} catch(Exception e) {
			throw new IllegalArgumentException("IntegerRange cannot contain an object of type '" + datum.type() + "'");
		}
		switch(lowerBoundType()) {
		case CLOSED:
			if(value == lowerBoundData)
				return true;
			break;
		case OPEN:
			if(value == lowerBoundData)
				return false;
			break;
		}
		switch(upperBoundType()) {
		case CLOSED:
			if(value == upperBoundData)
				return true;
			break;
		case OPEN:
			if(value == upperBoundData)
				return false;
			break;
		}
		
		if(value > lowerBoundData && value < upperBoundData)
			return true;
		else
			return false;
	}

	@Override
	public Datum generateRandom() {
		int lowerBound = 0, upperBound = 0;
		
		switch(lowerBoundType()) {
		case CLOSED:
			lowerBound 	= (Integer) lowerBound().data();
			break;
		case OPEN:
			lowerBound 	= (Integer) lowerBound().data() + 1;
			break;
		}
		switch(upperBoundType()) {
		case CLOSED:
			upperBound	= (Integer) upperBound().data() + 1; 
			break;
		case OPEN:
			upperBound	= (Integer) upperBound().data();
			break;
		}
		
		int randomValue = (int) (lowerBound + ((int) (Math.random() * (upperBound - lowerBound))));
		return new Datum.Int(randomValue);
	}

	@Override
	public Range span(Range range) {
		int lowerBoundData			= (Integer) this.lowerBound().data();
		int upperBoundData			= (Integer) this.upperBound().data();
		int rangeLowerBoundData		= (Integer) range.lowerBound().data();
		int rangeUpperBoundData		= (Integer) range.upperBound().data();

		int lowerBound				= 0;
		int upperBound				= 0;
		
		BoundType lowerBoundType	= null;
		BoundType upperBoundType	= null;
		
		if(lowerBoundData < rangeLowerBoundData) {
			lowerBound				= lowerBoundData;
			lowerBoundType			= lowerBoundType();
		}
		else if(rangeLowerBoundData < lowerBoundData) {
			lowerBound				= rangeLowerBoundData;
			lowerBoundType			= range.lowerBoundType();
		}
		else {
			lowerBound				= lowerBoundData;
			lowerBoundType			= BoundType.OPEN;
			if(this.lowerBoundType() == BoundType.CLOSED || range.lowerBoundType() == BoundType.CLOSED)
				lowerBoundType = BoundType.CLOSED;
		}
		
		
		if(upperBoundData > rangeUpperBoundData) {
			upperBound				= upperBoundData;
			upperBoundType			= upperBoundType();
		}
		else if(rangeUpperBoundData > upperBoundData) {
			upperBound				= rangeUpperBoundData;
			upperBoundType			= range.upperBoundType();
		}
		else {
			upperBound				= upperBoundData;
			upperBoundType			= BoundType.OPEN;
			if(this.upperBoundType() == BoundType.CLOSED || range.upperBoundType() == BoundType.CLOSED)
				upperBoundType = BoundType.CLOSED;
		}
		
		return new IntegerRange(lowerBound, lowerBoundType, upperBound, upperBoundType);
	}

	@Override
	public Range clone() {
		return new IntegerRange((Integer) lowerBound().data(), lowerBoundType(), (Integer) upperBound().data(), upperBoundType());
	}
}