package internal.space;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import internal.piece.PieceFactory;
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
	private List<String> stringRepresentation;
	
	public Space(int x, int y) {
		super(y, x);
		matrix = new Tile[y][x];
		xCurr = 0;
		yCurr = yDimension - 1;
		stringRepresentation = new ArrayList<String>();
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
	
	
	public int[] currentCoordinates() {
		return new int[] {xCurr,yCurr};
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
		updateStringRepresentation(indices[1], indices[0]);
	}
	
	/**
	 * Set a given tile specified by array indices.
	 * @param xCoord
	 * @param yCoord
	 * @param tile {@code ITile} object that is to be set in the given indices of the array.
	 */
	public void setByArray(int xIndex, int yIndex, ITile tile) {
		matrix[xIndex][yIndex] = tile;
		updateStringRepresentation(xIndex, yIndex);
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
	 * @param xCoord
	 * @param yCoord
	 * @return {@code String} containing set of valid interfaces.
	 */
	public Map<String, String> getValidInterfaces(int xCoord, int yCoord) {
		int [] indices = coordToArray(xCoord, yCoord);
		int xIndex = indices[0];
		int yIndex = indices[1];
		
		StringBuffer mandatoryInterfaces = new StringBuffer();
		StringBuffer invalidInterfaces = new StringBuffer();
		if(yIndex + 1 == yDimension)
			invalidInterfaces.append("D");
		else if(matrix[yIndex + 1][xIndex] == null);
		else if(matrix[yIndex + 1][xIndex].piece().hasInterface(TileInterfaceType.U))
			mandatoryInterfaces.append("D");
		else
			invalidInterfaces.append("D");
		
		if(xIndex - 1 < 0)
			invalidInterfaces.append("L");
		else if(matrix[yIndex][xIndex - 1] == null);
		else if(matrix[yIndex][xIndex - 1].piece().hasInterface(TileInterfaceType.R))
			mandatoryInterfaces.append("L");
		else
			invalidInterfaces.append("L");
		
		
		if(xIndex + 1 == xDimension)
			invalidInterfaces.append("R");
		else if(matrix[yIndex][xIndex + 1] == null);
		else if(matrix[yIndex][xIndex + 1].piece().hasInterface(TileInterfaceType.L))
			mandatoryInterfaces.append("R");
		else
			invalidInterfaces.append("R");
		
		
		if(yIndex - 1 < 0)
			invalidInterfaces.append("U");
		else if(matrix[yIndex - 1][xIndex] == null);
		else if(matrix[yIndex - 1][xIndex].piece().hasInterface(TileInterfaceType.D))
			mandatoryInterfaces.append("U");
		else
			invalidInterfaces.append("U");
		
		Map<String, String> interfaceMap = new HashMap<String, String>();
		interfaceMap.put("mandatoryInterfaces", mandatoryInterfaces.toString());
		interfaceMap.put("invalidInterfaces", invalidInterfaces.toString());
		return interfaceMap;
	}
	
	public List<String> getStringRepresentation() {
		stringRepresentation.removeAll(stringRepresentation);
		List<List<String>> listStringList = new ArrayList<List<String>>();
		for(int i = 0; i < getYDimension(); i++) {
			List<String> stringList = new ArrayList<String>();
			for(int j = 0; j < getXDimension(); j++) {
				if(getByArray(i, j) != null)
					stringList.add(getByArray(i, j).piece().toString());
				else
					stringList.add(PieceFactory.newPiece("").toString());
			}
			listStringList.add(stringList);
		}
		
//		We use one instance of a piece's toString() to test for number of lines.
		int lineCount = listStringList.get(0).get(0).split("\n").length;
		
		for(int yIndex = 0; yIndex < listStringList.size(); yIndex++) {
			List<String> stringList = listStringList.get(yIndex);
			for(int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
				StringBuffer fullLine = new StringBuffer(); 
				for(int xIndex = 0; xIndex < stringList.size(); xIndex++) {
					String[] stringArray = null;
					try {
						 stringArray = stringList.get(xIndex).split("\n");
						fullLine.append(stringArray[lineIndex] + " ");
					} catch(ArrayIndexOutOfBoundsException e) {
						System.err.println("size :" + stringList.size() + "\n" + stringList.get(xIndex));
						e.printStackTrace();
					}
					
				}
//				if(!stringRepresentation.contains(fullLine.toString()))
					stringRepresentation.add(fullLine.toString());
			}
		}
		return stringRepresentation;
	}
	
	private void updateStringRepresentation(int xIndex, int yIndex) {
//		First we find the 'line' that holds this tile (using yIndex)
		getStringRepresentation();
	}

	/**
	 * Set current coordinates
	 * @param x
	 * @param y
	 */
	public void setCurrentCoordinates(int x, int y) {
		xCurr = x;
		yCurr = y;
	}
	
	/**
	 * Get a {@code Collection} of neighboring tiles
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @return {@code Collection<ITile>} containing neighbouring tiles
	 */
	public Collection<ITile> getNeighbours(int x, int y) {
		
//		pre-processing
		int[] indices = coordToArray(x, y);
		x = indices[0];
		y = indices[1];
		
		Collection<ITile> returnCollection = new ArrayList<ITile>();
		
		int[] oldCurrent = currentCoordinates();
		setCurrentCoordinates(x, y);
		returnCollection.add(nextUp());
		returnCollection.add(nextDown());
		returnCollection.add(nextLeft());
		returnCollection.add(nextRight());
		
		setCurrentCoordinates(oldCurrent[0], oldCurrent[1]);
		
		return returnCollection;
	}
	
	/**
	 * Get a {@code Collection} of neighboring tiles from current tile
	 * @return {@code Collection<ITile>} containing neighboring tiles
	 */
	public Collection<ITile> getNeighbours() {
		return getNeighbours(xCurr, yCurr);
	}
}