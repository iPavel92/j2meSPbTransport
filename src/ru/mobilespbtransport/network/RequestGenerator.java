package ru.mobilespbtransport.network;

import ru.mobilespbtransport.Controller;
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
						"&sensor=" + Controller.isLocationSupported() + //fair play
						"&language=ru";
	}

	public static String getRequestForSearchRoutes(String routeNumber) {
		return "sEcho=4&iColumns=9&sColumns=id%2CtransportType%2CrouteNumber%2Cname%2Curban%2CpoiStart%2CpoiFinish%2CscheduleLinkColumn%2CmapLinkColumn&iDisplayStart=0&iDisplayLength=25&sNames=id%2CtransportType%2CrouteNumber%2Cname%2Curban%2CpoiStart%2CpoiFinish%2CscheduleLinkColumn%2CmapLinkColumn&iSortingCols=1&iSortCol_0=2&sSortDir_0=asc&bSortable_0=true&bSortable_1=true&bSortable_2=true&bSortable_3=true&bSortable_4=true&bSortable_5=true&bSortable_6=true&bSortable_7=false&bSortable_8=false&transport-type=0&transport-type=2&transport-type=1&route-number=" + routeNumber;
	}

	public static String getRequestForSearchStopsByRoute() {
		return "sEcho=1&iColumns=7&sColumns=id%2Cname%2Cimages%2CnearestStreets%2Croutes%2ClonLat%2Cfinish&iDisplayStart=0&iDisplayLength=-1&sNames=id%2Cname%2Cimages%2CnearestStreets%2Croutes%2ClonLat%2Cfinish";
	}

	public static String getUrlForDirectStops(Route route){
		return "http://transport.orgp.spb.ru/Portal/transport/route/" + route.getId() + "/stops/direct";
	}

	public static String getUrlForReturnStops(Route route){
		return "http://transport.orgp.spb.ru/Portal/transport/route/" + route.getId() + "/stops/return";
	}

	public static String getUrlForArriving(Stop stop){
		return "http://transport.orgp.spb.ru/Portal/transport/stop/" + stop.getId() + "/arriving";
	}

	public static String getRequestForArriving() {
		return "sEcho=2&iColumns=4&sColumns=index%2CrouteNumber%2CtimeToArrive%2CparkNumber&iDisplayStart=0&iDisplayLength=-1&sNames=index%2CrouteNumber%2CtimeToArrive%2CparkNumber";
	}
}
