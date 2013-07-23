package internal.parser.containers.expr;

import internal.parser.containers.IContainer;

public class AggExpr implements IContainer {
	private AggType type;
	private IExpr expr;
	
	public AggExpr(AggType type, IExpr expr) {
		this.type	= type;
		this.expr	= expr;
	}
	
	@Override
	public String debugString() {
		StringBuffer returnString = new StringBuffer("AGGTYPE(" + type);
		if(expr != null)
			returnString.append(" " + expr.debugString());
		else
			returnString.append(" COUNT");
		
		return returnString.toString();
	}
	
	@Override
	public String toString() {
		StringBuffer returnString = new StringBuffer(type.toString());
		if(expr != null)
			returnString.append(" " + expr);
		else
			returnString.append(" COUNT");
		
		return returnString.toString();
	}
	
	
	public enum AggType {
		SUM("SUM"),
		MAX("MAX"),
		MIN("MIN"),
		;
		
		private String type;
		
		private AggType(String type) {
			this.type = type;
		}
		
		public static AggType parse(String type) {
			for(AggType t : values()) {
				if(t.toString().equalsIgnoreCase(type))
					return t;
			}
			return null;
		}
		
		@Override
		public String toString() {
			return type;
		}
	}

	
}
