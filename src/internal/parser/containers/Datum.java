package internal.parser.containers;

import internal.parser.TokenCmpOp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Abstract container class to store various data types
 * @author guru
 *
 */
public abstract class Datum {
	Object data;
	DatumType type;
	
	protected Datum() {
//		Stops empty constructor initialization
	}
	
	private Datum(Object data) {
		this.data 	= data;
	}
	
	public Object data() {
		return data;
	}
	
	public DatumType type() {
		return type;
	}
	
	/**
	 * Split the provided datum into a {@code Collection<Datum>}
	 * @param size {@code int} containing the size of the collection
	 * @return {@code Collection<Datum>}
	 */
	public abstract Collection<Datum> split(int size);
	
	/**
	 * Add a {@code Datum} to the current {@code Datum} element
	 * @param datum {@code Datum} to add
	 * @return new {@code Datum} object containing the result
	 */
	public abstract Datum add(Datum datum);

	/**
	 * Subtract a {@code Datum} from the current {@code Datum} element
	 * @param datum {@code Datum} to subtract
	 * @return new {@code Datum} object containing the result
	 */
	public abstract Datum subtract(Datum datum);
	
	/**
	 * Multiply the current {@code Datum} element by the specified {@code Datum}
	 * @param datum {@code Datum} to multiply by
	 * @return new {@code Datum} object containing the result
	 */
	public abstract Datum multiply(Datum datum);
	
	/**
	 * Divide the current {@code Datum} element by the specified {@code Datum}
	 * @param datum {@code Datum} to divide by
	 * @return new {@code Datum} object containing the result
	 */
	public abstract Datum divide(Datum datum);
	
	/**
	 * Modulo the current {@code Datum} element by the specified {@code Datum}
	 * @param datum {@code Datum} to modulo by
	 * @return new {@code Datum} object containing the result
	 */
	public abstract Datum modulo(Datum datum);
	
	/**
	 * Clone the current {@code Datum} element
	 * @return new {@code Datum} object containing a deep-copy of the current {@code Datum}
	 */
	public abstract Datum clone();
	
	/**
	 * Convert the value of the current {@code Datum} to an {@link java.lang.Integer}
	 * @return {@code Datum} object containing the value as an {@code Integer}
	 * @throws IllegalStateException if the conversion is not possible
	 */
	public Datum toInt() {
		if(this.type == DatumType.INT)
			return this;
		Datum datum = null;
		int value = 0;
		switch(type) {
		case FLOAT:
			value 	= ((Float) this.data).intValue();
			break;
		case STRING:
			value	= Integer.parseInt("" + this.data);
			break;
		case BOOL:
		default:
			throw new IllegalStateException("Cannot convert from " + type + " to " + DatumType.INT);
		}

		datum = new Datum.Int(value);
		return datum;
	}
	
	/**
	 * Convert the value of the current {@code Datum} to an {@link java.lang.Float}
	 * @return {@code Datum} object containing the value as an {@code Float}
	 * @throws IllegalStateException if the conversion is not possible
	 */
	public Datum toFlt() {
		if(this.type == DatumType.FLOAT)
			return this;
		Datum datum = null;
		float value = 0;
		switch(type) {
		case INT:
			value	= ((Integer) this.data).floatValue();
			break;
		case STRING:
			value	= Float.parseFloat("" + this.data);
			break;
		case BOOL:
		default:
			throw new IllegalStateException("Cannot convert from " + type + " to " + DatumType.FLOAT);
		}
		
		datum = new Datum.Flt(value);
		return datum;
	}
	
	/**
	 * Convert the value of the current {@code Datum} to an {@link java.lang.String}
	 * @return {@code Datum} object containing the value as an {@code String}
	 * @throws IllegalStateException if the conversion is not possible
	 */
	public Datum toStr() {
		if(this.type == DatumType.STRING)
			return this;
		Datum datum = null;
		String value = null;
		switch(type) {
		case FLOAT:
		case INT:
		case BOOL:
			value	= "" + this.data;
			break;
		default:
			throw new IllegalStateException("Cannot convert from " + type + " to " + DatumType.FLOAT);
		}
		
		datum = new Datum.Str(value);
		return datum;
	}
	
