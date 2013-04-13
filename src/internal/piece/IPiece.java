package internal.piece;

import java.util.ArrayList;

public interface IPiece extends ITileInterface {
	public ArrayList<IPiece> getValidNeighbourPieces(TileInterfaceType it);
	public String toString();
	public String getText();
	
}
