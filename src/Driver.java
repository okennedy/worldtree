import internal.piece.PieceFactory;
import internal.tree.IWorldTree.IMap;
import internal.tree.WorldTreeFactory;

import static internal.Helper.*;

public class Driver {

	public static void main(String[] args) throws Exception {
		PieceFactory.initialize(pieceStrings);
		WorldTreeFactory factory = new WorldTreeFactory();
		IMap map = factory.newMap("TestMap", null);
		map.fullInit();
		write(map);
	}
}
