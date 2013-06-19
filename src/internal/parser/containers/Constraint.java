package internal.parser.containers;

import internal.parser.containers.condition.ICondition;
import internal.parser.containers.query.IQuery;

/**
 * Container class for storing a constraint <br>
 * CONSTRAINT := ‘FOR’ ‘ALL’ LEVEL QUERY ‘ASSERT’ CONDITION
 * @author guru
 *
 */
public class Constraint extends Statement {
	private IQuery query;
	private ICondition condition;
	
	public Constraint(IQuery query, ICondition condition) {
		super(StatementType.CONSTRAINT);
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
