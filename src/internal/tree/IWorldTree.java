package internal.tree;

import java.util.Collection;
import java.util.List;

import internal.containers.Constraint;
import internal.piece.IPiece;
import internal.piece.TileInterfaceType;
import internal.space.Space;

public interface IWorldTree {
	
	/**
	 * Get the name of this WorldTree instance
	 * @return {@code String} representing the name of this WorldTree instance
	 */
	public String name();
	
	/**
	 * Get the absolute name of this object
	 * @return {@code String} representing the absolute name of this WorldTree instance
	 */
	public String absoluteName();
	
	/**
	 * Get the parent of this WorldTree instance
	 * @return {@code IWroldTree} interface to the parent of this WorldTree instance
	 */
	public IWorldTree parent();
	
	/**
	 * Get the set of children of this WorldTree instance
	 * @return {@code List<IWorldTree>} containing the children of this WorldTree instance
	 */
	public Collection<IWorldTree> children();
	
	/**
	 * This method is used to initialize a particular level in the World hierarchy.
	 * Thus, each level has its own 'unique' implementation of this method.
	 */
	public void initialize();
	
	/**
	 * Obtain constraints on this WorldTree instance
	 * @return {@code Collection<Constriant>}  
	 */
	public Collection<Constraint> constraints();
	
	/**
	 * Obtain root of this WorldTree
	 * @return {@code IWorldTree} representing the root of this WorldTree
	 */
	public IWorldTree root();
	
	/**
	 * Obtain a neighbour of this WorldTree instance
	 * @param direction The direction in which the neighbour is to be located
	 * @return {@code IWorldTree} referring the neighbour in the specified direction
	 */
	public IWorldTree neighbour(Space.Direction direction);
	
	/**
	 * Method strictly reserved for debugging purposes
	 * @param direction {@code test.ui.Direction} in which to move
	 */
	public void move(test.ui.Direction direction);
	
	/**
	 * Get set of strings used to represent this WorldTree instance.
	 * @return {@code List<String>} containing the strings used to visually represent this WorldTree instance.
	 */
	List<String> getStringRepresentation();
	
	public String toString();
	
	public interface IMap extends IWorldTree {
		/**
		 * Fully initialize this Map
		 */
		public void fullInit();
	}
	
	public interface IRoom extends IWorldTree {
		
	}
	
	public interface IRegion extends IWorldTree {
		
	}
	
	public interface ITile extends IWorldTree {
		
		/**
		 * Obtain reference to the piece located in this Tile
		 * @return {@code IPiece} object referencing this Tile's piece
		 */
		public IPiece piece();
		
		/**
		 * Check whether this Tile has the specified interface
		 * @param it {@code TileInterfaceType} the interface to check for
		 * @return {@code true} if the ITile contains this interface <br>
		 * {@code false} otherwise
		 */
		public boolean hasInterface(TileInterfaceType it);
	}
}
