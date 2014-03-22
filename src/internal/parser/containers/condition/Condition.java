package internal.parser.containers.condition;

import development.com.collection.range.Range;
import internal.parser.TokenCmpOp;
import internal.parser.containers.Datum;
import internal.parser.containers.Reference;
import internal.parser.containers.Datum.Bool;
import internal.parser.containers.condition.BaseCondition.ConditionType;
import internal.parser.containers.property.Property;
import internal.parser.resolve.Result;
import internal.tree.IWorldTree;

/**
 * Container class for storing a condition <br>
 * COND = BASECONDITION ((‘AND’ | ‘OR’) BASECONDITION)*
 * @author guru
 *
 */
public class Condition implements ICondition {
	private ICondition baseCondition;
	UnionType unionType;
	private ICondition subCondition;
	
	public Condition(boolean not, ICondition condition) {
		this.baseCondition	= new BaseCondition(not, condition.type(), condition.reference(), condition.property(), condition.operator(), condition.value());
		this.subCondition	= condition.subCondition();
	}
	
	public Condition(ICondition baseCondition, UnionType unionType, ICondition subCondition) {
		this.baseCondition	= baseCondition;
		this.unionType		= unionType;
		this.subCondition	= subCondition;
	}
	
	@Override
	public Boolean notFlag() {
		return baseCondition.notFlag();
	}

	@Override
	public Reference reference() {
		return baseCondition.reference();
	}
	
	@Override
	public Property property() {
		return baseCondition.property();
	}

	@Override
	public ICondition subCondition() {
		return subCondition;
	}
	
	@Override
	public TokenCmpOp operator() {
		return baseCondition.operator();
	}

	@Override
	public Datum value() {
		return baseCondition.value();
	}
	
	@Override
	public void setValue(Datum value) {
		baseCondition.setValue(value);
	}
	
	@Override
	public void setValueRange(Range range) {
		baseCondition.setValueRange(range);
	}
	
	@Override
	public ConditionType type() {
		return ConditionType.COMPLEX;
	}
	
	@Override
	public UnionType unionType() {
		return unionType;
	}
	
	@Override
	public void setNotFlag(Boolean flag) {
		baseCondition.setNotFlag(flag);
	}

	@Override
	public void setReference(Reference reference) {
		baseCondition.setReference(reference);
	}

	@Override
	public void setProperty(Property property) {
		baseCondition.setProperty(property);
	}

	@Override
	public void setSubCondition(ICondition subCondition) {
		this.subCondition = subCondition;
	}

	@Override
	public void setOperator(String op) {
		baseCondition.setOperator(op);
	}

	@Override
	public void setType(ConditionType type) {
		baseCondition.setType(type);
	}
	
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(baseCondition + " ");
		
		if(subCondition != null)
			result.append(unionType + " " + subCondition);

		return result.toString();
	}

	@Override
	public String debugString() {
		StringBuffer result = new StringBuffer("CONDITION(" + baseCondition.debugString());
		
		if(subCondition != null)
			result.append(" " + unionType + " " + subCondition.debugString());

		result.append(")");
		return result.toString();
	}

	@Override
	public Datum evaluate(IWorldTree node, Result result) {
		boolean baseConditionResult, subConditionResult;
		boolean conditionResult = false;
		if(unionType != null) {
			baseConditionResult	= (Boolean) baseCondition.evaluate(node, result).data();
			subConditionResult	= (Boolean) subCondition.evaluate(node, result).data();
			switch(unionType) {
			case AND:
				conditionResult = baseConditionResult & subConditionResult;
				break;
			case OR:
				conditionResult = baseConditionResult | subConditionResult;
				break;
			}
			return new Datum.Bool(conditionResult);
		}
		else
			return baseCondition.evaluate(node, result);
	}
}
