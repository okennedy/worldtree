package internal.containers.condition;

import internal.containers.IContainer;
import internal.containers.Property;

public interface ICondition extends IContainer {
	public String statement();
	public Property property();
	public ICondition subCondition();
}
