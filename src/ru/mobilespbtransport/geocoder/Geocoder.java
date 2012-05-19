package ru.mobilespbtransport.geocoder;

import org.json.me.*;
import ru.mobilespbtransport.model.Place;
import ru.mobilespbtransport.network.HttpClient;
import ru.mobilespbtransport.network.URLEncoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 13:00
 * To change this template use File | Settings | File Templates.
 */
public class Geocoder {
	//TODO

	public static Vector getPlaces(String address) {
		String request = getUrlForGeocoding(address);
		System.out.println(request);
		String response;
		try {
			response = HttpClient.sendGET(request);
		} catch (IOException e) {
			return null;
		}
		System.out.println(response);
		try {
			Vector result = parseResponse(response);
			return result;
		} catch (JSONException e) {
			e.printStackTrace();  //TODO
			return null;
		}
	}

	private static String getUrlForGeocoding(String address) {
		String encodedAddress;
		encodedAddress = URLEncoder.encode(address);
		return
				"http://maps.googleapis.com/maps/api/geocode/json?" +
						"address=" + encodedAddress +
						"&bounds=59.661,29.518|60.270,30.757" +  //SPB only
						"&sensor=false" +
						"&language=ru";
	}


	private static Vector parseResponse(String response) throws JSONException {
		Vector result = new Vector();

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
	}
}
