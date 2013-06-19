package internal.parser.containers;

/**
 * Statement is an abstract container class that can hold different types of statements
 * @author guru
 *
 */
public abstract class Statement implements IStatement {
	StatementType type;
	
	public Statement(StatementType type) {
		this.type	= type;
	}
	
	public StatementType getType() {
		return type;
	}
}
