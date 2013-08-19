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
	
	private int interfaces;
	
	public TileInterface(String interfaces) {
		if(interfaces.equals("")) {
			this.interfaces 	= 0;
			return;
		}
		else if (interfaces.matches("[a-zA-Z]+")) {
//			We're converting a string of UDLR to integer
			char[] array 		= interfaces.toUpperCase().toCharArray();
			Arrays.sort(array);
			String binaryString	= TileInterfaceType.toBinaryString(String.valueOf(array));
			this.interfaces 	= TileInterfaceType.toInteger(binaryString);
		}
		else {
			try {
				this.interfaces = Integer.parseInt(interfaces);
			} catch(NumberFormatException e) {
				throw new IllegalArgumentException("Invalid interfaces!");
			}
		}
	}
	
	protected String getText() {
		StringBuffer result = new StringBuffer();
		if(validDown())
			result.append(TileInterfaceType.D.toString());
		if(validLeft())
			result.append(TileInterfaceType.L.toString());
		if(validRight())
			result.append(TileInterfaceType.R.toString());
		if(validUp())
			result.append(TileInterfaceType.U.toString());
		
		return result.toString();
	}

	public boolean validUp() {
		if((interfaces & (1 << 3)) > 0)
			return true;
		else
			return false;
	}
	
	public boolean validRight() {
		if( (interfaces & (1 << 2))  > 0)
			return true;
		else
			return false;
	}
	
	public boolean validDown() {
		if( (interfaces & (1 << 1)) > 0)
			return true;
		else
			return false;
	}
	
	public boolean validLeft() {
		if( (interfaces & 1) > 0)
			return true;
		else
			return false;
	}
	
	public boolean hasInterface(TileInterfaceType it) {
		return (interfaces & it.getType()) > 0;
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
		return getText();
	}
	
	public String getBinaryInterfaces() {
		StringBuffer result = new StringBuffer();
		int value = this.interfaces;
		
		while(value > 0) {
			int binary 	= value % 2;
			value 		= value / 2;
			result.append("" + binary);
		}
		return result.reverse().toString();
	}
}
