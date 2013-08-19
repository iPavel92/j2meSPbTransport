package ru.mobilespbtransport.view.mapview;

/**
 * Created with IntelliJ IDEA.
 * User: Павел
 * Date: 18.08.13
 * Time: 13:41
 * To change this template use File | Settings | File Templates.
 */
public class BoundingBox {
	private final double north;
	private final double south;
	private final double east;
	private final double west;

	public BoundingBox(double west, double east, double south, double north) {
		this.west = west;
		this.east = east;
		this.south = south;
		this.north = north;
	}

	public double getNorth() {
		return north;
	}

	public double getSouth() {
		return south;
	}

	public double getEast() {
		return east;
	}

	public double getWest() {
		return west;
	}

	public String toString() {
		return "BoundingBox{" +
				"north=" + north +
				", south=" + south +
				", east=" + east +
				", west=" + west +
				'}';
	}
}
