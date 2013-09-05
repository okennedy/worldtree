package development.com.collection.range;

import development.com.collection.range.Range.BoundType;

import internal.parser.containers.Datum;

public class FloatRange extends Range {
	
	protected FloatRange() {
		super();
	}
	
	protected FloatRange(float lowerBound, BoundType lowerBoundType, float upperBound, BoundType upperBoundType) {
		super(new Datum.Flt(lowerBound), lowerBoundType, new Datum.Flt(upperBound), upperBoundType);
	}
	
	public static FloatRange open(float lowerBound, float upperBound) {
		return new FloatRange(lowerBound, BoundType.OPEN, upperBound, BoundType.OPEN);
	}
	
	public static FloatRange closed(float lowerBound, float upperBound) {
		return new FloatRange(lowerBound, BoundType.CLOSED, upperBound, BoundType.CLOSED);
	}
	
	public static FloatRange openClosed(float lowerBound, float upperBound) {
		return new FloatRange(lowerBound, BoundType.OPEN, upperBound, BoundType.CLOSED);
	}
	
	public static FloatRange closedOpen(float lowerBound, float upperBound) {
		return new FloatRange(lowerBound, BoundType.CLOSED, upperBound, BoundType.OPEN);
	}
	
	public FloatRange intersection(Range range) {
		float lowerBoundData			= (Float) this.lowerBound().data();
		float upperBoundData			= (Float) this.upperBound().data();
		float rangeLowerBoundData		= (Float) range.lowerBound().data();
		float rangeUpperBoundData		= (Float) range.upperBound().data();
		
		float lowerBound 				= lowerBoundData > rangeLowerBoundData ? lowerBoundData : rangeLowerBoundData;
		float upperBound 				= upperBoundData < rangeUpperBoundData ? upperBoundData : rangeUpperBoundData;
		
		BoundType lowerBoundType	= BoundType.CLOSED;
		BoundType upperBoundType	= BoundType.CLOSED;
		
		if(this.lowerBoundType() == BoundType.OPEN || range.lowerBoundType() == BoundType.OPEN)
			lowerBoundType = BoundType.OPEN;
		if(this.upperBoundType() == BoundType.OPEN || range.upperBoundType() == BoundType.OPEN)
			upperBoundType = BoundType.OPEN;
		
		return new FloatRange(lowerBound, lowerBoundType, upperBound, upperBoundType);
	}
	
	@Override
	public Range add(Range range) {
		float lowerBound				= (Float) this.lowerBound().add(range.lowerBound()).data();
		float upperBound				= (Float) this.upperBound().add(range.upperBound()).data();
		
		BoundType lowerBoundType	= BoundType.CLOSED;
		BoundType upperBoundType	= BoundType.OPEN;
		
		return new FloatRange(lowerBound, lowerBoundType, upperBound, upperBoundType);
	}
	
	@Override
	public boolean contains(Datum datum) {
		Float value = null;
		
		Float lowerBoundData	= (Float) lowerBound().data();
		Float upperBoundData	= (Float) upperBound().data();
		
		try {
			value 			= (Float) datum.data();
		} catch(Exception e) {
			throw new IllegalArgumentException("FloatRange cannot contain an object of type '" + datum.type() + "'");
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
		float lowerBound = 0, upperBound = 0;
		
		switch(lowerBoundType()) {
		case CLOSED:
			lowerBound 	= (Float) lowerBound().data();
			break;
		case OPEN:
			float lb	= (Float) lowerBound().data();
			while(lowerBound != lb)
				lowerBound 	= (float) (lb + Math.random());
			break;
		}
		switch(upperBoundType()) {
		case CLOSED:
			upperBound	= (Float) upperBound().data();
			break;
		case OPEN:
			upperBound	= (Float) upperBound().data();
			float ub	= (Float) upperBound().data();
			while(upperBound != ub)
				upperBound 	= (float) (ub - Math.random());
			break;
		}
		
		float randomValue = (float) (lowerBound + ((float) Math.ceil((Math.random() * (upperBound - lowerBound)))));
		return new Datum.Flt(randomValue);
	}
	
	@Override
	public Range span(Range range) {
		float lowerBoundData			= (Float) this.lowerBound().data();
		float upperBoundData			= (Float) this.upperBound().data();
		float rangeLowerBoundData		= (Float) range.lowerBound().data();
		float rangeUpperBoundData		= (Float) range.upperBound().data();

		float lowerBound				= 0;
		float upperBound				= 0;
		
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
		
		return new FloatRange(lowerBound, lowerBoundType, upperBound, upperBoundType);
	}
	
	@Override
	public Range clone() {
		return new FloatRange((Float) lowerBound().data(), lowerBoundType(), (Float) upperBound().data(), upperBoundType());
	}
}