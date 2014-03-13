package development.com.collection.range;

import internal.parser.containers.Datum;
import internal.parser.containers.Datum.DatumType;

public class IntegerRange extends Range {
	
	protected IntegerRange() {
		super();
	}
	
	/* ---------------------------------- CONSTRUCTORS ---------------------------------- */
	protected IntegerRange(int lowerBound, BoundType lowerBoundType, int upperBound, BoundType upperBoundType) {
		super(new Datum.Int(lowerBound), lowerBoundType, new Datum.Int(upperBound), upperBoundType);
	}
	
	protected IntegerRange(Datum lowerBound, BoundType lowerBoundType, Datum upperBound, BoundType upperBoundType) {
		super(lowerBound, lowerBoundType, upperBound, upperBoundType);
	}
	
	public static IntegerRange open(int lowerBound, int upperBound) {
		return new IntegerRange(lowerBound, BoundType.OPEN, upperBound, BoundType.OPEN);
	}
	
	public static IntegerRange open(Datum lowerBound, Datum upperBound) {
		assert (lowerBound.type() == DatumType.INT && upperBound.type() == DatumType.INT) : "IntegerRange: Datum type is not INT";
		return new IntegerRange(lowerBound, BoundType.OPEN, upperBound, BoundType.OPEN);
	}
	
	
	public static IntegerRange closed(int lowerBound, int upperBound) {
		return new IntegerRange(lowerBound, BoundType.CLOSED, upperBound, BoundType.CLOSED);
	}
	
	public static IntegerRange closed(Datum lowerBound, Datum upperBound) {
		assert (lowerBound.type() == DatumType.INT && upperBound.type() == DatumType.INT) : "IntegerRange: Datum type is not INT";
		return new IntegerRange(lowerBound, BoundType.CLOSED, upperBound, BoundType.CLOSED);
	}
	
	
	public static IntegerRange openClosed(int lowerBound, int upperBound) {
		return new IntegerRange(lowerBound, BoundType.OPEN, upperBound, BoundType.CLOSED);
	}
	
	public static IntegerRange openClosed(Datum lowerBound, Datum upperBound) {
		assert (lowerBound.type() == DatumType.INT && upperBound.type() == DatumType.INT) : "IntegerRange: Datum type is not INT";
		return new IntegerRange(lowerBound, BoundType.OPEN, upperBound, BoundType.CLOSED);
	}
	
	
	public static IntegerRange closedOpen(int lowerBound, int upperBound) {
		return new IntegerRange(lowerBound, BoundType.CLOSED, upperBound, BoundType.OPEN);
	}
	
	public static IntegerRange closedOpen(Datum lowerBound, Datum upperBound) {
		assert (lowerBound.type() == DatumType.INT && upperBound.type() == DatumType.INT) : "IntegerRange: Datum type is not INT";
		return new IntegerRange(lowerBound, BoundType.CLOSED, upperBound, BoundType.OPEN);
	}
	/* ---------------------------------- CONSTRUCTORS ---------------------------------- */
	
	
	public Range intersection(Range range) {
//		R1 contains R2
		if(range.contains(this.lowerBound()) && range.contains(this.upperBound()))
			return this.clone();
//		R2 contains R1
		else if(this.contains(range.lowerBound()) && this.contains(range.upperBound()))
			return range.clone();
		
//		Either no overlap or partial overlap
		else {
			Datum lowerBound 			= null;
			BoundType lowerBoundType	= null;
			Datum upperBound			= null;
			BoundType upperBoundType	= null;
			
			if(this.contains(range.lowerBound())) {
				lowerBound 		= range.lowerBound();
				lowerBoundType	= range.lowerBoundType();
				upperBound		= this.upperBound();
				upperBoundType	= this.upperBoundType();
				return new IntegerRange(lowerBound, lowerBoundType, upperBound, upperBoundType);
			}
			else if(this.contains(range.upperBound())) {
				lowerBound		= this.lowerBound();
				lowerBoundType	= this.lowerBoundType();
				upperBound		= range.upperBound();
				upperBoundType	= range.upperBoundType();
				return new IntegerRange(lowerBound, lowerBoundType, upperBound, upperBoundType);
			}
		}
		return null;
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
		Integer valueInt 	= null;
		int value;
		int lowerBoundData	= (Integer) lowerBound().data();
		int upperBoundData	= (Integer) upperBound().data();
		
		try {
			valueInt		= (Integer) datum.data();
		} catch(Exception e) {
			throw new IllegalArgumentException("IntegerRange cannot contain an object of type '" + datum.type() + "'");
		}
		value = valueInt.intValue();
		if(value > lowerBoundData && value < upperBoundData)
			return true;
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
		return new IntegerRange(lowerBound(), lowerBoundType(), upperBound(), upperBoundType());
	}
}