package ru.mobilespbtransport;

import ru.mobilespbtransport.cache.Cache;
import ru.mobilespbtransport.model.Place;
import ru.mobilespbtransport.model.GeoConverter;
import ru.mobilespbtransport.model.Model;
import ru.mobilespbtransport.network.HttpClient;
import ru.mobilespbtransport.network.ImageLoader;
import ru.mobilespbtransport.screens.FavouritesList;
import ru.mobilespbtransport.screens.MapScreen;

import javax.microedition.lcdui.Image;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */
public class Controller {
	private static Main main; //for destroy app
	private static Model model;
	private static MapScreen mapScreen;
	private static FavouritesList favouritesList;

	public static Main getMain() {
		return main;
	}

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

	public static FavouritesList getFavouritesList() {
		return favouritesList;
	}

	public static void setFavouritesList(FavouritesList favouritesList) {
		Controller.favouritesList = favouritesList;
		favouritesList.setFavourites(getModel().getFavourites());
		favouritesList.update();
	}

	public static void setLayers(boolean showBus, boolean showTrolley, boolean showTram) {
		model.setShowBus(showBus);
		model.setShowTrolley(showTrolley);
		model.setShowTram(showTram);
		Cache.saveModel(model);
		loadTransportLayer();
	}

	public static void setAutoUpdate(boolean isAutoUpdate) {
		model.setUseAutoUpdate(isAutoUpdate);
		Cache.saveModel(model);
	}

	public static void setCurrentPlace(Place place) {
		model.setCurrentPlace(place);
		loadMap();
		loadTransportLayer();
	}
	
	public static Place getCurrentPlace(){
		return model.getCurrentPlace();
	}

	public static void addFavourite(Place place) {
		if (!model.getFavourites().contains(place)) {
			model.getFavourites().addElement(place);
			favouritesList.update();
			Cache.saveModel(model);
		}
	}

	public static void removeFavourite(Place place) {
		model.getFavourites().removeElement(place);
		favouritesList.update();
		Cache.saveModel(model);
	}

	public static void loadMap() {
		new Thread() {
			public void run() {
				try {
					if (model.getCurrentPlace() == null) {
						return;
					}
					String url = getMapUri(model.getCurrentPlace(), mapScreen.getWidth(), mapScreen.getHeight());
					System.out.println(url);
					Image map = ImageLoader.getMapImage(model.getCurrentPlace(), url);
					mapScreen.setMap(map);
					mapScreen.repaint();
				} catch (Exception e) {
					e.printStackTrace();  //TODO
				}
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
					String bBox = GeoConverter.buildBBox(model.getCurrentPlace(), mapScreen.getWidth(), mapScreen.getHeight());
					String url = getTransportMapUrl(bBox, model.isShowBus(), model.isShowTrolley(), model.isShowTram(), mapScreen.getWidth(), mapScreen.getHeight());
					System.out.println(url);
					Image transportLayer = ImageLoader.getImageFromInet(url);
					mapScreen.setTransportLayer(transportLayer);
					mapScreen.repaint();
				} catch (Exception e) {
					e.printStackTrace();  //TODO
				}
			}
		}.start();
	}

	private static String getTransportMapUrl(String bbox, boolean showBus, boolean showTrolley, boolean showTram, int screenWidth, int screenHeight) {
		boolean isCommaRequired = false;
		String layers = "";
		if (showBus) {
			layers = layers + "vehicle_bus";
			isCommaRequired = true;
		}
		if (showTrolley) {
			layers = layers + (isCommaRequired ? "," : "") + "vehicle_trolley";
			isCommaRequired = true;
		}
		if (showTram) {
			layers = layers + (isCommaRequired ? "," : "") + "vehicle_tram";
			isCommaRequired = true;
		}
		return "http://transport.orgp.spb.ru/cgi-bin/mapserv?TRANSPARENT=TRUE&FORMAT=image%2Fpng&LAYERS=" + layers + "&MAP=vehicle_typed.map&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&SRS=EPSG%3A900913&_OLSALT=0.1508798657450825&BBOX=" + bbox + "&WIDTH=" + screenWidth + "&HEIGHT=" + screenHeight;
	}

	private static String getMapUri(Place center, int screenWidth, int screenHeight) {
		return "http://maps.google.com/maps/api/staticmap?zoom=13&sensor=false&size=" + screenWidth + "x" + screenHeight + "&center=" + center.getLat() + "," + center.getLon();
	}

	public static void doMagic() {
		//final String url = "http://transport.orgp.spb.ru/Portal/transport/stop/16941/arriving";
		//final String request = "sEcho=8&iColumns=4&sColumns=index%2CrouteNumber%2CtimeToArrive%2CparkNumber&iDisplayStart=0&iDisplayLength=-1&sNames=index%2CrouteNumber%2CtimeToArrive%2CparkNumber";
		final String url = "http://transport.orgp.spb.ru/Portal/transport/stops/list";
		final String request = "sEcho=31&iColumns=7&sColumns=id%2CtransportType%2Cname%2Cimages%2CnearestStreets%2Croutes%2ClonLat&iDisplayStart=0&iDisplayLength=25&sNames=id%2CtransportType%2Cname%2Cimages%2CnearestStreets%2Croutes%2ClonLat&iSortingCols=1&iSortCol_0=0&sSortDir_0=asc&bSortable_0=true&bSortable_1=true&bSortable_2=true&bSortable_3=false&bSortable_4=true&bSortable_5=false&bSortable_6=false&transport-type=0&transport-type=2&transport-type=1&use-bbox=true&bbox-value=3379151.668188%2C8411310.270772%2C3379587.053501%2C8411787.237829";
		System.out.println(request);
		
		new Thread() {
			public void run() {
				try {
					String response;
					response = HttpClient.sendPost(url, request);
					System.out.println(response);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static void exit(){
		if(main == null){
			throw new IllegalStateException("main midlet class not setted");
		}
		main.exit();
	}

	public static MapScreen getMapScreen() {
		return mapScreen;
	}
}
