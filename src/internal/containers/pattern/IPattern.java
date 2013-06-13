package internal.containers.pattern;

import internal.containers.IContainer;

public interface IPattern extends IContainer {
	public IPattern base();
	public IPattern subPattern();
}
