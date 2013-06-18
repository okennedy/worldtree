package internal.piece;

import java.util.ArrayList;

public interface IPiece extends ITileInterface {
	
	/**
	 * Get the list of valid neighbouring pieces with regard to a particular interface of this IPiece.
	 * @param it {@code TileInterfaceType} specifying the interface of this IPiece that is to be used
	 * @return {@code ArrayList<Piece>} containing list of valid pieces.
	 */
	public ArrayList<IPiece> getValidNeighbourPieces(TileInterfaceType it);
	
	/**
	 * Returns a visual representation of this IPiece.
	 * @return {@code String} containing visual representation of this IPiece.
	 */
	public String toString();
	
	/**
	 * Obtain {@code String} containing the textual representation of this IPiece.
	 * @return {@code String} containing the interfaces as defined for this IPiece.
	 */
	public String getText();
	
}
