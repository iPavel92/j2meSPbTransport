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

	/*public static Vector getPlaces(String address) {
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
	}   */




}
