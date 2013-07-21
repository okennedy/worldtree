package internal.parser.containers.condition;

import internal.parser.TokenCmpOp;
import internal.parser.containers.IContainer;
import internal.parser.containers.property.Property;

public interface ICondition extends IContainer {
	
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
	 * @return {@code String}
	 */
	public String value();
}
