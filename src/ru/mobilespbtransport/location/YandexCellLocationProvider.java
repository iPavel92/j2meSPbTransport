package ru.mobilespbtransport.location;

import ru.mobilespbtransport.model.Coordinate;
import ru.mobilespbtransport.network.HttpClient;

/**
 * Created with IntelliJ IDEA.
 * User: Павел
 * Date: 17.08.13
 * Time: 22:02
 * To change this template use File | Settings | File Templates.
 */
public class YandexCellLocationProvider implements CellLocationProvider {

	public Coordinate getLocation(CellData cellData) {
		try {
			String request = generateRequestUrl(cellData);

			String response = HttpClient.sendGET(request);
			if(response == null){
				return null;
			}

			return parseResponse(response);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Coordinate parseResponse(String response){
		try {
			int latBeginPos = response.indexOf("latitude=\"");
			if(latBeginPos == -1){
				return null;
			}
			int latEndPos = response.indexOf("\"", latBeginPos + 10);
			if(latEndPos == -1){
				return null;
			}
			String latitudeStr = response.substring(latBeginPos + 10, latEndPos);
			double latitude = Double.parseDouble(latitudeStr);

			int lonBeginPos = response.indexOf("longitude=\"");
			if(lonBeginPos == -1){
				return null;
			}
			int lonEndPos = response.indexOf("\"", lonBeginPos + 11);
			if(lonEndPos == -1){
				return null;
			}
			String longitudeStr = response.substring(lonBeginPos + 11, lonEndPos);
			double longitude = Double.parseDouble(longitudeStr);

			return new Coordinate(latitude, longitude);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String generateRequestUrl(CellData cellData){
		return "http://mobile.maps.yandex.net/cellid_location/?" +
				"cellid=" + cellData.getCellId() + "&" +
				"operatorid=" + cellData.getMnc() + "&" +
				"countrycode=" + cellData.getMcc() + "&" +
				"lac=" + cellData.getLac();
	}
}
