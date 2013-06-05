import test.ui.UIDebugEngine;

import internal.piece.PieceFactory;
import internal.tree.IWorldTree.IMap;
import internal.tree.WorldTreeFactory;


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
		new PieceFactory(pieceStrings);
		WorldTreeFactory factory = new WorldTreeFactory();
		IMap map = factory.newMap("TestMap", null, null);
		UIDebugEngine.init(map);
	}
}
