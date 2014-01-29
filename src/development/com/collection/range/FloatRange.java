package development.com.collection.range;

import internal.parser.containers.Datum;
import internal.parser.containers.Datum.DatumType;

public class FloatRange extends Range {
	
	protected FloatRange() {
		super();
	}
	
	/* ---------------------------------- CONSTRUCTORS ---------------------------------- */
	protected FloatRange(float lowerBound, BoundType lowerBoundType, float upperBound, BoundType upperBoundType) {
		super(new Datum.Flt(lowerBound), lowerBoundType, new Datum.Flt(upperBound), upperBoundType);
	}
	
	protected FloatRange(Datum lowerBound, BoundType lowerBoundType, Datum upperBound, BoundType upperBoundType) {
		super(lowerBound, lowerBoundType, upperBound, upperBoundType);
	}
	
	public static FloatRange open(float lowerBound, float upperBound) {
		return new FloatRange(lowerBound, BoundType.OPEN, upperBound, BoundType.OPEN);
	}
	
	public static FloatRange open(Datum lowerBound, Datum upperBound) {
		assert (lowerBound.type() == DatumType.FLOAT && upperBound.type() == DatumType.FLOAT) : "FloatRange: Datum type is not FLOAT";
		return new FloatRange(lowerBound, BoundType.OPEN, upperBound, BoundType.OPEN);
	}
	
	
	public static FloatRange closed(float lowerBound, float upperBound) {
		return new FloatRange(lowerBound, BoundType.CLOSED, upperBound, BoundType.CLOSED);
	}
	
	public static FloatRange closed(Datum lowerBound, Datum upperBound) {
		assert (lowerBound.type() == DatumType.FLOAT && upperBound.type() == DatumType.FLOAT) : "FloatRange: Datum type is not FLOAT";
		return new FloatRange(lowerBound, BoundType.CLOSED, upperBound, BoundType.CLOSED);
	}
	
	
	public static FloatRange openClosed(float lowerBound, float upperBound) {
		return new FloatRange(lowerBound, BoundType.OPEN, upperBound, BoundType.CLOSED);
	}
	
	public static FloatRange openClosed(Datum lowerBound, Datum upperBound) {
		assert (lowerBound.type() == DatumType.FLOAT && upperBound.type() == DatumType.FLOAT) : "FloatRange: Datum type is not FLOAT";
		return new FloatRange(lowerBound, BoundType.OPEN, upperBound, BoundType.CLOSED);
	}
	
	
	public static FloatRange closedOpen(float lowerBound, float upperBound) {
		return new FloatRange(lowerBound, BoundType.CLOSED, upperBound, BoundType.OPEN);
	}
	
	public static FloatRange closedOpen(Datum lowerBound, Datum upperBound) {
		assert (lowerBound.type() == DatumType.FLOAT && upperBound.type() == DatumType.FLOAT) : "FloatRange: Datum type is not FLOAT";
		return new FloatRange(lowerBound, BoundType.CLOSED, upperBound, BoundType.OPEN);
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
				return new FloatRange(lowerBound, lowerBoundType, upperBound, upperBoundType);
			}
			else if(this.contains(range.upperBound())) {
				lowerBound		= this.lowerBound();
				lowerBoundType	= this.lowerBoundType();
				upperBound		= range.upperBound();
				upperBoundType	= range.upperBoundType();
				return new FloatRange(lowerBound, lowerBoundType, upperBound, upperBoundType);
			}
		}
		return null;
	}
	
	@Override
	public Range add(Range range) {
		float lowerBound				= (Float) this.lowerBound().add(range.lowerBound()).data();
		float upperBound				= (Float) this.upperBound().add(range.upperBound()).data();
		
		if(this.lowerBoundType() == BoundType.OPEN)
			lowerBound += Float.MIN_VALUE;
		if(this.upperBoundType() == BoundType.OPEN)
			upperBound -= Float.MIN_VALUE;
		
		if(range.lowerBoundType() == BoundType.OPEN)
			lowerBound += Float.MIN_VALUE;
		if(range.upperBoundType() == BoundType.OPEN)
			upperBound -= Float.MIN_VALUE;
		
		return new FloatRange(lowerBound, BoundType.CLOSED, upperBound, BoundType.CLOSED);
	}
	
	@Override
	public boolean contains(Datum datum) {
		Float valueFlt = null;
		float value;
		float lowerBoundData	= (Float) lowerBound().data();
		float upperBoundData	= (Float) upperBound().data();
		
		try {
			valueFlt			= (Float) datum.data();
		} catch(Exception e) {
			throw new IllegalArgumentException("FloatRange cannot contain an object of type '" + datum.type() + "'");
		}
		value = valueFlt.floatValue();
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
		return new FloatRange(lowerBound(), lowerBoundType(), upperBound(), upperBoundType());
	}
}