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
		StringBuffer result = new StringBuffer("AGGTYPE(");
		if(type.equals(AggType.COUNT)) {
			assert expr == null : type + " with expr :" + expr + " ?\n";
			result.append(type + "())");
		}
		else {
			result.append(" " + type);
			result.append(" " + expr.debugString());
		}
		return result.toString();
	}
	
	@Override
	public String toString() {
		if(type.equals(AggType.COUNT)) {
			assert expr == null : type + " with expr :" + expr + " ?\n";
			return type + "()";
		}
		
		StringBuffer result = new StringBuffer(type.toString());
		if(expr != null)
			result.append(" " + expr);
		else
			result.append(" COUNT");
		
		return result.toString();
	}
	
	
	public enum AggType {
		SUM		("SUM"),
		MAX		("MAX"),
		MIN		("MIN"),
		COUNT	("COUNT"),
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
