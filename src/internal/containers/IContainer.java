package internal.containers;

/**
 * Container interface that is used while parsing statements <br>
 * Provides the debugString() interface
 * @author guru
 *
 */
public interface IContainer {
	
	/**
	 * String representation of this container which is useful for debugging purposes
	 * @return {@code String}
	 */
	public String debugString();
}
