package ru.mobilespbtransport.model;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 04.05.12
 * Time: 19:04
 * To change this template use File | SettingsScreen | File Templates.
 */
public class Place {
	protected final String name;
	protected final double lat;
	protected final double lon;

	public Place(String name, double lat, double lon) {
		this.name = name;
		this.lat = lat;
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public String getName() {
		return name;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Place place = (Place) o;

		if (place.lat != lat) return false;
		if (place.lon != lon) return false;
		if (name != null ? !name.equals(place.name) : place.name != null) return false;

		return true;
	}

	public int hashCode() {
		int result;
		long temp;
		result = name != null ? name.hashCode() : 0;
		temp = lat != +0.0d ? Double.doubleToLongBits(lat) : 0L;
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = lon != +0.0d ? Double.doubleToLongBits(lon) : 0L;
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}
