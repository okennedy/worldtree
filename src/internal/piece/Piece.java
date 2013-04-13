package internal.piece;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * The Piece class is used to define every type of Piece that is valid.
 * These pieces occupy Cells.
 * @author guru
 *
 */
public class Piece extends TileInterface {
	private static List<Piece> listOfPieces = new ArrayList<Piece>();
	private String visual;
	
	public Piece(String interfaces) {
		super(interfaces);
		initializeVisual();
	}
	
	private void initializeVisual() {
		visual = "";
		BufferedReader in = null;
		try {
			ArrayList<TileInterfaceType> interfaceList 	= getValidInterfaces();
			in = new BufferedReader(new FileReader(new File("pieces.txt")));
			String line;
			while( (line = in.readLine()) != null) {
				if(line.matches("[A-Z]+")) {
					ArrayList<TileInterfaceType> tokenList		= new ArrayList<TileInterfaceType>();
					char[] tokens = line.toCharArray();
					for(char c : tokens) {
						tokenList.add(TileInterfaceType.convert("" + c));
					}
					if(interfaceList.containsAll(tokenList) && tokenList.containsAll(interfaceList)) {
//						Valid visual
						while( (line = in.readLine()) != null) {
							if(line.matches("(-x)*"))
								break;
							else
								visual += line + "\n";
						}
					}
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Get the list of valid neighbouring pieces with regard to a particular interface of this Piece.
	 * @param it {@code InterfaceType} specifying the interface of this Piece that is to be used
	 * @return {@code ArrayList<Piece>} containing list of valid pieces.
	 */
	public ArrayList<Piece> getValidNeighbourPieces(TileInterfaceType it) {
		ArrayList<Piece> returnList = new ArrayList<Piece>();
		TileInterfaceType complementaryInterface = it.getComplementaryInterface();
		assert(this.hasInterface(it));
		for(Piece p : listOfPieces) {
			if(p.hasInterface(complementaryInterface))
				returnList.add(p);
		}
		return returnList;
	}
	
	/**
	 * Returns a visual representation of this piece.
	 */
	@Override
	public String toString() {
		return visual;
	}

	public String getText() {
		return super.toString();
	}
	
	public static void initialize(String[] list) {
		for(String s : list) {
			listOfPieces.add(new Piece(s));
		}
	}
	
	public static Piece randomPiece() {
		return listOfPieces.get((new Random()).nextInt(listOfPieces.size()));
	}
}
