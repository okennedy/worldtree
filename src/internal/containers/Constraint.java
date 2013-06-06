package internal.containers;

import internal.containers.condition.ICondition;
import internal.containers.query.IQuery;

public class Constraint implements IContainer {
	private IQuery query;
	private ICondition condition;
	
	public Constraint(IQuery query, ICondition condition) {
		this.query		= query;
		this.condition	= condition;
	}

	
	@Override
	public String toString() {
		return "FOR ALL " + query.toString() + " ASSERT " + condition.toString();
	}
	
	@Override
	public String debugString() {
		StringBuffer result = new StringBuffer("CONSTRAINT(FOR ALL ");
		
		result.append(query.debugString());
		result.append(" ASSERT ");
		result.append(condition.debugString());
		result.append(")");
		
		return result.toString();
	}
}
