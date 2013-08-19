package ru.mobilespbtransport.controller;

import ru.mobilespbtransport.Main;
import ru.mobilespbtransport.cache.Cache;
import ru.mobilespbtransport.location.Locator;
import ru.mobilespbtransport.model.*;
import ru.mobilespbtransport.network.HttpClient;
import ru.mobilespbtransport.network.RequestGenerator;
import ru.mobilespbtransport.network.ResponseParser;
import ru.mobilespbtransport.view.*;
import ru.mobilespbtransport.view.mapview.MapView;


import javax.microedition.lcdui.Image;
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
    private static MapView mapView;
	private static FavouritesScreen favouritesScreen;

	private static final TaskQueue tasks = new TaskQueue();

	public static void setMain(Main main) {
		Controller.main = main;
	}

	public static TaskQueue getTasks() {
		return tasks;
	}

	public static Model getModel() {
		return model;
	}

	public static void setModel(Model model) {
		Controller.model = model;
	}

    public static void setMapView(MapView mapView) {
        Controller.mapView = mapView;
    }

    public static MapView getMapView() {
        return mapView;
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

	public static void setFilters(boolean showBus, boolean showTrolley, boolean showTram) {
		model.setShowBus(showBus);
		model.setShowTrolley(showTrolley);
		model.setShowTram(showTram);
		Cache.saveModel();
	}

	public static void setAutoUpdate(boolean isAutoUpdate) {
		model.setUseAutoUpdate(isAutoUpdate);
		Cache.saveModel();
	}

	public static void setCurrentPlace(Place place) {
		mapView.setMapCenter(place.getCoordinate());
	}

	public static void addFavourite(Favourite favourite) {
		if (!model.getFavourites().contains(favourite)) {
			model.getFavourites().addElement(favourite);
			favouritesScreen.update();
			Cache.saveModel();
		}
	}

	public static void removeFavourite(Favourite favourite) {
		model.getFavourites().removeElement(favourite);
		favouritesScreen.update();
		Cache.saveModel();
	}

	public static void exit() {
		if (main == null) {
			throw new IllegalStateException("main midlet class not setted");
		}
		if(mapView != null && mapView.getMapCenter() != null){
            model.setLastCoordinate(mapView.getMapCenter());
            model.setLastZoom(mapView.getZoom());
        }
        Cache.saveModel();
        main.exit();
	}

	public static void locateMe() {
		sheduleTask(new Runnable() {
			public void run() {
				Coordinate coordinate = Locator.getLocation();
				if (coordinate != null) {
					Place place = new Place(null, coordinate);
					setCurrentPlace(place);
				} else {
					ScreenStack.push(favouritesScreen);
				}
			}
		});
	}

	public static void findRoutes(final String routeNumber) {
		final RoutesListScreen routesList = new RoutesListScreen();
		ScreenStack.push(routesList);
		sheduleTask(new Runnable() {
			public void run() {
				String url = "http://transport.orgp.spb.ru/Portal/transport/routes/list";
				String request = RequestGenerator.getRequestForSearchRoutes(routeNumber);
				String response = HttpClient.sendPost(url, request);
				Vector routes = ResponseParser.parseRoutes(response);
				routesList.setRoutes(routes);
				Cache.saveRoutes();
			}
		});
	}

	public static void findPlaces(final String address) {
		final PlacesListScreen placesList = new PlacesListScreen();
		ScreenStack.push(placesList);

		sheduleTask(new Runnable() {
			public void run() {
				String request = RequestGenerator.getUrlForGeocoding(address);
				String response = HttpClient.sendGET(request);
				Vector places = ResponseParser.parseGeocoderResponse(response);
				placesList.setPlaces(places);
			}
		});
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
		sheduleTask(new Runnable() {
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
					responseDirect = null; //clearing memory
					for (Enumeration e = stopsDirect.elements(); e.hasMoreElements(); ) {
						Stop stop = (Stop) e.nextElement();
						stop.setDirect(true);
						stops.addElement(stop); //to result

						addStop(stop); //to cached stops
						route.addStopId(stop.getId()); //lining stops and routes
						stop.addRouteId(route.getId());
					}

					//clearing memory
					urlDirect = null;
					stopsDirect = null;
					System.gc();

					String urlReturn = RequestGenerator.getUrlForReturnStops(route);
					String responseReturn = HttpClient.sendPost(urlReturn, request);
					Vector stopsReturn = ResponseParser.parseStopsByRoute(responseReturn, route.getTransportType());
					responseReturn = null; //clearing memory
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
		});
	}

	public static void updateArrivingScreen(final Stop stop, final ArrivingScreen arrivingScreen) {
		sheduleTask(new Runnable() {
			public void run() {
				String url = RequestGenerator.getUrlForArriving(stop);
				String request = RequestGenerator.getRequestForArriving();
				String response = HttpClient.sendPost(url, request);
				Vector arriving = ResponseParser.parseArriving(response, stop.getRoutesId());
				stop.setArriving(arriving);

				arrivingScreen.updateRoutes();
			}
		});
	}

	public static void sheduleTask(Runnable task){
		synchronized (tasks) {
			tasks.push(task);
			tasks.notify();
		}
	}
}
