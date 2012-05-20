package ru.mobilespbtransport.network;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;
import org.json.me.JSONTokener;
import ru.mobilespbtransport.model.*;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 20.05.12
 * Time: 12:09
 * To change this template use File | Settings | File Templates.
 */
public class ResponseParser {

	public static Vector parseGeocoderResponse(String response) {
		try {
			Vector result = new Vector();  //Vector<Places>

			JSONTokener jt = new JSONTokener(response);
			JSONObject answer = new JSONObject(jt);
			JSONArray results = answer.getJSONArray("results");
			for (int i = 0; i < results.length(); i++) {
				try {
					JSONObject item = results.getJSONObject(i);
					String name = item.getString("formatted_address");
					JSONObject geometry = item.getJSONObject("geometry");
					JSONObject location = geometry.getJSONObject("location");
					double lat = location.getDouble("lat");
					double lon = location.getDouble("lng");
					Place c = new Place(name, lat, lon);
					result.addElement(c);
				} catch (JSONException e) {
					//ignoring
					e.printStackTrace();
				}
			}

			return result;
		} catch (JSONException e) {
			return null;
		}
	}

	public static Vector parseRoutes(String response) {
		try {
			Vector result = new Vector(); //Vector<Routes>

			JSONTokener jt = new JSONTokener(response);
			JSONObject answer = new JSONObject(jt);

			JSONArray aaData = answer.getJSONArray("aaData");
			for (int i = 0; i < aaData.length(); i++) {
				JSONArray record = aaData.getJSONArray(i);
				int routeId = record.getInt(0);
				JSONObject transportTypeJson = record.getJSONObject(1);
				int transportTypeInt = transportTypeJson.getInt("id");
				TransportType transportType = null;
				switch (transportTypeInt) {
					case 0:
						transportType = new TransportType(TransportType.BUS);
						break;
					case 1:
						transportType = new TransportType(TransportType.TROLLEY);
						break;
					case 2:
						transportType = new TransportType(TransportType.TRAM);
						break;
				}
				String routeNumber = record.getString(2);
				Route route = new Route(transportType, routeNumber, routeId);
				result.addElement(route);
			}
			return result;
		} catch (JSONException e) {
			return null;
		}
	}

	public static Vector parseStopsByRoute(String response, TransportType transportType) {
		try {
			Vector result = new Vector(); //Vector<Stop>

			JSONTokener jt = new JSONTokener(response);
			JSONObject answer = new JSONObject(jt);

			JSONArray aaData = answer.getJSONArray("aaData");
			for (int i = 0; i < aaData.length(); i++) {
				JSONArray record = aaData.getJSONArray(i);

				int id = record.getInt(0);
				String name = record.getString(1);
				JSONObject coordinates = record.getJSONObject(5);
				double lat = coordinates.getDouble("lat");
				double lon = coordinates.getDouble("lon");

				Vector routes = new Vector(); //Vector<Route>
				JSONArray routesJson = record.getJSONArray(4);
				for (int j = 0; j < routesJson.length(); j++) {
					JSONObject routeJson = routesJson.getJSONObject(j);
					int routeId = routeJson.getInt("id");
					String routeNumber = routeJson.getString("routeNumber");
					Route route = new Route(transportType, routeNumber, routeId);
					routes.addElement(route);
				}

				Stop stop = new Stop(name, lat, lon, transportType, id);
				stop.setRoutes(routes);
				result.addElement(stop);
			}
			return result;
		} catch (JSONException e) {
			return null;
		}
	}

	//TODO: it may be more than one arriving transport on one route. FIXME
	public static Vector parseArriving(String response, Vector routes) {
		try {
			Vector result = new Vector(); //Vector<Arriving>

			JSONTokener jt = new JSONTokener(response);
			JSONObject answer = new JSONObject(jt);

			JSONArray aaData = answer.getJSONArray("aaData");
			for (int i = 0; i < aaData.length(); i++) {
				JSONArray record = aaData.getJSONArray(i);

				String routeNumber = record.getString(1);
				int minutesToArrive = record.getInt(2);
				
				Route route = null;
				for(Enumeration e = routes.elements(); e.hasMoreElements(); ){
					Route item = (Route) e.nextElement();
					if(item.getRouteNumber().equals(routeNumber)){
						route = item;
						break;
					}
				}
				
				Arriving arriving = new Arriving(route, minutesToArrive);
				result.addElement(arriving);
			}
			return result;
		} catch (JSONException e) {
			return null;
		}
	}
}