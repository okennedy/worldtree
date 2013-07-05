import internal.piece.PieceFactory;
import internal.tree.IWorldTree.IMap;
import internal.tree.WorldTreeFactory;
import static internal.Helper.write;

public class Driver {
	public static String[] pieceStrings = {
		"LR",
		"UD",
		"UL",
		"L",
		"U",
		"D",
		"R",
		"UDLR",
		"UDL",
		"UDR",
		"ULR",
		"DLR",
		"UR",
		"DR",
		"DL",
		""
	};
	public static void main(String[] args) throws Exception {
		PieceFactory.initialize(pieceStrings);
		WorldTreeFactory factory = new WorldTreeFactory();
		IMap map = factory.newMap("TestMap", null, null);
		map.fullInit();
		write(map);
	}
}
