package internal.parser.containers;

import internal.parser.TokenCmpOp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Datum {
	Object data;
	DatumType type;
	
	protected Datum() {
//		Stops empty constructor initialization
	}
	
	private Datum(Integer data) {
		this.data 	= data;
		this.type 	= DatumType.INT;
	}
	
	private Datum(Float data) {
		this.data	= data;
		this.type	= DatumType.FLOAT;
	}
	
	private Datum(String data) {
		this.data	= data;
		this.type	= DatumType.STRING;
	}
	
	private Datum(Boolean data) {
		this.data	= data;
		this.type	= DatumType.BOOL;
	}
	
	public Object data() {
		return data;
	}
	
	public DatumType type() {
		return type;
	}
	
	public abstract List<Datum> split(int size);
	public abstract Datum add(Datum datum);
	public abstract Datum subtract(Datum datum);
	public abstract Datum multiply(Datum datum);
	public abstract Datum divide(Datum datum);
	public abstract Datum modulo(Datum datum);
	public abstract Datum clone();
	
	public Datum toInt() {
		Datum datum = null;
		
		if(type.equals(DatumType.INT)) {
			datum	= this;
		}
		
		else if(type.equals(DatumType.FLOAT)) {
			datum	= new Datum.Int(new Integer((Integer.parseInt(Float.toString((Float) this.data)))));
		}
		
		else if(type.equals(DatumType.STRING)) {
			datum	= new Datum.Int(new Integer(Integer.parseInt((String) this.data)));
		}
		datum.type	= DatumType.INT;
		return datum;
	}
	
	public Datum toFlt() {
		Datum datum = null;
		
		if(type.equals(DatumType.INT)) {
			datum	= new Datum.Flt(new Float((Integer) this.data));
		}
		
		else if(type.equals(DatumType.FLOAT)) {
			datum	= this;
		}
		
		else if(type.equals(DatumType.STRING)) {
			datum	= new Datum.Flt(new Float(Float.parseFloat((String) this.data)));
		}
		datum.type	= DatumType.FLOAT;
		return datum;
	}
	
	public Datum toStr() {
		Datum datum = null;
		
		if(type.equals(DatumType.INT)) {
			datum	= new Datum.Str(Integer.toString((Integer)this.data));
		}
		
		else if(type.equals(DatumType.FLOAT)) {
			datum	= new Datum.Str(Float.toString((Float) this.data));
		}
		
		else if(type.equals(DatumType.STRING)) {
			datum	= this;
		}
		
		datum.type	= DatumType.STRING;
		
		return datum;
	}
	
	
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
		case INT:
		case FLOAT: {
			float val1 = (Float) toFlt().data;
			float val2 = (Float) datum.toFlt().data;
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
			break;
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
	
	
	
	public static class Int extends Datum {
		public Int(Integer data) {
			super(data);
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
			
			int data 			= (Integer) this.data;
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
	
	
	
	public static class Flt extends Datum {
		public Flt(Float data) {
			super(data);
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
	
	
	
	public static class Str extends Datum {
		public Str(String data) {
			super(data);
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
	
	
	
	public static class Bool extends Datum {
		public Bool(Boolean data) {
			super(data);
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
		
		protected static DatumType parse(Class<?> clazz) {
			for(DatumType dt : values()) {
				if(dt.clazz.equals(clazz))
					return dt;
			}
			throw new IllegalArgumentException(clazz.getName() + " is not a valid DatumType!");
		}
	}
}
