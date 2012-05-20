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

		double west = place.getCoordinate().toWGS84().getLat() + (hDiff * LAT_COEFF_FOR_ZOOM_13) / 100;
		double east = place.getCoordinate().toWGS84().getLat() - (hDiff * LAT_COEFF_FOR_ZOOM_13) / 100;
		double north = place.getCoordinate().toWGS84().getLon() - (wDiff * LON_COEFF_FOR_ZOOM_13) / 100;
		double south = place.getCoordinate().toWGS84().getLon() + (wDiff * LON_COEFF_FOR_ZOOM_13) / 100;


		Coordinate nw = new Coordinate(west, north, Coordinate.WGS84).toEPSG();
		Coordinate se = new Coordinate(east, south, Coordinate.WGS84).toEPSG();

		return nw.getLat() + "," + se.getLon() + "," + se.getLat() + "," + nw.getLon();
	}
}