	/**
	 * Compare current {@code Datum} object to another using the operator specified
	 * @param datum {@code Datum} object to compare against
	 * @param operator {@code TokenCmpOp} operator to compare with
	 * @return <b>0</b> if the two {@code Datum}s are equal in value<br>
	 * <b>-1</b> otherwise
	 * @throws IllegalArgumentException if the two datums are not comparable
	 */
//	FIXME: Perhaps we should return all possible values rather than just 0 or -1
	public int compareTo(Datum datum, TokenCmpOp operator) {
		switch(type) {
		case BOOL: {
			assert datum.type == DatumType.BOOL : "Cannot compare " + type + " and " + datum.type;
			
			boolean val1 = (Boolean) data;
			boolean val2 = (Boolean) datum.data;
			switch(operator) {
			case EQ:
				if(val1 == val2)
					return 0;
				break;
			case NOTEQ:
				if(val1 != val2)
					return 0;
				break;
			default:
				throw new IllegalArgumentException("Cannot compare " + type + " and " + datum.type + " using " + operator);
			}
		}
		case INT: {
			int val1 	= ((Integer) data).intValue();
			int val2 	= ((Integer) datum.toInt().data).intValue();	//FIXME: This should ideally be float..but float takes lot of time
			switch(operator) {
			case EQ:
				if(val1 == val2)
					return 0;
				break;
			case GE:
				if(val1 >= val2)
					return 0;
				break;
			case GT:
				if(val1 > val2)
					return 0;
				break;
			case LE:
				if(val1 <= val2)
					return 0;
				break;
			case LT:
				if(val1 < val2)
					return 0;
				break;
			case NOTEQ:
				if(val1 != val2)
					return 0;
				break;
			}
			return -1;
		}
			
		case FLOAT: {
			float val1 = ((Float) data).floatValue();
			float val2 = ((Float) datum.toFlt().data).floatValue();
			switch(operator) {
			case EQ:
				if(val1 == val2)
					return 0;
				break;
			case GE:
				if(val1 >= val2)
					return 0;
				break;
			case GT:
				if(val1 > val2)
					return 0;
				break;
			case LE:
				if(val1 <= val2)
					return 0;
				break;
			case LT:
				if(val1 < val2)
					return 0;
				break;
			case NOTEQ:
				if(val1 != val2)
					return 0;
				break;
			}
			return -1;
		}
		case STRING: {
			String val1 = (String) data;
			String val2 = (String) datum.data;
			switch(operator) {
			case EQ:
				if(val1.equals(val2))
					return 0;
				break;
			case GE:
			case GT:
			case LE:
			case LT:
				return val1.compareTo(val2);
			case NOTEQ:
				if(!val1.equals(val2))
					return 0;
				break;
			}
			break;
		}
		}
		return -1;
	}
	
	
	/**
	 * Inner {@code Datum} class to store {@link java.lang.Integer}
	 * @author guru
	 *
	 */
	public static class Int extends Datum {
		public Int(Integer data) {
			super(data);
			this.type = DatumType.INT;
		}
		
		@Override
		public String toString() {
			return Integer.toString((Integer) data);
		}

		@Override
		public List<Datum> split(int size) {
			List<Datum> result = new ArrayList<Datum>();
			
			int availableQty = (Integer) data;
			while(result.size() < size) {
				if(result.size() == size - 1) {
//					Last element..add the remaining
					result.add(new Datum.Int(availableQty));
				}
				else {
					int qty = 0 + ((int) (Math.random() * (availableQty + 1)));
					result.add(new Datum.Int(qty));
					availableQty -= qty;
				}
			}
			
//			TODO: Remove this
			int sum = 0;
			for(Datum d : result) {
				sum += (Integer) d.data;
			}
			assert sum == (Integer) data : "Datum.Int.allocate failed to allocate accurately!\n"
					+ "Total allocated :" + sum + "\n"
					+ "Available       :" + data + "\n";
			return result;
		}
		
		public Datum add(Datum datum) {
			assert (datum.type.equals(DatumType.INT) || datum.type.equals(DatumType.FLOAT)) : "Cannot add Datum types " + this.type + " , " + datum.type;
			
			int data = (Integer) this.data;
			switch(datum.type) {
			case FLOAT:
				return new Datum.Flt(data + (Float) datum.data);
			case INT:
				return new Datum.Int(data + (Integer) datum.data);
			case STRING:
			case BOOL:
			default:
				throw new IllegalStateException("Should not be here!");
			}
			
		}
		
		public Datum subtract(Datum datum) {
			assert (datum.type.equals(DatumType.INT) || datum.type.equals(DatumType.FLOAT)) : "Cannot subtract Datum types " + this.type + " , " + datum.type;
			
			int data 			= (Integer) this.data;
			switch(datum.type) {
			case FLOAT:
				return new Datum.Flt(data - (Float) datum.data);
			case INT:
				return new Datum.Int(data - (Integer) datum.data);
			case STRING:
			case BOOL:
			default:
				throw new IllegalStateException("Should not be here!");
			}
		}
		
