package internal.piece;

/**
 * The enum InterfaceType is used to store and provide a mechanism to distinguish interfaces that are both present
 * and absent in the Interface class.
 * Interfaces are broken into four types, each representing one direction.
 * @author guru
 *
 */
public enum TileInterfaceType {
	U(1 << 3),
	R(1 << 2),
	D(1 << 1),
	L(1 << 0)
	
	;
	/**
	 * type is used to store the integer representation of the enum.
	 */
	private int type;
	private TileInterfaceType(int type) {
		this.type = type;
	}

	/**
	 * Converts a given textual representation of the interface configuration to a binary representation
	 * @param interfaces {@code String} containing the textual representation of the interface configuration
	 * @return {@code String} containing the binary representation of the given interface configuration
	 */
	public static String toBinaryString(String interfaces) {
		int binary = 0;
		testString(interfaces);
		
		for(int i = 0; i < interfaces.length(); i++) {
			String ch = Character.toString(interfaces.charAt(i));
			for(TileInterfaceType it : values()) {
				if(ch.equals(it.toString())) {
					binary |= it.type;
					break;
				}
			}
		}			
		return getIntAsBinaryString(binary);
	}
	
	/**
	 * Helper method that converts a given integer to its binary representation
	 * @param binary {@code Integer} containing the number to be converted to binary format
	 * @return {@code String} containing the binary representation of the given integer
	 */
	public static String getIntAsBinaryString(int binary) {
		String binaryString = "";
		
		for(int i = 3; i >= 0; i--) {
			if( (binary & (1 << i)) > 0)
				binaryString = binaryString.concat("1");
			else
				binaryString = binaryString.concat("0");
		}
		return binaryString;
	}

	/**
	 * Tests a given string to check whether it conforms to the type standards of this class.
	 * @param interfaces {@code String} containing the interface configuration to test for correctness
	 * @throws IllegalArgumentException if the String contains invalid characters.
	 */
	public static void testString(String interfaces) {
		String errorString = "";
		for(int i = 0; i < interfaces.length(); i++) {
			String ch = Character.toString(interfaces.charAt(i));
			boolean matchFlag = false; 
			for(TileInterfaceType it : values()) {
				if(ch.equals(it.toString())) {
					matchFlag = true;
					break;
				}
			}
			if(!matchFlag)
				errorString += ch;
		}
		if(errorString.length() > 0)
			throw new IllegalArgumentException("Invalid characters found :" + errorString);
	}

	/**
	 * Unused helper method that is used to convert a given String to its corresponding InterfaceType
	 * @param c {@code String} containing the InterfaceType
	 * @return {@code InterfaceType} that maps to the given String; null otherwise
	 */
	public static TileInterfaceType convert(String str) {
		for(TileInterfaceType it : values()) {
			if(str.equals(it.toString()))
				return it;
		}
		return null;
	}
	
	/**
	 * Helper method that converts a binary string representation to integer
	 * @param binaryString {@code String} containing the binary representation of an integer
	 * @return {@code Integer} equivalent to the binary representation
	 */
	public static int toInteger(String binaryString) {
		int binary = 0;
		
		for(int i = 0; i < binaryString.length(); i++) {
			if(binaryString.charAt(i) == '1')
				binary |= 1 << binaryString.length() - i - 1;
		}
		
		return binary;
	}
	
	public int getType() {
		return type;
	}
	
	/**
	 * Returns the complementary interface to this interface.
	 * @return {@code InterfaceType} that is complementary to this interface
	 */
	public TileInterfaceType getComplementaryInterface() {
		switch(this) {
		case U :
			return D;
		case D :
			return U;
		case L :
			return R;
		case R :
			return L;
		default :
			throw new IllegalArgumentException("Invalid InterfaceType " + toString());
		}
	}
}