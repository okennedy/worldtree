package internal.parser.containers.condition;

import development.com.collection.range.Range;
import internal.parser.TokenCmpOp;
import internal.parser.containers.Datum;
import internal.parser.containers.Reference;
import internal.parser.containers.Datum.Bool;
import internal.parser.containers.property.Property;
import internal.parser.resolve.Result;
import internal.tree.IWorldTree;

/**
 * Container class for storing a condition <br>
 * BASECONDITION := PROPERTY | ‘NOT’ BASECONDITION | ‘(’ CONDITION ‘)’
 * @author guru
 *
 */
public class BaseCondition implements ICondition {
	private boolean not;
	private ConditionType type;
	private Reference reference;
	private Property property;
	private TokenCmpOp cmpOp;
	private Datum value;
	private Range valueRange;
	
	public BaseCondition(boolean not, ConditionType type, Reference reference, Property property, TokenCmpOp op, Datum value) {
		this.not		= not;
		this.type		= type;
		this.reference	= reference;
		this.property	= property;
		this.cmpOp		= op;
		this.value		= value;
	}
	
	@Override
	public Boolean notFlag() {
		return not;
	}
	
	@Override
	public Reference reference() {
		return reference;
	}
	
	@Override
	public Property property() {
		return property;
	}

	@Override
	public ICondition subCondition() {
		return null;
	}
	
	@Override
	public ConditionType type() {
		return type;
	}
	
	@Override
	public UnionType unionType() {
		return null;
	}

	@Override
	public TokenCmpOp operator() {
		return cmpOp;
	}

	@Override
	public Datum value() {
		if(value != null)
			return value;
		else if(valueRange != null)
			return valueRange.generateRandom();
		else
			throw new IllegalStateException("Value in condition '" + this.toString() + "' is null\n");
	}
	

	@Override
	public void setNotFlag(Boolean flag) {
		this.not = flag;
	}

	@Override
	public void setReference(Reference reference) {
		this.reference = reference;
	}
	
	@Override
	public void setProperty(Property property) {
		this.property = property;
	}

	@Override
	public void setSubCondition(ICondition subCondition) {
		System.err.println("Calling method setSubCondition on type " + this.getClass().getName() + " is not supported");
	}

	@Override
	public void setOperator(String op) {
		this.cmpOp = TokenCmpOp.parse(op);
	}
	
	@Override
	public void setValue(Datum value) {
		this.value = value;
	}

	@Override
	public void setType(ConditionType type) {
		this.type = type;
	}
	
	@Override
	public void setValueRange(Range range) {
		this.valueRange = range;
	}
	
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		if(not)
			result.append("NOT ");
		
		result.append(reference.toString() + "." + property.toString());
		switch(type) {
		case BASIC:
			result.append(" " + cmpOp + " " + value);
			break;
		case BOOLEAN:
//			TODO
			break;
		case COMPLEX:
//			TODO
			break;
		default:
			break;
		}
		
		return result.toString();
	}

	@Override
	public String debugString() {
		StringBuffer result = new StringBuffer("CONDITION(");
		
		if(not)
			result.append("NOT ");
		
		result.append(reference.debugString() + "." + property.debugString());
		switch(type) {
		case BASIC:
			result.append(" " + cmpOp + " " + value);
			break;
		case BOOLEAN:
//			TODO
			break;
		case COMPLEX:
//			TODO
			break;
		default:
			break;
		}
		
		return result.toString();
	}
	
	
	public enum ConditionType {
		BASIC,
		BOOLEAN,
		COMPLEX,
		;
	}


	@Override
	public Datum evaluate(IWorldTree node, Result result) {
		IWorldTree referenceNode	= result.get(reference).get(0);	//FIXME: This may not be the right approach...
		Datum propertyValue			= referenceNode.properties().get(property);
		boolean conditionResult		= false;
		switch(type) {
		case BASIC:
			if(propertyValue.compareTo(value, cmpOp) == 0)
				conditionResult = true;
			break;
		case BOOLEAN:
			if(propertyValue != null)
				conditionResult = ((Integer) propertyValue.toInt().data()).intValue() > 0 ? true : false;
			break;
		case COMPLEX:
//			TODO
			break;
		default:
			break;
		}
		if(not)
			conditionResult = !conditionResult;
		return new Datum.Bool(conditionResult);
	}
}
