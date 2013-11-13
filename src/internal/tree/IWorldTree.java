package internal.tree;

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import development.com.collection.range.Range;
import development.com.collection.range.RangeSet;
import internal.parser.containers.Constraint;
import internal.parser.containers.Datum;
import internal.parser.containers.property.PropertyDef;
import internal.parser.containers.property.PropertyDef.RandomSpec;
import internal.piece.IPiece;
import internal.piece.TileInterfaceType;
import internal.space.Space;

public interface IWorldTree {
	
	/**
	 * Get the name of this {@code IWorldTree} instance
	 * @return {@code String} representing the name of this {@code IWorldTree} instance
	 */
	public String name();
	
	/**
	 * Get the absolute name of this object
	 * @return {@code String} representing the absolute name of this {@code IWorldTree} instance
	 */
	public String absoluteName();
	
	/**
	 * Get the parent of this {@code IWorldTree} instance
	 * @return {@code IWroldTree} interface to the parent of this {@code IWorldTree} instance
	 */
	public IWorldTree parent();
	
	/**
	 * Get the set of children of this {@code IWorldTree} instance
	 * @return {@code List<IWorldTree>} containing the children of this {@code IWorldTree} instance
	 */
	public Collection<IWorldTree> children();
	
	/**
	 * This method is used to initialize a particular level in the World hierarchy.
	 * Thus, each level has its own 'unique' implementation of this method.
	 */
	public void initialize();
	
	/**
	 * Obtain constraints on this {@code IWorldTree} instance
	 * @return {@code Collection<Constriant>}  
	 */
	public Collection<Constraint> constraints();
	
	/**
	 * Push down the {@code Constraints} defined at this level to its children
	 */
	public void pushDownConstraints();
	
	/**
	 * Obtain the definitions of this {@code IWorldTree} instance
	 * @return {@code Collection<PropertyDef>}
	 */
	public Collection<PropertyDef> definitions();
	
	/**
	 * Set constraints to provided object for this {@code IWorldTree} instance
	 * @param constraints {@code Collection<Constraint>} set of new constraints
	 */
//	FIXME: Added this to solve NPE on constraints()
	public void setConstraints(Collection<Constraint> constraints);
	
	public void addConstraint(Constraint constraint);
	
	/**
	 * Remove the specified constraint
	 * @param constraint {@code Constraint} to be removed
	 * @return <b>true</b> if the constraint was removed successfully <br>
	 * <b>false</b> otherwise
	 */
	public boolean removeConstraint(Constraint constraint);
	
	/**
	 * Add a new property to this {@code IWorldTree}
	 * @param name {@code String} containing the name of this property
	 * @param value {@code Datum} containing the value of this property
	 */
	public void addProperty(String name, Datum value);
	
	/**
	 * Obtain the properties of this {@code IWorldTree} instance
	 * @return {@code Map<String, Datum>} containing the properties and their values of this {@code IWorldTree} instance
	 */
	public Map<String, Datum> properties();
	
	/**
	 * Obtain root of this {@code IWorldTree} instance
	 * @return {@code IWorldTree}
	 */
	public IWorldTree root();
	
	/**
	 * Obtain a neighbour of this {@code IWorldTree} instance
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
	 * Pre-processing step to compute bounds.
	 * This should occur before the first call to getBounds.
	 */
	public void processBounds();
	
	/**
	 * Return bounds of the specified {@code PropertyDef} on this {@code IWorldTree} instance
	 * @param parentDefinition {@code PropertyDef} definition whose bounds are required
	 * @return {@code RangeSet} containing the minimum and maximum values for each definition 
	 * that matches the parent definition
	 */
	public RangeSet getBounds(PropertyDef parentDefinition);
	
	/**
	 * Get set of strings used to represent this {@code IWorldTree} instance.
	 * @return {@code List<String>} containing the strings used to visually represent this {@code IWorldTree} instance.
	 */
	List<String> getStringRepresentation();
	
	public String toString();
	
	public interface IMap extends IWorldTree {
		/**
		 * Fully initialize this Map
		 */
		public void fullInit();
		
		/**
		 * Initialize the Rooms
		 */
		public void initRooms();
		
		/**
		 * Initialize the Regions
		 */
		public void initRegions();
		
		public void initTiles();
		
		public void materializeConstraints();

		public void fill();
	}
	
	public interface IRoom extends IWorldTree {
		
	}
	
	public interface IRegion extends IWorldTree {
		
		/**
		 * Set the start and end tiles for this {@code IRegion}
		 */
		public void setStartEndTiles();
	}
	
	public interface ITile extends IWorldTree {
		
		/**
		 * Add an artifact to the visual of this {@code ITile}
		 * @param artifact {@code String} containing the artifact to add
		 */
		public void addArtifact(String artifact);
		
		/**
		 * Remove an artifact from the visual of this {@code ITile}
		 * @param artifact {@code String} containing the artifact to remove
		 */
		public void removeArtifact(String artifact);
		
		/**
		 * Obtain a collection of all artifacts that are part of this {@code ITile}'s visual
		 * @return {@code Collection<String>} containing all artifacts
		 */
		public Collection<String> artifacts();
		
		/**
		 * Obtain reference to the piece located in this Tile
		 * @return {@code IPiece} object referencing this Tile's piece
		 */
		public IPiece piece();
		
		/**
		 * Set <b>piece</b> as the {@code IPiece} for this {@code ITile}
		 * @param piece {@code IPiece} referencing the piece to set
		 */
		public void setPiece(IPiece piece);
		
		/**
		 * Check whether this Tile has the specified interface
		 * @param it {@code TileInterfaceType} the interface to check for
		 * @return {@code true} if the ITile contains this interface <br>
		 * {@code false} otherwise
		 */
		public boolean hasInterface(TileInterfaceType it);
	}
}
