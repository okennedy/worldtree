package internal.containers;

public abstract class Statement implements IStatement {
	StatementType type;
	
	public Statement(StatementType type) {
		this.type	= type;
	}
	
	public StatementType getType() {
		return type;
	}
}
