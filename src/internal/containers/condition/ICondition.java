package internal.containers.condition;

import internal.containers.Property;

public interface ICondition {
	public String statement();
	public Property property();
	public ICondition subCondition();
}
