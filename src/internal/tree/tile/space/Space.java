package internal.tree.tile.space;

import java.util.ArrayList;
import java.util.List;

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
	
	public ITile getByCoord(int xCoord, int yCoord) {
		return matrix[yDimension - yCoord - 1][xCoord];
	}
	
	public ITile getByArray(int xIndex, int yIndex) {
		return matrix[xIndex][yIndex];
	}

	public int xCoord() {
		return xCurr;
	}
	
	public int yCoord() {
		return yCurr;
	}
	
	public void setByCoord(int xCoord, int yCoord, ITile tile) {
		int[] indices = coordToArray(xCoord, yCoord);
		matrix[indices[1]][indices[0]] = tile;
	}
	
	public void setByArray(int xIndex, int yIndex, ITile tile) {
		matrix[xIndex][yIndex] = tile;
	}

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
}
