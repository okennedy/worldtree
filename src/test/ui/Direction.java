package test.ui;

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
	
	protected static Direction getDirection(String string) {
		for(Direction d : values()) {
			if(d.toString().equalsIgnoreCase(string) || d.toString().substring(0, 0).equalsIgnoreCase(string))
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
