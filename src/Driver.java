import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
		map.initialize();
		map.initialize();
		map.initialize();
//		map.initialize();
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(new File("output/output.txt")));
			out.write(map.toString());
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
