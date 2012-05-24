package ru.mobilespbtransport.model;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 24.05.12
 * Time: 1:12
 * To change this template use File | Settings | File Templates.
 */
public class StopsGroup implements Favourite{
	private String name;
	private Vector stops = new Vector(); //Vector<Stop>

	public StopsGroup(String name, Vector stops) {
		this.name = name;
		this.stops = stops;
	}

	public StopsGroup(Vector stops) {
		name = ((Stop)stops.elementAt(0)).getName();
		this.stops = stops;
	}

	public StopsGroup(Stop stop) {
		name = stop.getName();
		stops.addElement(stop);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Vector getStops() {
		return stops;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof StopsGroup)) return false;

		StopsGroup that = (StopsGroup) o;

		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (stops != null ? !stops.equals(that.stops) : that.stops != null) return false;

		return true;
	}

	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (stops != null ? stops.hashCode() : 0);
		return result;
	}
}
