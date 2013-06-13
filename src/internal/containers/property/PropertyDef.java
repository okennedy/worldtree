package internal.containers.property;

import internal.containers.Reference;
import internal.containers.Statement;
import internal.containers.StatementType;
import internal.containers.condition.ICondition;
import internal.containers.query.IQuery;

public class PropertyDef extends Statement {
	private String level, property;
	private Reference reference;
	private ICondition condition;
	private IQuery query;

	public PropertyDef(String level, Reference reference, String property, ICondition condition, IQuery query) {
		super(StatementType.PROPERTYDEF);
		this.level		= level;
		this.reference	= reference;
		this.property	= property;
		this.condition	= condition;
		this.query		= query;
	}

	@Override
	public String debugString() {
		StringBuffer result = new StringBuffer("PROPERTYDEF(DEFINE " + level + " ");
		result.append(reference.debugString() + "." + property + " AS " + condition.debugString() + 
				" IN " + query.debugString());
		
		return result.toString();
	}
	
	@Override
	public String toString() {
		return "DEFINE " + level + reference + "." + property + " AS " + condition + " IN " + query;
	}
}