		public Datum multiply(Datum datum) {
			assert (datum.type.equals(DatumType.INT) || datum.type.equals(DatumType.FLOAT)) : "Cannot multiply Datum types " + this.type + " , " + datum.type;
			
			int data 			= (Integer) this.data;
			switch(datum.type) {
			case FLOAT:
				return new Datum.Flt(data * (Float) datum.data);
			case INT:
				return new Datum.Int(data * (Integer) datum.data);
			case STRING:
			case BOOL:
			default:
				throw new IllegalStateException("Should not be here!");
			}
		}
		
		public Datum divide(Datum datum) {
			assert (datum.type.equals(DatumType.INT) || datum.type.equals(DatumType.FLOAT)) : "Cannot divide Datum types " + this.type + " , " + datum.type;
			
			int data 			= (Integer) this.data;
			switch(datum.type) {
			case FLOAT:
				return new Datum.Flt(data / (Float) datum.data);
			case INT:
				return new Datum.Int(data / (Integer) datum.data);
			case STRING:
			case BOOL:
			default:
				throw new IllegalStateException("Should not be here!");
			}
		}
		
		public Datum modulo(Datum datum) {
			assert (type.equals(DatumType.INT) || type.equals(DatumType.FLOAT)) : "Cannot add Datum types " + this.type + " , " + datum.type;
			
			int data 			= (Integer) this.data;
			switch(datum.type) {
			case FLOAT:
				return new Datum.Flt(data % (Float) datum.data);
			case INT:
				return new Datum.Int(data % (Integer) datum.data);
			case STRING:
			case BOOL:
			default:
				throw new IllegalStateException("Should not be here!");
			}
		}

		@Override
		public Datum clone() {
			return new Datum.Int((Integer)this.data);
		}
	}
	
	
	/**
	 * Inner {@code Datum} class to store {@link java.lang.Float}
	 * @author guru
	 *
	 */
	public static class Flt extends Datum {
		public Flt(Float data) {
			super(data);
			this.type = DatumType.FLOAT;
		}
		
		@Override
		public String toString() {
			return Float.toString((Float)data);
		}

		@Override
		public List<Datum> split(int size) {
			List<Datum> result = new ArrayList<Datum>();
			
			float availableQty = (Float) data;
			while(result.size() < size) {
				float qty = 0 + ((float) (Math.random() * (availableQty + 1)));
				result.add(new Datum.Flt(qty));
				availableQty -= qty;
			}
			
//			TODO: Remove this
			float sum = 0;
			for(Datum d : result) {
				sum += (Float) d.data;
			}
			assert sum == (Integer) data : "Datum.Int.allocate failed to allocate accurately!\n"
					+ "Total allocated :" + sum + "\n"
					+ "Available       :" + data + "\n";
			return result;
		}
		
		public Datum add(Datum datum) {
			assert (datum.type.equals(DatumType.INT) || datum.type.equals(DatumType.FLOAT)) : "Cannot add Datum types " + this.type + " , " + datum.type;
			
			float data 			= (Float) this.data;
			switch(datum.type) {
			case FLOAT:
				return new Datum.Flt(data + (Float) datum.data);
			case INT:
				return new Datum.Flt(data + (Integer) datum.data);
			case STRING:
			case BOOL:
			default:
				throw new IllegalStateException("Should not be here!");
			}
			
		}
		
		public Datum subtract(Datum datum) {
			assert (datum.type.equals(DatumType.INT) || datum.type.equals(DatumType.FLOAT)) : "Cannot subtract Datum types " + this.type + " , " + datum.type;
			
			float data 			= (Float) this.data;
			switch(datum.type) {
			case FLOAT:
				return new Datum.Flt(data - (Float) datum.data);
			case INT:
				return new Datum.Flt(data - (Integer) datum.data);
			case STRING:
			case BOOL:
			default:
				throw new IllegalStateException("Should not be here!");
			}
		}
		
		public Datum multiply(Datum datum) {
			assert (datum.type.equals(DatumType.INT) || datum.type.equals(DatumType.FLOAT)) : "Cannot multiply Datum types " + this.type + " , " + datum.type;
			
			float data 			= (Float) this.data;
			switch(datum.type) {
			case FLOAT:
				return new Datum.Flt(data * (Float) datum.data);
			case INT:
				return new Datum.Flt(data * (Integer) datum.data);
			case STRING:
			case BOOL:
			default:
				throw new IllegalStateException("Should not be here!");
			}
		}
		
