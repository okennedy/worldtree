package internal.tree;

import internal.piece.Piece;

import java.util.List;

public interface IWorldTree {
	public String name();
	public IWorldTree parent();
	public List<IWorldTree> children();
	public void initialize();
	List<String> getStringRepresentation();
	void initString();
	
	public interface IMap extends IWorldTree {
		
	}
	
	public interface IRoom extends IWorldTree {
		
	}
	
	public interface IRegion extends IWorldTree {
		
	}
	
	public interface ITile extends IWorldTree {
		public Piece piece();
	}
	
	public interface IObject extends IWorldTree {
		public List<ITile> getValidTiles();
	}
}
