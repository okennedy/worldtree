package internal.containers.pattern;

import internal.containers.IContainer;
import internal.containers.Reference;
import internal.containers.Relation;

public interface IPattern extends IContainer {
	public Reference lhs();
	public Reference rhs();
	public Relation  relation();
	public IPattern  subPattern();
}
