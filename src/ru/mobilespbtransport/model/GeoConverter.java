package ru.mobilespbtransport.model;

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
	//100 pixels height = 0.0043157 diff in latitude
	//100 pixels width = 0.00825 diff in longitude

	private static final Hashtable zoomLatCoeff = new Hashtable(); //int -> double
	private static final Hashtable zoomLonCoeff = new Hashtable(); //int -> double

	static {
		zoomLatCoeff.put(new Integer(13), new Double(0.00879));
		zoomLonCoeff.put(new Integer(13), new Double(0.01741));

		zoomLatCoeff.put(new Integer(14), new Double(0.0043157));
		zoomLonCoeff.put(new Integer(14), new Double(0.00825));

		zoomLatCoeff.put(new Integer(16), new Double(0.00104));
		zoomLonCoeff.put(new Integer(16), new Double(0.00208));

		zoomLatCoeff.put(new Integer(17), new Double(0.00081));
		zoomLonCoeff.put(new Integer(17), new Double(0.00103));

		zoomLatCoeff.put(new Integer(18), new Double(0.00039));
		zoomLonCoeff.put(new Integer(18), new Double(0.00052));

		zoomLatCoeff.put(new Integer(19), new Double(0.000199));
		zoomLonCoeff.put(new Integer(19), new Double(0.000258));
	}

	public static String buildBBox(Coordinate coordinate, int screenWidth, int screenHeight, int zoom) {
		double west = coordinate.toWGS84().getLat() + getLatDiff(screenHeight / 2, zoom);
		double east = coordinate.toWGS84().getLat() - getLatDiff(screenHeight / 2, zoom);
		double north = coordinate.toWGS84().getLon() - getLonDiff(screenWidth / 2, zoom);
		double south = coordinate.toWGS84().getLon() + getLonDiff(screenWidth / 2, zoom);

		Coordinate nw = new Coordinate(west, north, Coordinate.WGS84).toEPSG();
		Coordinate se = new Coordinate(east, south, Coordinate.WGS84).toEPSG();

		return nw.getLat() + "," + se.getLon() + "," + se.getLat() + "," + nw.getLon();
	}

	public static double getLatDiff(int pixels, int zoom) {
		return pixels * ((Double) zoomLatCoeff.get(new Integer(zoom))).doubleValue() / 100;
	}

	public static double getLonDiff(int pixels, int zoom) {
		return pixels * ((Double) zoomLonCoeff.get(new Integer(zoom))).doubleValue() / 100;
	}

	public static Place moveMapLeft(Place place, int screenWidth, int zoom) {
		return new Place(place.getName(),
				new Coordinate(
						place.getCoordinate().toWGS84().getLat(),
						place.getCoordinate().toWGS84().getLon() - getLonDiff(screenWidth, zoom),
						Coordinate.WGS84
				)
		);
	}

	public static Place moveMapUp(Place place, int screenHeight, int zoom) {
		return new Place(place.getName(),
				new Coordinate(
						place.getCoordinate().toWGS84().getLat() + getLatDiff(screenHeight, zoom),
						place.getCoordinate().toWGS84().getLon(),
						Coordinate.WGS84
				)
		);
	}

	public static Place moveMapRight(Place place, int screenWidth, int zoom) {
		return new Place(place.getName(),
				new Coordinate(
						place.getCoordinate().toWGS84().getLat(),
						place.getCoordinate().toWGS84().getLon() + getLonDiff(screenWidth, zoom),
						Coordinate.WGS84
				)
		);
	}

	public static Place moveMapDown(Place place, int screeHeight, int zoom) {
		return new Place(place.getName(),
				new Coordinate(
						place.getCoordinate().toWGS84().getLat() - getLatDiff(screeHeight, zoom),
						place.getCoordinate().toWGS84().getLon(),
						Coordinate.WGS84
				)
		);
	}
	
	public static int getBorderWidth(int screenWidth, int currentZoom, int innerZoom){
		return (int)(screenWidth * (((Double)zoomLonCoeff.get(new Integer(innerZoom))).doubleValue() / ((Double)zoomLonCoeff.get(new Integer(currentZoom))).doubleValue()));
	}

	public static int getBorderHeight(int screenHeight, int currentZoom, int innerZoom){
		return (int)(screenHeight * (((Double)zoomLatCoeff.get(new Integer(innerZoom))).doubleValue() / ((Double)zoomLatCoeff.get(new Integer(currentZoom))).doubleValue()));
	}
	
	public static Coordinate getZoomedInCoordinate(Coordinate coordinate, int cursorX, int cursorY, int screenWidth, int screenHeight, int zoom){
		double lon = coordinate.toWGS84().getLon() + getLonDiff(cursorX - screenWidth/2, zoom);
		double lat = coordinate.toWGS84().getLat() + getLatDiff(screenHeight / 2 - cursorY, zoom);
		return new Coordinate(lat, lon, Coordinate.WGS84);
	}
	
	public static int getXPixelFromCoordinate(Coordinate centerCoordinate, Coordinate coordinate, int screenWidth, int zoom){
		double lonDiff = coordinate.toWGS84().getLon() - centerCoordinate.toWGS84().getLon();
		double zoomCoeff = ((Double)zoomLonCoeff.get(new Integer(zoom))).doubleValue();
		return (int)(lonDiff * 100 / zoomCoeff + screenWidth/2);
	}

	public static int getYPixelFromCoordinate(Coordinate centerCoordinate, Coordinate coordinate, int screenHeight, int zoom){
		double latDiff = centerCoordinate.toWGS84().getLat() - coordinate.toWGS84().getLat();
		double zoomCoeff = ((Double)zoomLatCoeff.get(new Integer(zoom))).doubleValue();
		return (int)(latDiff * 100 / zoomCoeff + screenHeight/2);
	}
}


