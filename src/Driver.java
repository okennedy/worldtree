import internal.piece.PieceFactory;
import internal.tree.IWorldTree.IMap;
import internal.tree.WorldTreeFactory;
import test.TimeKeeper;

import static internal.Helper.*;

public class Driver {

	public static void main(String[] args) throws Exception {
    PieceFactory.initialize(pieceStrings);
    IMap map;
    WorldTreeFactory factory = null;
		TimeKeeper timeKeeper = new TimeKeeper();
				
		timeKeeper.start();
		factory = new WorldTreeFactory("init.properties", "world.definitions");
		map = factory.newMap("InitTestMap", null);
		map.initRooms();
		map.initRegions();
		map.initTiles();
		
		timeKeeper.stop();
		System.out.println("Time taken to create skeleton          :" + timeKeeper.toString());
		
		timeKeeper.start();
		map.fill();
		timeKeeper.stop();
		System.out.println("Time taken to fill entire map          :" + timeKeeper.toString());
		
		
		timeKeeper.start();
		map.materializeConstraints();
		timeKeeper.stop();
		System.out.println("Time taken to materialize constraints  :" + timeKeeper.toString());
		
		timeKeeper.start();
		write(map);
		timeKeeper.stop();
		System.out.println("Time taken to write map                :" + timeKeeper.toString());
	}
}
