package internal.tree;

import java.util.List;

import internal.piece.IPiece;

public interface IWorldTree {
	public String name();
	public IWorldTree parent();
	public List<IWorldTree> children();
	public void initialize();
	List<String> getStringRepresentation();
	public String toString();
	
	public interface IMap extends IWorldTree {
		
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
