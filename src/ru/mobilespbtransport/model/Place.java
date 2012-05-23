package ru.mobilespbtransport.model;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 04.05.12
 * Time: 19:04
 * To change this template use File | SettingsScreen | File Templates.
 */
public class Place implements Favourite {
	protected String name;
	protected Coordinate coordinate;

	public Place(String name, Coordinate coordinate) {
		this.name = name;
		this.coordinate = coordinate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Place)) return false;

		Place place = (Place) o;

		if (coordinate != null ? !coordinate.equals(place.coordinate) : place.coordinate != null) return false;
		if (name != null ? !name.equals(place.name) : place.name != null) return false;

		return true;
	}

	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (coordinate != null ? coordinate.hashCode() : 0);
		return result;
	}
}
