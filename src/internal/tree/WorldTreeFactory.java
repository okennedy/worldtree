package internal.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import internal.piece.Piece;
import internal.piece.TileInterfaceType;
import internal.tree.IWorldTree.IMap;
import internal.tree.IWorldTree.IRoom;
import internal.tree.IWorldTree.IRegion;
import internal.tree.IWorldTree.ITile;
import internal.tree.IWorldTree.IObject;
import internal.tree.tile.space.Space;


public class WorldTreeFactory {

	private  class Map extends WorldTree implements IMap {
		public Map(String name, IWorldTree parent, Constraint constraints) {
			super(name, parent, constraints);
		}

		@Override
		public void initialize() {
			IWorldTree root = null;
			List<IWorldTree> possibleRoots = new ArrayList<IWorldTree>();
			possibleRoots.add(this);
			while(true) {
				List<IWorldTree> oldRootList = new ArrayList<IWorldTree>(possibleRoots);
				root = possibleRoots.get(0);
				while(root.children() != null) {
					for(IWorldTree child : root.children())
						possibleRoots.add(child);
					possibleRoots.remove(root);
					break;
				}
				
				if(oldRootList.containsAll(possibleRoots) && possibleRoots.containsAll(oldRootList))
					break;
			}
			
			if(root == this) {
				children = new ArrayList<IWorldTree>();
				for(int i = 0; i < 3; i++) {
					children.add(new Room("Room" + i, this, null));
				}
			}
			
			else {
				for(IWorldTree child : possibleRoots) {
					child.initialize();
				}
			}
		}
	}
	
	public IMap newMap(String name, IWorldTree parent, Constraint constraints) {
		return new Map(name, parent, constraints);
	}
	
	private  class Room extends WorldTree implements IRoom {
		public Room(String name, IWorldTree parent, Constraint constraints) {
			super(name, parent, constraints);
		}

//		The Room must decide the location of the tiles (I think..)
		@Override
		public void initialize() {
			children = new ArrayList<IWorldTree>();
			String regionNames[] = {
					"Pit",
					"Normal",
					"Stairs",
					"Spikes",
					"Cave"
			};
			for(int i = 0; i < 4; i++) {
				int nextInt = (new Random()).nextInt(5);
				children.add(newRegion(regionNames[nextInt], this, null, new Space(nextInt + 1, nextInt + 1)));
			}
		}
	}
	
	public IRoom newRoom(String name, IWorldTree parent, Constraint constraints) {
		return new Room(name, parent, constraints);
	}
	
	private  class Region extends WorldTree implements IRegion {
		private Space space;
		public Region(String name, IWorldTree parent, Constraint constraints, Space space) {
			super(name, parent, constraints);
			this.space = space;
			stringRepresentation = new ArrayList<String>();
		}

		@Override
		public void initialize() {
			for(int i = 0; i < space.getXDimension(); i++) {
				for(int j = 0; j < space.getYDimension(); j++) {
					ITile tile = newTile("" + space.arrayToCoord(space.xCoord(), space.yCoord()), parent, null, Piece.randomPiece());
					space.setByArray(i, j, tile);
					System.out.print("");
				}
			}
			initString();
		}
		
		/**
		 * This is some really ugly code where multi-line visuals of each tile are split into single lines and
		 * each line of each tile is appended together to the StringBuffer before moving on to the next line of every tile.
		 * <p>
		 * This is done to ensure that the visual of a room/region is as it should be!
		 */
		@Override
		public void initString() {
			List<List<String>> listStringList = new ArrayList<List<String>>();
			for(int i = 0; i < space.getXDimension(); i++) {
				List<String> stringList = new ArrayList<String>();
				for(int j = 0; j < space.getYDimension(); j++) {
					stringList.add(space.getByArray(i, j).piece().toString());
				}
				listStringList.add(stringList);
			}
			
//			We use one instance of a piece's toString() to test for number of lines.
			int lineCount = listStringList.get(0).get(0).split("\n").length;
			
			for(int yIndex = 0; yIndex < listStringList.size(); yIndex++) {
				List<String> stringList = listStringList.get(yIndex);
				for(int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
					StringBuffer fullLine = new StringBuffer(); 
					for(int xIndex = 0; xIndex < stringList.size(); xIndex++) {
						fullLine.append(stringList.get(xIndex).split("\n")[lineIndex]);
						
					}
					if(!stringRepresentation.contains(fullLine.toString()))
						stringRepresentation.add(fullLine.toString());
				}
			}
		}
	}
	
	public IRegion newRegion(String name, IWorldTree parent, Constraint constraints, Space space) {
		return new Region(name, parent, constraints, space);
	}
	
	/**
	 * The Tile class is used to fill up the space.
	 * @author guru
	 *
	 */
	public class Tile extends WorldTree implements ITile {
		private Piece piece;
		public Tile(String name, IWorldTree parent, Constraint constraints, Piece tilePiece) {
			super(name, parent, constraints);
			this.piece = tilePiece;
		}
		
		/**
		 * Get the current piece that is stored in the Tile
		 * @return {@code Piece} object stored by this Tile.
		 */
		@Override
		public Piece piece() {
			return piece;
		}
		
		/**
		 * Check whether this Tile has the specified interface
		 * @param it {@code InterfaceType} the interface to check for
		 * @return True if the Tile contains this interface; false otherwise
		 */
		public boolean hasInterface(TileInterfaceType it) {
			return piece.getValidInterfaces().contains(it.toString());
		}
		
		@Override
		public void initialize() {
		}
	}
	
	public ITile newTile(String name, IWorldTree parent, Constraint constraints, Piece tilePiece) {
		return new Tile(name, parent, constraints, tilePiece);
	}
	
	private  class Object extends WorldTree implements IObject {
		private List<ITile> validTiles;
		public Object(String name, IWorldTree parent, Constraint constraints, List<ITile> validTiles) {
			super(name, parent, constraints);
			this.validTiles = validTiles;
		}

		@Override
		public List<ITile> getValidTiles() {
			return validTiles;
		}

		@Override
		public void initialize() {
		}
	}
	
	public IObject newObject(String name, IWorldTree parent, Constraint constraints, List<ITile> validTiles) {
		return new Object(name, parent, constraints, validTiles);
	}
}
