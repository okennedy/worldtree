package internal.parser.containers.condition;

import development.com.collection.range.Range;
import internal.parser.TokenCmpOp;
import internal.parser.containers.Datum;
import internal.parser.containers.IContainer;
import internal.parser.containers.Reference;
import internal.parser.containers.condition.BaseCondition.ConditionType;
import internal.parser.containers.property.Property;

public interface ICondition extends IContainer {
	
	/**
	 * Obtain the value contained by the <b>not</b> field of this {@code ICondition}
	 */
	public Boolean notFlag();
	
	/**
	 * Obtain the {@code Reference} on which this {@code ICondition} is based
	 * @return {@code Reference}
	 */
	public Reference reference();
	
	/**
	 * Obtain the {@code Property} on which this {@code ICondition} is based
	 * @return {@code Property}
	 */
	public Property property();
	
	/**
	 * Obtain the sub-condition(if any)
	 * @return {@code ICondition} if there exists a sub-condition <br>
	 * <b>null</b> otherwise
	 */
	public ICondition subCondition();
	
	/**
	 * Obtain the comparison operator used for this {@code ICondition}
	 * @return {@code TokenCmpOp}
	 */
	public TokenCmpOp operator();
	
	/**
	 * Obtain the comparison value
	 * @return {@code Datum}
	 */
	public Datum value();
	
	/**
	 * Obtain the condition type of this {@code ICondition}
	 * @return {@code ConditionType}
	 */
	public ConditionType type();
	
	
//	Setters
	/**
	 * Set the value of the <b>not</b> field of this {@code ICondition}
	 * @param flag {@code Boolean} to be set
	 */
	public void setNotFlag(Boolean flag);
	
	/**
	 * Set the {@code Reference} on which this {@code ICondition} is based
	 * @param reference {@code Reference} to be set
	 */
	public void setReference(Reference reference);

	/**
	 * Set the {@code Property} on which this {@code ICondition} is based
	 * @param property {@code Property} to be set
	 */
	public void setProperty(Property property);

	/**
	 * Set the sub-condition(if any)
	 * @param subCondition {@code ICondition} to be set
	 */
	public void setSubCondition(ICondition subCondition);
	
	/**
	 * Set the comparison operator used for this {@code ICondition} <br>
	 * For simplicity, this method accepts a string and internally converts it into type {@code TokenCmpOp}
	 * @param op {@code String} containing the operator to be set
	 * @see TokenCmpOp
	 */
	public void setOperator(String op);
	
	/**
	 * Set the {@code value} field of this {@code ICondition}
	 * @param value {@code Datum} containing the value to set
	 */
	public void setValue(Datum value);
	
	/**
	 * Set the {@code valueRange} field of this {@code ICondition}
	 * @param value {@code Range} containing the range of values to set
	 */
	public void setValueRange(Range range);
	
	/**
	 * Set the condition type of this {@code ICondition}
	 * @param type {@code ConditionType} to be set
	 * @see ConditionType
	 */
	public void setType(ConditionType type);
}
