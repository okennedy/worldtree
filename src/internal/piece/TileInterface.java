package internal.piece;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * The Interface class is an abstract class that gives some notion of common ground between two or more cells.
 * @author guru
 *
 */
public abstract class TileInterface implements ITileInterface, Serializable {
	private static final long serialVersionUID = 7435579309730202286L;
	
	private String interfaces;
	private String binaryString;
	private int integerFormat;
	
	public TileInterface(String interfaces) {
		if(interfaces.equals("")) {
			this.interfaces 	= "";
			this.binaryString 	= "0000";
			this.integerFormat 	= 0;
			return;
		}
		else if (interfaces.matches("[a-zA-Z]+")) {
//			We're converting a string of UDLR to binary
			char[] array 		= interfaces.toUpperCase().toCharArray();
			Arrays.sort(array);
			this.interfaces 	= new String(array);
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

	public boolean validUp() {
		if((integerFormat & (1 << 3)) > 0)
			return true;
		else
			return false;
	}
	
	public boolean validRight() {
		if( (integerFormat & (1 << 2))  > 0)
			return true;
		else
			return false;
	}
	
	public boolean validDown() {
		if( (integerFormat & (1 << 1)) > 0)
			return true;
		else
			return false;
	}
	
	public boolean validLeft() {
		if( (integerFormat & 1) > 0)
			return true;
		else
			return false;
	}
	
	public boolean hasInterface(TileInterfaceType it) {
		return (integerFormat & it.getType()) > 0;
	}
	
	public List<TileInterfaceType> getValidInterfaces() {
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
	
	@Override
	public String toString() {
		return interfaces;
	}
	
	public String getBinaryInterfaces() {
		return binaryString;
	}
}
