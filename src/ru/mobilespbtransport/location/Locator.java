package ru.mobilespbtransport.location;

import ru.mobilespbtransport.cache.Cache;
import ru.mobilespbtransport.model.Coordinate;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Locator {
	private static Hashtable cachedLocations; //Hashtable<CellData,Coordinate>

	public static Coordinate getLocation(){
		if(cachedLocations == null){
			initCachedLocations();
		}

		CellData currentCell = new CellData(
				DeviceInfo.getMCC(),
				DeviceInfo.getMCC(),
				DeviceInfo.getCellId(),
				DeviceInfo.getLAC()
		);

		CellData testCell = new CellData(250, 1, 2065, 221);
		//currentCell = testCell;

		if(currentCell.getCellId() == -1 || currentCell.getLac() == -1 || currentCell.getMcc() == -1 || currentCell.getMcc() == -1){
			return null;
		}

		if(cachedLocations.containsKey(currentCell)){
			return (Coordinate)cachedLocations.get(currentCell);
		}

		Vector locationProviders = getCellLocationProviders(); //Vector<CellLocationProvider>
		Vector foundCoordinates = new Vector(); //Vector<Coordinate>
		for(Enumeration e = locationProviders.elements(); e.hasMoreElements(); ){
			try {
				CellLocationProvider locationProvider = (CellLocationProvider)e.nextElement();
				Coordinate coordinate = locationProvider.getLocation(currentCell);
				if(coordinate != null){
					foundCoordinates.addElement(coordinate);
				}
			} catch (Exception exception) {
			}
		}
		Coordinate meanCoordinate = getMeanCoordinate(foundCoordinates);

		addToCache(currentCell, meanCoordinate);

		return meanCoordinate;
	}

	private static Coordinate getMeanCoordinate(Vector coordinates){  //Vector<Coordinate>
		double meanLatitude = 0;
		double meanLongitude = 0;
		for(Enumeration e = coordinates.elements(); e.hasMoreElements(); ){
			Coordinate coordinate = (Coordinate)e.nextElement();
			meanLatitude += coordinate.getLat();
			meanLongitude += coordinate.getLon();
		}
		meanLatitude /= coordinates.size();
		meanLongitude /= coordinates.size();
		return new Coordinate(meanLatitude, meanLongitude);
	}

	private static Vector getCellLocationProviders(){
		Vector locationProviders = new Vector(); //Vector<CellLocationProvider>
		locationProviders.addElement(new YandexCellLocationProvider());
		locationProviders.addElement(new GoogleCellLocationProvider());
		return locationProviders;
	}

	private static void initCachedLocations() {
		cachedLocations = new Hashtable();
		Cache.loadLocationsCache(cachedLocations);
	}

	private static void addToCache(CellData cellData, Coordinate coordinate){
		cachedLocations.put(cellData, coordinate);
		Cache.saveLocationsCache(cachedLocations);
	}
}
