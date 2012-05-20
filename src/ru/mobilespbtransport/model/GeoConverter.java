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
		System.out.println("tan result = " + Math.tan((90 + src.getLat())));
		System.out.println("log result = " + MathUtil.log(Math.tan((90 + src.getLat()) * Math.PI / 360.0)));
		double my = MathUtil.log(Math.tan((90 + src.getLat()) * Math.PI / 360.0)) / (Math.PI / 180.0);

		my = my * originShift / 180.0;
		return new Place(null, mx, my);
	}

	public static Place tileToWorldPos(double x, double y)
	{
		//"Converts XY point from Spherical Mercator EPSG:900913 to lat/lon in WGS84 Datum"
		double originShift = Math.PI * 6378137;
		double lon = (x / originShift) * 180.0;
		double lat = (y / originShift) * 180.0;

		lat = 180 / Math.PI * (2 * MathUtil.atan( MathUtil.exp( lat * Math.PI / 180.0)) - Math.PI / 2.0);
		Place p = new Place("", lat, lon);
		return p;
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

		Place nw = fromLatLonToMeters(new Place(null, west, north));
		Place p = GeoConverter.tileToWorldPos(nw.getLat(), nw.getLon());
		System.out.println(">1: " + west + ", " + north);
		System.out.println(">2: " + nw.getLat() + ", " + nw.getLon());
		System.out.println(">3: " + p.getLat() + ", " + p.getLon());
		Place se = fromLatLonToMeters(new Place(null, east, south));

		return nw.getLat() + "," + se.getLon() + "," + se.getLat() + "," + nw.getLon();
	}
}


