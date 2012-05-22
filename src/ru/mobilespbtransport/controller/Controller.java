package ru.mobilespbtransport.controller;

import ru.mobilespbtransport.Main;
import ru.mobilespbtransport.cache.Cache;
import ru.mobilespbtransport.model.*;
import ru.mobilespbtransport.network.HttpClient;
import ru.mobilespbtransport.network.RequestGenerator;
import ru.mobilespbtransport.network.ResponseParser;
import ru.mobilespbtransport.view.*;


import javax.microedition.lcdui.Image;
import javax.microedition.location.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */
public class Controller {
	private static Main main; //for destroy app
	private static Model model = new Model();
	private static MapScreen mapScreen;
	private static FavouritesScreen favouritesScreen;

	public final static int DEFAULT_ZOOM = 13;
	public final static int SELECT_STOP_ZOOM = 16;
	private static boolean isZoomedIn = false;
	private static int zoom = DEFAULT_ZOOM;

	public static void setMain(Main main) {
		Controller.main = main;
	}

	public static Model getModel() {
		return model;
	}

	public static void setModel(Model model) {
		Controller.model = model;
	}

	public static void setMapScreen(MapScreen mapScreen) {
		Controller.mapScreen = mapScreen;
	}

	public static FavouritesScreen getFavouritesScreen() {
		return favouritesScreen;
	}

	public static void setFavouritesScreen(FavouritesScreen favouritesScreen) {
		Controller.favouritesScreen = favouritesScreen;
		favouritesScreen.setFavourites(getModel().getFavourites());
		favouritesScreen.update();
	}

	public static void addStop(Stop stop) {
		Integer key = new Integer(stop.getId());
		if (!model.getStops().containsKey(key)) {
			model.getStops().put(key, stop);
		}
	}

	public static void addRoute(Route route) {
		Integer key = new Integer(route.getId());
		if (!model.getRoutes().containsKey(key)) {
			model.getRoutes().put(key, route);
		}
	}

	public static Route getRoute(int id) {
		Integer key = new Integer(id);
		return (Route) model.getRoutes().get(key);
	}

	public static Stop getStop(int id) {
		Integer key = new Integer(id);
		return (Stop) model.getStops().get(key);
	}

	public static void setLayers(boolean showBus, boolean showTrolley, boolean showTram) {
		model.setShowBus(showBus);
		model.setShowTrolley(showTrolley);
		model.setShowTram(showTram);
		Cache.saveModel();
		loadTransportLayer();
	}

	public static void setAutoUpdate(boolean isAutoUpdate) {
		model.setUseAutoUpdate(isAutoUpdate);
		Cache.saveModel();
	}

	public static void setCurrentPlace(Place place) {
		model.setCurrentPlace(place);
		loadMap();
		if (isZoomedIn) {
			loadStopsToMap();
		} else {
			loadTransportLayer();
		}
	}

	public static Place getCurrentPlace() {
		return model.getCurrentPlace();
	}

	public static void addFavourite(Place place) {
		if (!model.getFavourites().contains(place)) {
			model.getFavourites().addElement(place);
			favouritesScreen.update();
			Cache.saveModel();
		}
	}

	public static void removeFavourite(Place place) {
		model.getFavourites().removeElement(place);
		favouritesScreen.update();
		Cache.saveModel();
	}

	public static void loadMap() {
		new Thread() {
			public void run() {
				if (model.getCurrentPlace() == null) {
					return;
				}
				String url = RequestGenerator.getMapUrl(model.getCurrentPlace(), mapScreen.getWidth(), mapScreen.getHeight(), zoom);
				Image map = getMapImage(model.getCurrentPlace(), url, zoom);
				mapScreen.setMap(map);
				mapScreen.repaint();
			}
		}.start();
	}

	public static void loadTransportLayer() {
		new Thread() {
			public void run() {
				try {
					if (model.getCurrentPlace() == null) {
						return;
					}
					String bBox = GeoConverter.buildBBox(model.getCurrentPlace().getCoordinate(), mapScreen.getWidth(), mapScreen.getHeight(), zoom);
					String url = RequestGenerator.getTransportMapUrl(bBox, model.isShowBus(), model.isShowTrolley(), model.isShowTram(), mapScreen.getWidth(), mapScreen.getHeight());
					Image transportLayer = HttpClient.loadImage(url);
					mapScreen.setTransportLayer(transportLayer);
					mapScreen.repaint();
				} catch (Exception e) {
					e.printStackTrace();  //TODO
				}
			}
		}.start();
	}

