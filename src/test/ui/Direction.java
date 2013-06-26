package test.ui;

/**
 * Enum enumerating the various UI debugging directions supported
 * @author guru
 *
 */
public enum Direction {
	UP("UP"),
	DOWN("DOWN"),
	LEFT("LEFT"),
	RIGHT("RIGHT"),
	;
	
	private final String direction;
	
	private Direction(String direction) {
		this.direction = direction;
	}
	
	/**
	 * Obtain the direction corresponding to the specified parameter
	 * @param string {@code String} specifying the {@code Direction}
	 * @return {@code Direction} corresponding to the parameter <b>string</b> <br>
	 * <b>null</b> otherwise
	 */
	protected static Direction getDirection(String string) {
		for(Direction d : values()) {
			if(d.toString().equalsIgnoreCase(string) || d.toString().substring(0, 1).equalsIgnoreCase(string))
				return d;
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return direction;
	}
	
	public String text() {
		return direction;
	}
}
