package ru.mobilespbtransport.model;

import akme.mobile.util.MathUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 20.05.12
 * Time: 19:24
 * To change this template use File | Settings | File Templates.
 */
public class Coordinate {
	private final double lat;
	private final double lon;
	private final int type;

	public static final int WGS84 = 0; //lat, lon (gps, google)
	public static final int EPSG = 1; //bbox (transport portal)

	private final static double ORIGIN_SHIFT = Math.PI * 6378137;

	/**
	 * Creates default WGS84 (gps) lat/long coordinate
	 * @param lat
	 * @param lon
	 */
	public Coordinate(double lat, double lon) {
		this(lat, lon, WGS84);
	}

	public Coordinate(double lat, double lon, int type) {
		this.lat = lat;
		this.lon = lon;
		this.type = type;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	/**
	 * Converts given lat/lon in WGS84 Datum to XY in Spherical Mercator EPSG:900913
	 */
	public Coordinate toEPSG() {
		if (type == EPSG) {
			return this;
		}
		double mx = lon * ORIGIN_SHIFT / 180.0;
		double my = MathUtil.log(Math.tan((90 + lat) * Math.PI / 360.0)) / (Math.PI / 180.0);

		my = my * ORIGIN_SHIFT / 180.0;
		return new Coordinate(mx, my, EPSG);
	}

	/*
	* Converts XY point from Spherical Mercator EPSG:900913 to lat/lon in WGS84 Datum
	*/
	public Coordinate toWGS84() {
		if (type == WGS84) {
			return this;
		}

		double newlon = (lon / ORIGIN_SHIFT) * 180.0;
		double newlat = (lat / ORIGIN_SHIFT) * 180.0;

		newlat = 180 / Math.PI * (2 * MathUtil.atan(MathUtil.exp(newlat * Math.PI / 180.0)) - Math.PI / 2.0);

		return new Coordinate(newlat, newlon, WGS84);
	}

	public String toString() {
		return "Coordinate{" +
				lat +
				", " + lon +
				" (" + (type == WGS84 ? "WGS84" : "EPSG") +
				")}";
	}
}
