package internal.piece;

import java.util.ArrayList;

public interface ITileInterface {
	public boolean validUp();
	public boolean validDown();
	public boolean validLeft();
	public boolean validRight();
	public boolean hasInterface(TileInterfaceType it);
	public ArrayList<TileInterfaceType> getValidInterfaces();
	public String toString();
	public String getBinaryInterfaces();
}
