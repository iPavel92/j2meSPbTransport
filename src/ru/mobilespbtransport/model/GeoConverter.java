package ru.mobilespbtransport.model;

import akme.mobile.util.MathUtil;

import java.util.Hashtable;


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

	private static final Hashtable zoomLatCoeff = new Hashtable(); //int -> double
	private static final Hashtable zoomLonCoeff = new Hashtable(); //int -> double

	static {
		zoomLatCoeff.put(new Integer(13), new Double(0.00879));
		zoomLonCoeff.put(new Integer(13), new Double(0.01741));

		zoomLatCoeff.put(new Integer(14), new Double(0.0043157));
		zoomLonCoeff.put(new Integer(14), new Double(0.00825));
	}

	public static String buildBBox(Place place, int screenWidth, int screenHeight, int zoom) {
		double west = place.getCoordinate().toWGS84().getLat() + getLatDiffToEndOfTheScreen(screenHeight, zoom);
		double east = place.getCoordinate().toWGS84().getLat() - getLatDiffToEndOfTheScreen(screenHeight, zoom);
		double north = place.getCoordinate().toWGS84().getLon() - getLonDiffToEndOfTheScreen(screenWidth, zoom);
		double south = place.getCoordinate().toWGS84().getLon() + getLonDiffToEndOfTheScreen(screenWidth, zoom);

		Coordinate nw = new Coordinate(west, north, Coordinate.WGS84).toEPSG();
		Coordinate se = new Coordinate(east, south, Coordinate.WGS84).toEPSG();

		return nw.getLat() + "," + se.getLon() + "," + se.getLat() + "," + nw.getLon();
	}

	public static double getLatDiffToEndOfTheScreen(int screenHeight, int zoom) {
		int hDiff = screenHeight / 2;
		return hDiff * ((Double) zoomLatCoeff.get(new Integer(zoom))).doubleValue() / 100;
	}

	public static double getLonDiffToEndOfTheScreen(int screenWidth, int zoom) {
		int wDiff = screenWidth / 2;
		return wDiff * ((Double) zoomLonCoeff.get(new Integer(zoom))).doubleValue() / 100;
	}

	public static Place moveMapLeft(Place place, int screenWidth, int zoom) {
		return new Place(place.getName(),
				new Coordinate(
						place.getCoordinate().toWGS84().getLat(),
						place.getCoordinate().toWGS84().getLon() - getLonDiffToEndOfTheScreen(screenWidth, zoom) * 2,
						Coordinate.WGS84
				)
		);
	}

	public static Place moveMapUp(Place place, int screenHeight, int zoom) {
		return new Place(place.getName(),
				new Coordinate(
						place.getCoordinate().toWGS84().getLat() + getLatDiffToEndOfTheScreen(screenHeight, zoom) * 2,
						place.getCoordinate().toWGS84().getLon(),
						Coordinate.WGS84
				)
		);
	}

	public static Place moveMapRight(Place place, int screenWidth, int zoom) {
		return new Place(place.getName(),
				new Coordinate(
						place.getCoordinate().toWGS84().getLat(),
						place.getCoordinate().toWGS84().getLon() + getLonDiffToEndOfTheScreen(screenWidth, zoom) * 2,
						Coordinate.WGS84
				)
		);
	}

	public static Place moveMapDown(Place place, int screeHeight, int zoom) {
		return new Place(place.getName(),
				new Coordinate(
						place.getCoordinate().toWGS84().getLat() - getLatDiffToEndOfTheScreen(screeHeight, zoom) * 2,
						place.getCoordinate().toWGS84().getLon(),
						Coordinate.WGS84
				)
		);
	}
}


