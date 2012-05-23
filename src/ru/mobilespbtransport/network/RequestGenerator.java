package ru.mobilespbtransport.network;

import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.model.Place;
import ru.mobilespbtransport.model.Route;
import ru.mobilespbtransport.model.Stop;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 20.05.12
 * Time: 12:09
 * To change this template use File | Settings | File Templates.
 */
public class RequestGenerator {

	public static String getUrlForGeocoding(String address) {
		String encodedAddress;
		encodedAddress = URLEncoder.encode(address);
		return
				"http://maps.googleapis.com/maps/api/geocode/json?" +
						"address=" + encodedAddress +
						"&bounds=59.661,29.518|60.270,30.757" +  //SPB only
						"&sensor=false" + // + (Controller.isLocationSupported() ? "true" : "false") +
						"&language=ru";
	}

	public static String getRequestForSearchRoutes(String routeNumber) {
		return "sEcho=4&iColumns=9&sColumns=id%2CtransportType%2CrouteNumber%2Cname%2Curban%2CpoiStart%2CpoiFinish%2CscheduleLinkColumn%2CmapLinkColumn&iDisplayStart=0&iDisplayLength=25&sNames=id%2CtransportType%2CrouteNumber%2Cname%2Curban%2CpoiStart%2CpoiFinish%2CscheduleLinkColumn%2CmapLinkColumn&iSortingCols=1&iSortCol_0=2&sSortDir_0=asc&bSortable_0=true&bSortable_1=true&bSortable_2=true&bSortable_3=true&bSortable_4=true&bSortable_5=true&bSortable_6=true&bSortable_7=false&bSortable_8=false&transport-type=0&transport-type=2&transport-type=1&route-number=" + routeNumber;
	}

	public static String getRequestForSearchStopsByRoute() {
		return "sEcho=1&iColumns=7&sColumns=id%2Cname%2Cimages%2CnearestStreets%2Croutes%2ClonLat%2Cfinish&iDisplayStart=0&iDisplayLength=-1&sNames=id%2Cname%2Cimages%2CnearestStreets%2Croutes%2ClonLat%2Cfinish";
	}

	public static String getUrlForDirectStops(Route route) {
		return "http://transport.orgp.spb.ru/Portal/transport/route/" + route.getId() + "/stops/direct";
	}

	public static String getUrlForReturnStops(Route route) {
		return "http://transport.orgp.spb.ru/Portal/transport/route/" + route.getId() + "/stops/return";
	}

	public static String getUrlForArriving(Stop stop) {
		return "http://transport.orgp.spb.ru/Portal/transport/stop/" + stop.getId() + "/arriving";
	}

	public static String getRequestForArriving() {
		return "sEcho=2&iColumns=4&sColumns=index%2CrouteNumber%2CtimeToArrive%2CparkNumber&iDisplayStart=0&iDisplayLength=-1&sNames=index%2CrouteNumber%2CtimeToArrive%2CparkNumber";
	}

	public static String getTransportMapUrl(String bbox, boolean showBus, boolean showTrolley, boolean showTram, int screenWidth, int screenHeight) {
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

	public static String getMapUrl(Place center, int screenWidth, int screenHeight, int zoom) {
		return "http://maps.google.com/maps/api/staticmap?zoom=" + zoom +
				"&sensor=false" + // + (Controller.isLocationSupported() ? "true" : "false") +
				"&size=" + screenWidth +
				"x" + screenHeight +
				"&center=" + center.getCoordinate().toWGS84().getLat() +
				"," + center.getCoordinate().toWGS84().getLon();
	}

	public static String getRequestForStopsOnMap(String bbox) {
		return "sEcho=31&iColumns=7&sColumns=id%2CtransportType%2Cname%2Cimages%2CnearestStreets%2Croutes%2ClonLat&iDisplayStart=0&iDisplayLength=25&sNames=id%2CtransportType%2Cname%2Cimages%2CnearestStreets%2Croutes%2ClonLat&iSortingCols=1&iSortCol_0=0&sSortDir_0=asc&bSortable_0=true&bSortable_1=true&bSortable_2=true&bSortable_3=false&bSortable_4=true&bSortable_5=false&bSortable_6=false&transport-type=0&transport-type=2&transport-type=1&use-bbox=true&bbox-value=" + bbox; //3379151.668188%2C8411310.270772%2C3379587.053501%2C8411787.237829";
	}
}
