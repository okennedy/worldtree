package internal.tree.tile.space;

import java.util.ArrayList;
import java.util.List;

import internal.piece.TileInterfaceType;
import internal.tree.IWorldTree;
import internal.tree.IWorldTree.ITile;
import internal.tree.WorldTreeFactory.Tile;

/**
 * The Space class aims to provide methods to traverse the space easily.
 * The Space class consists of Tiles in a 2-Dimensional array.
 * <p>
 * This implementation of Space tries to emulate Cartesian behavior.
 * All methods of access - read/write can be passed in with Cartesian coordinates instead of array indices
 * @author Guru
 *
 */
public class Space extends Dimension {
	protected int xCurr, yCurr;
	protected ITile[][] matrix;
	
	public Space(int x, int y) {
		super(y, x);
		matrix = new Tile[y][x];
		xCurr = 0;
		yCurr = yDimension - 1;
	}
	
	public int[] arrayToCoord(int x, int y) {
		return new int[] {x, (yDimension - y - 1)};
	}
	
	public int[] coordToArray(int x, int y) {
		return new int[] {x, yDimension - y - 1};
	}
	/**
	 * Returns the next Tile to the left
	 * @return {@code Tile} object to the left of the current Tile
	 */
	public ITile nextLeft() {
		assert(xCurr - 1 != -1);
		return matrix[yCurr][xCurr - 1];
	}
	
	/**
	 * Returns the next Tile to the right
	 * @return {@code Tile} object to the right of the current Tile
	 */
	public ITile nextRight() {
		assert(xCurr + 1 != yDimension);
		return matrix[yCurr][xCurr + 1];
	}
	
	/**
	 * Returns the next Tile on top of the current Tile
	 * @return {@code Tile} object to the top of the current Tile
	 */
	public ITile nextUp() {
		assert(yCurr - 1 != -1);
		return matrix[yCurr - 1][xCurr];
	}
	
	/**
	 * Returns the next Tile below the current Tile
	 * @return {@code Tile} object below the current Tile
	 */
	public ITile nextDown() {
		assert(yCurr + 1 != xDimension);
		return matrix[yCurr + 1][xCurr];
	}

	/**
	 * Returns the current Tile
	 * @return {@code Tile} object referencing the current Tile
	 */
	public ITile currentTile() {
		return matrix[xCurr][yCurr];
	}
	
	/**
	 * Retrieve the Cell that is being represented by the given set of Cartesian coordinates.
	 * @param xCoord
	 * @param yCoord
	 * @return {@code ITile} representing the given set of coordinates.
	 */
	public ITile getByCoord(int xCoord, int yCoord) {
		return matrix[yDimension - yCoord - 1][xCoord];
	}
	
	/**
	 * Retrieve the Cell that is being represented by the given set of array indices.
	 * @param xIndex
	 * @param yIndex
	 * @return {@code ITile} representing the given set of indices.
	 */
	public ITile getByArray(int xIndex, int yIndex) {
		return matrix[xIndex][yIndex];
	}

	/**
	 * Current x-coordinate
	 * @return {@code Integer} holding the current x-coordinate.
	 */
	public int xCoord() {
		return xCurr;
	}
	
	/**
	 * Current y-coordinate
	 * @return {@code Integer} holding the current y-coordinate.
	 */
	public int yCoord() {
		return yCurr;
	}
	
	/**
	 * Set a given tile specified by Cartesian coordinates.
	 * @param xCoord
	 * @param yCoord
	 * @param tile {@code ITile} object that is to be set in the given coordinates.
	 */
	public void setByCoord(int xCoord, int yCoord, ITile tile) {
		int[] indices = coordToArray(xCoord, yCoord);
		matrix[indices[1]][indices[0]] = tile;
	}
	
	/**
	 * Set a given tile specified by array indices.
	 * @param xCoord
	 * @param yCoord
	 * @param tile {@code ITile} object that is to be set in the given indices of the array.
	 */
	public void setByArray(int xIndex, int yIndex, ITile tile) {
		matrix[xIndex][yIndex] = tile;
	}

	/**
	 * Obtain a collection of all {@code ITile} objects used in this Space.
	 * @return {@code List<IWorldTree} containing every {@code ITile} from this Space.
	 */
	public List<IWorldTree> collection() {
		List<IWorldTree> returnList = new ArrayList<IWorldTree>();
		for(int i = 0; i < getXDimension(); i++) {
			for(int j = 0; j < getYDimension(); j++) {
				if(matrix[i][j] != null)
					returnList.add(matrix[i][j]);
			}
		}
		return returnList;
	}

	/**
	 * Check surrounding entries in the space to find out valid interfaces. Also check for corner cases (literally).
	 * @param xIndex
	 * @param yIndex
	 * @return {@code String} containing set of valid interfaces.
	 */
	public String getValidInterfaces(int yIndex, int xIndex) {
		StringBuffer validInterfaces = new StringBuffer();
		if(yIndex + 1 == yDimension)
			validInterfaces.append("!D");
		else if(matrix[xIndex][yIndex + 1] == null)
			validInterfaces.append("D");
		else if(matrix[xIndex][yIndex + 1].piece().hasInterface(TileInterfaceType.U))
			validInterfaces.append("D");
		else
			validInterfaces.append("!D");
		
		if(xIndex - 1 < 0)
			validInterfaces.append("!L");
		else if(matrix[xIndex - 1][yIndex] == null)
			validInterfaces.append("L");
		else if(matrix[xIndex - 1][yIndex].piece().hasInterface(TileInterfaceType.R))
			validInterfaces.append("L");
		else
			validInterfaces.append("!L");
		
		
		if(xIndex + 1 == xDimension)
			validInterfaces.append("!R");
		else if(matrix[xIndex + 1][yIndex] == null)
			validInterfaces.append("R");
		else if(matrix[xIndex + 1][yIndex].piece().hasInterface(TileInterfaceType.L))
			validInterfaces.append("R");
		else
			validInterfaces.append("!R");
		
		
		if(yIndex - 1 < 0)
			validInterfaces.append("!U");
		else if(matrix[xIndex][yIndex - 1] == null)
			validInterfaces.append("U");
		else if(matrix[xIndex][yIndex - 1].piece().hasInterface(TileInterfaceType.D))
			validInterfaces.append("U");
		else
			validInterfaces.append("!U");
		
		return validInterfaces.toString();
	}
}