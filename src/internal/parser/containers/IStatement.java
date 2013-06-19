package internal.parser.containers;

/**
 * Interface that is extended by the abstract class Statement.
 * @author guru
 *
 */
public interface IStatement extends IContainer {
	
	/**
	 * Allows identification of the type of this Statement
	 * @return {@code StatementType} representing the type of this Statement
	 */
	public StatementType getType();
}
