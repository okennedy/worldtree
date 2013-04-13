package internal.tree.tile.space;

/**
 * Dimension is an abstract class that gives some sense of dimension to any space that extends it.
 * <p>
 * Dimension class maintains a 2-Dimensional matrix that represents the space.
 * @author Guru
 *
 */
public abstract class Dimension {
	protected int xDimension;
	protected int yDimension;
	
	public Dimension(int y, int x) {
		this.xDimension = x;
		this.yDimension = y;
	}
	
	/**
	 * Get the dimension in the horizontal direction
	 * @return {@code Integer} containing the xDimension value
	 */
	public int getXDimension() {
		return xDimension;
	}
	
	/**
	 * Get the dimension in the vertical direction
	 * @return {@code Integer} containing the yDimension value
	 */
	public int getYDimension() {
		return yDimension;
	}
}
