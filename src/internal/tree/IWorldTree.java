package internal.tree;

import java.util.Collection;
import java.util.List;

import internal.containers.Constraint;
import internal.piece.IPiece;

public interface IWorldTree {
	
	/**
	 * Get the name of this WorldTree instance.
	 * @return {@code String} representing the name of this WorldTree instance.
	 */
	public String name();
	
	/**
	 * Get the parent of this WorldTree instance.
	 * @return {@code IWroldTree} interface to the parent of this WorldTree instance.
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
	 * Method strictly reserved for debugging purposes
	 * @param direction {@code test.ui.Direction} in which to move
	 */
	public void move(test.ui.Direction direction);
	
	List<String> getStringRepresentation();
	
	public String toString();
	
	public interface IMap extends IWorldTree {
		public void fullInit();
	}
	
	public interface IRoom extends IWorldTree {
		
	}
	
	public interface IRegion extends IWorldTree {
		
	}
	
	public interface ITile extends IWorldTree {
		public IPiece piece();
	}
	
	public interface IObject extends IWorldTree {
		public List<ITile> getValidTiles();
	}
}
