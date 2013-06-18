package internal.piece;

import java.util.List;


public interface ITileInterface {
	
	/**
	 * @return {@code true} if the current interface has open interface above<br>
	 * {@code false} otherwise
	 */
	public boolean validUp();

	/**
	 * @return {@code true} if the current interface has open interface below <br>
	 * {@code false} otherwise
	 */
	public boolean validDown();
	
	/**
	 * @return {@code true} if the current interface has open interface to the left <br>
	 * {@code false} otherwise
	 */

	public boolean validLeft();
	
	/**
	 * @return {@code true} if the current interface has open interface to the right<br>
	 * {@code false} otherwise
	 */

	public boolean validRight();
	
	/**
	 * Check if this object has a particular {@code TileInterfaceType} open
	 * @param it {@code TileInterfaceType} containing the {@code TileInterfaceType} to check for
	 * @return {@code true} if this interface has the specified {@code TileInterfaceType} <br>
	 * {@code false} otherwise
	 */
	public boolean hasInterface(TileInterfaceType it);
	
	/**
	 * Obtain {@code List<TileInterfaceType>} containing valid {@code TileInterfaceType}s 
	 * @return {@code List<InterfaceType>} containing list of all valid InterfaceTypes
	 */
	public List<TileInterfaceType> getValidInterfaces();
	
	/**
	 * Obtain a string representation of the interfaces that are available.
	 * @return {@code String} representing the available interfaces
	 */
	public String toString();
	
	/**
	 * Obtain a binary representation of the interfaces that are available.
	 * @return {@code String} representing the interfaces in binary format
	 */
	public String getBinaryInterfaces();
}
