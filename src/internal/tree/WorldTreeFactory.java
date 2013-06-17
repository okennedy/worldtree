package internal.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import internal.containers.Constraint;
import internal.piece.IPiece;
import internal.piece.PieceFactory;
import internal.piece.TileInterfaceType;
import internal.space.Space;
import internal.space.Coordinates;
import internal.tree.IWorldTree.IMap;
import internal.tree.IWorldTree.IRoom;
import internal.tree.IWorldTree.IRegion;
import internal.tree.IWorldTree.ITile;
import internal.space.Space.Direction;

import static test.ui.UIDebugEngine.write;

public class WorldTreeFactory {

	private  class Map extends WorldTree implements IMap {
		public Map(String name, IWorldTree parent, Collection<Constraint> constraints) {
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

		@Override
		public void fullInit() {
			this.initialize();
			List<IWorldTree> nodes = new ArrayList<IWorldTree>();
			nodes.addAll(children);
			
			IWorldTree node = null;
			try {
				while(nodes.size() > 0) {
					node = nodes.get(0);
					node.initialize();
					nodes.addAll(node.children());
					nodes.remove(node);
				}
			} catch (Exception e) {
				System.out.print("");
			}
		}
		
		@Override
		public void move(test.ui.Direction direction) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public IWorldTree neighbour(Direction direction) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public IMap newMap(String name, IWorldTree parent, Collection<Constraint> constraints) {
		return new Map(name, parent, constraints);
	}
	
	private  class Room extends WorldTree implements IRoom {
		public Room(String name, IWorldTree parent, Collection<Constraint> constraints) {
			super(name, parent, constraints);
		}

//		The Room must decide the location of the tiles (I think..)
		@Override
		public void initialize() {
			children = new ArrayList<IWorldTree>();
			String regionNames[] = {
					"Dungeon",
					"Normal",
					"Altar",
			};
			for(int i = 0; i < 2; i++) {
				int nextInt = (new Random()).nextInt(regionNames.length);
				children.add(newRegion(regionNames[nextInt], this, null, new Space(6, 6)));
			}
		}

		@Override
		public void move(test.ui.Direction direction) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public IWorldTree neighbour(Direction direction) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public IRoom newRoom(String name, IWorldTree parent, Collection<Constraint> constraints) {
		return new Room(name, parent, constraints);
	}
	
	private  class Region extends WorldTree implements IRegion {
		private Space space;
		public Region(String name, IWorldTree parent, Collection<Constraint> constraints, Space space) {
			super(name, parent, constraints);
			this.space = space;
//			First tile
			ITile tile = initTile(new Coordinates(true, 0, 0));
			space.setByCoord(new Coordinates(true, 0, 0), tile);
			space.setCurrentCoordinates(new Coordinates(true, 0, 0));
			initNeighbours();
		}

		@Override
		public void initialize() {
//			TODO: Ensure all pieces are traverse-able.
//			initRegion();
			initString();
		}
		
		/**
		 * This method can be used to instantly initialize this Region and all its children tiles.
		 */
		private void initRegion() {
			for(int i = 0; i < space.getYDimension(); i++) {
				for(int j = 0; j < space.getXDimension(); j++) {
					Coordinates coords = new Coordinates(true, i, j);
					ITile tile = initTile(coords);
					space.setByCoord(coords, tile);
				}
			}
		}
		
		private ITile initTile(Coordinates coordinates) {
			int x, y;
			Coordinates coords = coordinates;
			if(!coordinates.cartesian()) {
				coords = space.arrayToCoord(coordinates);
			}
			
			java.util.Map<String, String> interfaceMap = space.getValidInterfaces(coords);
			String coordString = "(" + coords.x + "," + coords.y + ")";
			ITile tile = newTile("tile" + coordString, this, null, PieceFactory.randomPiece(interfaceMap));
//			Collection<IWorldTree> children = null;		//TODO: Add a way to initialize Objects into Tiles
			return tile;
		}
		
		private void initNeighbours() {
			Coordinates coords = space.currentCoordinates();
			initNeighbours(coords);
		}

		private void initNeighbours(Coordinates coordinates) {
			
			List<Direction> directions = new ArrayList<Direction>(Space.listDirections());
			
			while(directions.size() > 0) {
				Coordinates coords = new Coordinates(coordinates);
				
				int randomIndex = 0 + (int) (Math.random() * (directions.size() - 0) + 0);
				Direction direction = directions.get(randomIndex);
				switch(direction) {
				case E:
					coords.x++;
					break;
				case N:
					coords.y++;
					break;
				case NE:
					coords.x++;
					coords.y++;
					break;
				case NW:
					coords.x--;
					coords.y++;
					break;
				case S:
					coords.y--;
					break;
				case SE:
					coords.x++;
					coords.y--;
					break;
				case SW:
					coords.x--;
					coords.y--;
					break;
				case W:
					coords.x--;
					break;
				default:
					throw new IllegalStateException("Invalid direction? This should have never occured!\n");
				}
				
//				Convert to Cartesian coordinates for validate
				if(space.validate(coords) && space.getByCoord(coords) == null) {	
					ITile tile = initTile(coords);
					space.setByCoord(coords, tile);
				}
				directions.remove(direction);
			}
		}

		/**
		 * This is some really ugly code where multi-line visuals of each tile are split into single lines and
		 * each line of each tile is appended together to the StringBuffer 
		 * before moving on to the next line of every tile.
		 * <br>
		 * This is done to ensure that the visual of a room/region is as it should be!
		 */
		@Override
		public void initString() {
			stringRepresentation = space.getStringRepresentation();
			prepareToString();
		}
		
		@Override
		public Collection<IWorldTree> children() {
			if(space.collection().size() > 0)
				return space.collection();
			else
				return null;
		}
		
		@Override
		public void move(test.ui.Direction direction) {		//FIXME
			Coordinates coordinates = space.currentCoordinates();
			switch(direction) {
			case UP:
				coordinates.y++;
				break;
			case DOWN:
				coordinates.y--;
				break;
			case LEFT:
				coordinates.x--;
				break;
			case RIGHT:
				coordinates.x++;
				break;
			default:
				throw new IllegalStateException("Only directions should be passed to move()");
			}
			
			if(space.validate(coordinates)) {
				space.setCurrentCoordinates(coordinates);
				initNeighbours();
			}
		}

		@Override
		public IWorldTree neighbour(Direction direction) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public IRegion newRegion(String name, IWorldTree parent, Collection<Constraint> constraints, Space space) {
		return new Region(name, parent, constraints, space);
	}
	
	/**
	 * The Tile class is used to fill up the space.
	 * @author guru
	 *
	 */
	public class Tile extends WorldTree implements ITile {
		private IPiece piece;
		public Tile(String name, IWorldTree parent, Collection<Constraint> constraints, IPiece tilePiece) {
			super(name, parent, constraints);
			this.piece = tilePiece;
			initialize();
		}
		
		/**
		 * Get the current piece that is stored in the Tile
		 * @return {@code Piece} object stored by this Tile.
		 */
		@Override
		public IPiece piece() {
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
			children = new ArrayList<IWorldTree>();
		}
		
		@Override
		public String toString() {
			return piece.toString();
		}
		
		protected void addChild(IWorldTree child) {
			this.children.add(child);
			
			StringBuffer visual = new StringBuffer();
			for(String line : this.stringRepresentation)
				visual.append(line);
			
			if(visual.toString().contains("  "))
				visual = new StringBuffer(visual.toString().replace("  ", child.toString()));
			else
				System.err.println("Error: " + this.name() + " is unable to accomodate more children visually\n" +
						"\tThe object still contains these children");
		}

		@Override
		public void move(test.ui.Direction direction) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public IWorldTree neighbour(Direction direction) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public ITile newTile(String name, IWorldTree parent, Collection<Constraint> constraints, IPiece tilePiece) {
		return new Tile(name, parent, constraints, tilePiece);
	}
}
