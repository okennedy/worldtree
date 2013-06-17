package internal.space;

public class Coordinates {
	protected boolean cartesian;
	public int x, y;
	
	public Coordinates(boolean cartesian, int x, int y) {
		this.cartesian	= cartesian;
		this.x			= x;
		this.y			= y;
	}
	
	public Coordinates(Coordinates coordinates) {
		this.x 			= coordinates.x;
		this.y			= coordinates.y;
		this.cartesian	= coordinates.cartesian;
	}
	
	public boolean cartesian() {
		return cartesian;
	}
	
	public int x() {
		return x;
	}
	
	public int y() {
		return y;
	}
	
	@Override
	public String toString() {
		if(cartesian)
			return "C(" + x + "," + y + ")";
		else
			return "A(" + y + "," + x + ")";
	}
}