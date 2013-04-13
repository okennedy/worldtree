package internal.tree.tile.space;

import internal.piece.TileInterfaceType;
import internal.piece.Piece;

/**
 * The cell class is used to fill up the space.
 * @author guru
 *
 */
public class Cell {
	private Piece piece;
	
	public Cell() {
	}
	
	public Cell(Piece p) {
		this.piece = p;
	}
	
	/**
	 * Check whether this cell has the specified interface
	 * @param it {@code InterfaceType} the interface to check for
	 * @return True if the cell contains this interface; false otherwise
	 */
	public boolean hasInterface(TileInterfaceType it) {
		return piece.getValidInterfaces().contains(it.toString());
	}

	/**
	 * Get the current piece that is stored in the cell
	 * @return {@code Piece} object stored by this cell.
	 */
	public Piece getPiece() {
		return piece;
	}
}
