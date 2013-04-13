package internal.piece;

import java.util.ArrayList;


/**
 * The Interface class is an abstract class that gives some notion of common ground between two or more cells.
 * @author guru
 *
 */
public abstract class TileInterface {
	private String interfaces;
	private String binaryString;
	private int integerFormat;
	
	public TileInterface(String interfaces) {
		if (interfaces.matches("[a-zA-Z]+")) {
//			We're converting a string of UDLR to binary
			this.interfaces = interfaces.toUpperCase();
			this.binaryString 	= TileInterfaceType.toBinaryString(interfaces);
		}
		else {
			this.binaryString 	= interfaces;
			this.interfaces		= getText();
			
		}
		this.integerFormat 	= TileInterfaceType.toInteger(this.binaryString);
	}
	
	private String getText() {
		String result = "";
		if(validUp())
			result += TileInterfaceType.U.toString();
		if(validRight())
			result += TileInterfaceType.R.toString();
		if(validDown())
			result += TileInterfaceType.D.toString();
		if(validLeft())
			result += TileInterfaceType.L.toString();
		
		return result;
	}

	/**
	 * @return True if the current interface has open interface above; false otherwise
	 */
	public boolean validUp() {
		if((integerFormat & (1 << 3)) > 0)
			return true;
		else
			return false;
	}
	
	/**
	 * @return True if the current interface has open interface to the right; false otherwise
	 */
	public boolean validRight() {
		if( (integerFormat & (1 << 2))  > 0)
			return true;
		else
			return false;
	}
	
	/**
	 * @return True if the current interface has open interface below; false otherwise
	 */
	public boolean validDown() {
		if( (integerFormat & (1 << 1)) > 0)
			return true;
		else
			return false;
	}
	
	/**
	 * @return True if the current interface has open interface to the left; false otherwise
	 */
	public boolean validLeft() {
		if( (integerFormat & 1) > 0)
			return true;
		else
			return false;
	}
	
	/**
	 * Check if this interface has a particular InterfaceType open
	 * @param it {@code InterfaceType} containing the InterfaceType to check for
	 * @return True if this interface has the specified InterfaceType; false otherwise
	 */
	public boolean hasInterface(TileInterfaceType it) {
		return (integerFormat & it.getType()) > 0;
	}
	
	/**
	 * Returns a @{code List} of {@code InterfaceType} that are valid for this interface
	 * @return {@code ArrayList<InterfaceType>} containing list of all valid InterfaceTypes
	 */
	public ArrayList<TileInterfaceType> getValidInterfaces() {
		ArrayList<TileInterfaceType> list = new ArrayList<TileInterfaceType>();
		if(validUp())
			list.add(TileInterfaceType.U);
		if(validDown())
			list.add(TileInterfaceType.D);
		if(validLeft())
			list.add(TileInterfaceType.L);
		if(validRight())
			list.add(TileInterfaceType.R);
		
		return list;
	}
	
	/**
	 * Return a String representation of the interfaces that are available.
	 */
	@Override
	public String toString() {
		return interfaces;
	}
}
