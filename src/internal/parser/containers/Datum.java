package internal.parser.containers;

public class Datum {
	Object data;
	Class<?> type;
	
	protected Datum() {
//		Stops empty constructor initialization
	}
	
	private Datum(Integer data) {
		this.data 	= data;
		this.type 	= Datum.Int.class;
	}
	
	private Datum(Float data) {
		this.data	= data;
		this.type	= Datum.Int.class;
	}
	
	private Datum(String data) {
		this.data	= data;
		this.type	= Datum.Str.class;
	}
	
	private Datum(Boolean data) {
		this.data	= data;
		this.type	= Datum.Bool.class;
	}
	
	public Datum toInt() {
		Datum datum = new Datum();
		
		if(type.equals(Datum.Int.class)) {
			datum.data	= new Integer((Integer) this.data);
		}
		
		else if(type.equals(Datum.Flt.class)) {
			datum.data	= new Integer((Integer.parseInt(Float.toString((Float) this.data))));
		}
		
		else if(type.equals(Datum.Str.class)) {
			datum.data	= new Integer(Integer.parseInt((String) this.data));
		}
		datum.type	= Datum.Int.class;
		return datum;
	}
	
	public Datum toFlt() {
		Datum datum = new Datum();
		
		if(type.equals(Datum.Int.class)) {
			datum.data	= new Float((Integer) this.data);
		}
		
		else if(type.equals(Datum.Flt.class)) {
			datum.data	= new Float((Float) this.data);
		}
		
		else if(type.equals(Datum.Str.class)) {
			datum.data	= new Float(Float.parseFloat((String) this.data));
		}
		datum.type	= Datum.Flt.class;
		return datum;
	}
	
	public Datum toStr() {
		Datum datum = new Datum();
		
		if(type.equals(Datum.Int.class)) {
			datum.data	= Integer.toString((Integer)this.data);
		}
		
		else if(type.equals(Datum.Flt.class)) {
			datum.data	= Float.toString((Float) this.data);
		}
		
		else if(type.equals(Datum.Str.class)) {
			datum.data	= new String((String) this.data);
		}
		
		datum.type	= Datum.Str.class;
		
		return datum;
	}
	
	
	
	
	
	public static class Int extends Datum {
		public Int(Integer data) {
			super(data);
		}
		
		@Override
		public String toString() {
			return Integer.toString((Integer) data);
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
	}
	
	
	
	public static class Str extends Datum {
		public Str(String data) {
			super(data);
		}
		
		@Override
		public String toString() {
			return (String) data;
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
	}
}
