package ru.mobilespbtransport.model;

import akme.mobile.util.MathUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 04.05.12
 * Time: 19:03
 * Parts of code from here
 * https://github.com/ofelbaum/spb-transport-app
 * Thanks!!!!
 */
public class GeoConverter {
	/**
	 * Converts given lat/lon in WGS84 Datum to XY in Spherical Mercator EPSG:900913
	 */
	public static Place fromLatLonToMeters(Place src) {
		double originShift = Math.PI * 6378137; //6378137
		double mx = src.getLon() * originShift / 180.0;
		double my = MathUtil.log(Math.tan((90 + src.getLat()) * Math.PI / 360.0)) / (Math.PI / 180.0);

		my = my * originShift / 180.0;
		return new Place(null, mx, my);
	}

	//some magic numbers
	//at 14-zoom level
	//100 pixels height =  diff in latitude
	//100 pixels width = 0.00825 digg in longitude
	private static final double LAT_COEFF_FOR_ZOOM_14 = 0.0043157;
	private static final double LON_COEFF_FOR_ZOOM_14 = 0.00825;

	private static final double LAT_COEFF_FOR_ZOOM_13 = 0.00879;
	private static final double LON_COEFF_FOR_ZOOM_13 = 0.01741;


	public static String buildBBox(Place place, int screenWidth, int screenHeight) {

		int wDiff = screenWidth / 2;
		int hDiff = screenHeight / 2;

		System.out.println(screenWidth + " " + screenHeight);

		double west = place.getLat() + (hDiff * LAT_COEFF_FOR_ZOOM_13) / 100;
		double east = place.getLat() - (hDiff * LAT_COEFF_FOR_ZOOM_13) / 100;
		double north = place.getLon() - (wDiff * LON_COEFF_FOR_ZOOM_13) / 100;
		double south = place.getLon() + (wDiff * LON_COEFF_FOR_ZOOM_13) / 100;

		System.out.println(west + "," + north);
		System.out.println(east + "," + south);
		Place nw = fromLatLonToMeters(new Place(null, west, north));
		Place se = fromLatLonToMeters(new Place(null, east, south));

		return nw.getLat() + "," + se.getLon() + "," + se.getLat() + "," + nw.getLon();
	}
}