		public Datum divide(Datum datum) {
			assert (datum.type.equals(DatumType.INT) || datum.type.equals(DatumType.FLOAT)) : "Cannot divide Datum types " + this.type + " , " + datum.type;
			
			float data 			= (Float) this.data;
			switch(datum.type) {
			case FLOAT:
				return new Datum.Flt(data / (Float) datum.data);
			case INT:
				return new Datum.Flt(data / (Integer) datum.data);
			case STRING:
			case BOOL:
			default:
				throw new IllegalStateException("Should not be here!");
			}
		}
		
		public Datum modulo(Datum datum) {
			assert (type.equals(DatumType.INT) || type.equals(DatumType.FLOAT)) : "Cannot add Datum types " + this.type + " , " + datum.type;
			
			float data 			= (Float) this.data;
			switch(datum.type) {
			case FLOAT:
				return new Datum.Flt(data % (Float) datum.data);
			case INT:
				return new Datum.Flt(data % (Integer) datum.data);
			case STRING:
			case BOOL:
			default:
				throw new IllegalStateException("Should not be here!");
			}
		}

		@Override
		public Datum clone() {
			return new Datum.Flt((Float)this.data);
		}
		
	}
	
	
	/**
	 * Inner {@code Datum} class to store {@link java.lang.String}
	 * @author guru
	 *
	 */
	public static class Str extends Datum {
		public Str(String data) {
			super(data);
			this.type = DatumType.STRING;
		}
		
		@Override
		public String toString() {
			return (String) data;
		}

		@Override
		public List<Datum> split(int size) {
			throw new IllegalStateException("Cannot allocate value of type " + this.getClass().getName());
		}

		@Override
		public Datum add(Datum datum) {
			throw new IllegalStateException("Cannot add types " + this.type + " and " + datum.type);
		}

		@Override
		public Datum subtract(Datum datum) {
			throw new IllegalStateException("Cannot subtract types " + this.type + " and " + datum.type);
		}

		@Override
		public Datum multiply(Datum datum) {
			throw new IllegalStateException("Cannot multiply types " + this.type + " and " + datum.type);
		}

		@Override
		public Datum divide(Datum datum) {
			throw new IllegalStateException("Cannot divide types " + this.type + " and " + datum.type);
		}
		
		@Override
		public Datum modulo(Datum datum) {
			throw new IllegalStateException("Cannot modulo types " + this.type + " and " + datum.type);
		}

		@Override
		public Datum clone() {
			return new Datum.Str((String)this.data);
		}
	}
	
	
	/**
	 * Inner {@code Datum} class to store {@link java.lang.Boolean}
	 * @author guru
	 *
	 */
	public static class Bool extends Datum {
		public Bool(Boolean data) {
			super(data);
			this.type = DatumType.BOOL;
		}
		
		@Override
		public String toString() {
			return Boolean.toString((Boolean) data);
		}

		@Override
		public List<Datum> split(int size) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Datum add(Datum datum) {
			throw new IllegalStateException("Cannot add types " + this.type + " and " + datum.type);
		}

		@Override
		public Datum subtract(Datum datum) {
			throw new IllegalStateException("Cannot subtract types " + this.type + " and " + datum.type);
		}

		@Override
		public Datum multiply(Datum datum) {
			throw new IllegalStateException("Cannot multiply types " + this.type + " and " + datum.type);
		}

		@Override
		public Datum divide(Datum datum) {
			throw new IllegalStateException("Cannot divide types " + this.type + " and " + datum.type);
		}
		
		@Override
		public Datum modulo(Datum datum) {
			throw new IllegalStateException("Cannot modulo types " + this.type + " and " + datum.type);
		}

		@Override
		public Datum clone() {
			return new Datum.Bool((Boolean) this.data);
		}
	}

	/**
	 * Enum enumerating the various datum types
	 * @author guru
	 *
	 */
	public enum DatumType {
		INT(Datum.Int.class),
		FLOAT(Datum.Flt.class),
		STRING(Datum.Str.class),
		BOOL(Datum.Bool.class),
		;
		
		private Class<?> clazz;
		
		private DatumType(Class<?> clazz) {
			this.clazz = clazz;
		}
		
		/**
		 * Parse a {@code Datum} class into a {@code DatumType}
		 * @param clazz {@code Class<?>} representing a concrete {@code Datum} class 
		 * @return {@code DatumType} representing {@code clazz}
		 * @throws IllegalArgumentException if {@code clazz} is not a valid {@code Datum}
		 */
		protected static DatumType parse(Class<?> clazz) {
			for(DatumType dt : values()) {
				if(dt.clazz.equals(clazz))
					return dt;
			}
			throw new IllegalArgumentException(clazz.getName() + " is not a valid DatumType!");
		}
	}
}
