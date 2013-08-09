package internal.parser.containers.condition;

import internal.parser.TokenCmpOp;
import internal.parser.containers.Datum;
import internal.parser.containers.IContainer;
import internal.parser.containers.condition.BaseCondition.ConditionType;
import internal.parser.containers.property.Property;

public interface ICondition extends IContainer {
	
	/**
	 * Obtain the value contained by the <b>not</b> field of this {@code ICondition}
	 */
	public Boolean notFlag();
	
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
	 * Set the {@code value} field of this {@code ICondition}
	 * @param value {@code Datum} containing the value to set
	 */
	public void setValue(Datum value);
	
	/**
	 * Obtain the condition type of this {@code ICondition}
	 * @return {@code ConditionType}
	 */
	public ConditionType type();
}
