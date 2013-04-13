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
 * PieceFactory is a singleton factory class that is responsible for returning pieces.
 * @author Guru
 *
 */
public class PieceFactory {
	private static List<Piece> listOfPieces = new ArrayList<Piece>();
	private static PieceFactory instance = null;
	
	protected PieceFactory() {
	}
	
	public PieceFactory(String[] pieceStrings) {
		instance = new PieceFactory();
		instance.initialize(pieceStrings);
	}

	private void initialize(String[] list) {
		for(String s : list) {
			Piece p = new Piece(s);
			p.initializeVisual();
			listOfPieces.add(p);
		}
	}


	private Piece getNewPiece(String interfaces) {
		for(Piece p : listOfPieces) {
			if(p.getBinaryInterfaces().equals(interfaces))
				return p;
			char [] array = interfaces.toUpperCase().toCharArray();
			Arrays.sort(array);
			interfaces = array.toString();
			if(interfaces.equals(p.getText()))
				return p;
		}
		throw new IllegalArgumentException("Invalid interfaces :" + interfaces);
	}
	
	/**
	 * Returns the Piece object corresponding to the specified interfaces.
	 * @param interfaces {@code String} representing the interfaces of the Piece
	 * @return {@code Piece} representing the interfaces.
	 * @throws IllegalArgumentException if the interfaces are invalid or if there is no Piece 
	 * corresponding to the specified interfaces.
	 */
	public static Piece newPiece(String interfaces) {
		return instance.getNewPiece(interfaces);
	}

	public static IPiece randomPiece() {
		return listOfPieces.get((new Random()).nextInt(listOfPieces.size()));
	}

	/**
	 * The Piece class is used to define every type of Piece that is valid.
	 * These pieces occupy Cells.
	 * @author guru
	 *
	 */
	private class Piece extends TileInterface implements IPiece {
		
		private String visual;
		
		private Piece(String interfaces) {
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
//							Valid visual
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
		@Override
		public ArrayList<IPiece> getValidNeighbourPieces(TileInterfaceType it) {
			ArrayList<IPiece> returnList = new ArrayList<IPiece>();
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

		@Override
		public String getText() {
			return super.toString();
		}
	}
}