	public static void loadStopsToMap() {
		new Thread() {
			public void run() {
				String url = "http://transport.orgp.spb.ru/Portal/transport/stops/list";
				String bBox = GeoConverter.buildBBox(model.getCurrentPlace().getCoordinate(), mapScreen.getWidth(), mapScreen.getHeight(), zoom);
				String request = RequestGenerator.getRequestForStopsOnMap(bBox);
				String response = HttpClient.sendPost(url, request);
				Vector stops = ResponseParser.parseStopsToMap(response);
				for (Enumeration e = stops.elements(); e.hasMoreElements(); ) {
					addStop((Stop) e.nextElement()); //to cached stops
				}
				Cache.saveStops();
				Cache.saveRoutes();
				mapScreen.setStops(stops);
				mapScreen.repaint();
			}
		}.start();
	}


	public static void exit() {
		if (main == null) {
			throw new IllegalStateException("main midlet class not setted");
		}
		main.exit();
	}

	public static MapScreen getMapScreen() {
		return mapScreen;
	}

	public static boolean isLocationSupported() {
		boolean isItTrue = true;
		try {
			Class.forName("javax.microedition.location.Location");
		} catch (Exception e) {
			isItTrue = false;
		}
		return isItTrue;
	}

	public static Place getMyLocation() {
		if (isLocationSupported()) {
			try {
				Criteria cr = new Criteria();
				cr.setHorizontalAccuracy(500);
				LocationProvider lp = null;
				lp = LocationProvider.getInstance(cr);
				Location l = lp.getLocation(10);
				QualifiedCoordinates qc = l.getQualifiedCoordinates();
				return new Place("", new Coordinate(qc.getLatitude(), qc.getLongitude(), Coordinate.WGS84));
			} catch (LocationException e) {
				ScreenStack.showAlert("Не удалось получить координаты GPS");
				e.printStackTrace();
			} catch (InterruptedException e) {
				ScreenStack.showAlert("Не удалось получить координаты GPS");
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void locateMe() {
		new Thread() {
			public void run() {
				Place place = getMyLocation();
				if (place != null) {
					setCurrentPlace(place);
				}
			}
		}.start();
	}

	public static void findRoutes(final String routeNumber) {
		final RoutesListScreen routesList = new RoutesListScreen();
		ScreenStack.push(routesList);
		new Thread() {
			public void run() {
				String url = "http://transport.orgp.spb.ru/Portal/transport/routes/list";
				String request = RequestGenerator.getRequestForSearchRoutes(routeNumber);

				String response = HttpClient.sendPost(url, request);

				Vector routes = ResponseParser.parseRoutes(response);
				for (Enumeration e = routes.elements(); e.hasMoreElements(); ) {
					Route route = (Route) e.nextElement();
					addRoute(route);
				}
				routesList.setRoutes(routes);
				//Cache.saveRoutes();
			}
		}.start();
	}

	public static void findPlaces(final String address) {
		final PlacesListScreen placesList = new PlacesListScreen();
		ScreenStack.push(placesList);

		new Thread() {
			public void run() {
				String request = RequestGenerator.getUrlForGeocoding(address);

				String response = HttpClient.sendGET(request);

				Vector places = ResponseParser.parseGeocoderResponse(response);

				placesList.setPlaces(places);
			}
		}.start();
	}

	public static Vector getRoutes(final Stop stop) {
		Vector routes = new Vector();
		for (Enumeration e = stop.getRoutesId().elements(); e.hasMoreElements(); ) {
			Integer routeId = (Integer) e.nextElement();
			routes.addElement(Controller.getRoute(routeId.intValue()));
		}
		return routes;
	}

	public static void findStops(final Route route) {
		final StopsListScreen stopsScreen = new StopsListScreen();
		ScreenStack.push(stopsScreen);
		new Thread() {
			public void run() {
				Vector stops = new Vector();
				if (route.isStopsLoaded()) {
					//found in cache
					stops = new Vector(route.getStopsId().size());
					for (Enumeration e = route.getStopsId().elements(); e.hasMoreElements(); ) {
						Integer stopId = (Integer) e.nextElement();
						stops.addElement(getStop(stopId.intValue()));
					}
				} else {
					//no found in cache
					//loading from inet
					String request = RequestGenerator.getRequestForSearchStopsByRoute();

					String urlDirect = RequestGenerator.getUrlForDirectStops(route);
					String responseDirect = HttpClient.sendPost(urlDirect, request);
					Vector stopsDirect = ResponseParser.parseStopsByRoute(responseDirect, route.getTransportType());
					for (Enumeration e = stopsDirect.elements(); e.hasMoreElements(); ) {
						Stop stop = (Stop) e.nextElement();
						stop.setDirect(true);
						stops.addElement(stop); //to result

						addStop(stop); //to cached stops
						route.addStopId(stop.getId()); //lining stops and routes
						stop.addRouteId(route.getId());
					}

					String urlReturn = RequestGenerator.getUrlForReturnStops(route);
					String responseReturn = HttpClient.sendPost(urlReturn, request);
					Vector stopsReturn = ResponseParser.parseStopsByRoute(responseReturn, route.getTransportType());
					for (Enumeration e = stopsReturn.elements(); e.hasMoreElements(); ) {
						Stop stop = (Stop) e.nextElement();
						stop.setDirect(false);
						stops.addElement(stop); //to result

						addStop(stop); //to cached stops
						route.addStopId(stop.getId()); //lining stops and routes
						stop.addRouteId(route.getId());
					}
					route.setStopsLoaded(true);
					Cache.saveStops();
					Cache.saveRoutes();
				}
				stopsScreen.setStops(stops);
			}
		}.start();
	}

	public static void updateArrivingScreen(final Stop stop, final ArrivingScreen arrivingScreen) {
		new Thread() {
			public void run() {
				String url = RequestGenerator.getUrlForArriving(stop);
				String request = RequestGenerator.getRequestForArriving();

				String response = HttpClient.sendPost(url, request);
				Vector arriving = ResponseParser.parseArriving(response, stop.getRoutesId());

				stop.setArriving(arriving);

				arrivingScreen.updateRoutes();
			}
		}.start();
	}

	public static void zoomIn() {
		isZoomedIn = true;
		zoom = SELECT_STOP_ZOOM;
		Coordinate coordinate = GeoConverter.getZoomedInCoordinate(getCurrentPlace().getCoordinate(),
				mapScreen.getCursorX(), mapScreen.getCursorY(),
				mapScreen.getWidth(), mapScreen.getHeight(),
				DEFAULT_ZOOM);
		Place zoomedPlace = new Place(Controller.getCurrentPlace().getName(), coordinate);
		setCurrentPlace(zoomedPlace);
	}

	public static void zoomOut() {
		isZoomedIn = false;
		zoom = DEFAULT_ZOOM;
	}

	public static boolean isZoomedIn() {
		return isZoomedIn;
	}

	//load image from cache or inet
	//save downloaded image to cache
	public static Image getMapImage(Place place, String url, int zoom) {
		if (Cache.isImageExists(place, zoom)) {
			System.out.println("found in cache");
			return Cache.loadImage(place, zoom);
		} else {
			System.out.println("not found in cache");
			byte[] imageData = HttpClient.loadImageBytes(url);
			System.out.println("image loaded");
			Cache.saveImage(place, imageData, zoom);
			System.out.println("image saved");
			Image im = Image.createImage(imageData, 0, imageData.length);
			return (im == null ? null : im);
		}
	}

	public static void moveMapLeft() {
		setCurrentPlace(GeoConverter.moveMapLeft(getCurrentPlace(), mapScreen.getWidth(), zoom));
	}

	public static void moveMapRight() {
		setCurrentPlace(GeoConverter.moveMapRight(getCurrentPlace(), mapScreen.getWidth(), zoom));
	}

	public static void moveMapUp() {
		setCurrentPlace(GeoConverter.moveMapUp(getCurrentPlace(), mapScreen.getHeight(), zoom));
	}

	public static void moveMapDown() {
		setCurrentPlace(GeoConverter.moveMapDown(getCurrentPlace(), mapScreen.getHeight(), zoom));
	}

	public static int getZoom() {
		return zoom;
	}
}
