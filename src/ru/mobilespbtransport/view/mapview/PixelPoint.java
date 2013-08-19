package ru.mobilespbtransport.view.mapview;

/**
 * Created with IntelliJ IDEA.
 * User: Павел
 * Date: 18.08.13
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
public class PixelPoint {
	private final int x;
	private final int y;

	public PixelPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String toString() {
		return "{" +
				"x=" + x +
				", y=" + y +
				'}';
	}
}
