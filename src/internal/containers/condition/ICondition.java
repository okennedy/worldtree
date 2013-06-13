package internal.containers.condition;

import internal.containers.IContainer;
import internal.containers.property.Property;

public interface ICondition extends IContainer {
	public Property property();
	public ICondition subCondition();
}
