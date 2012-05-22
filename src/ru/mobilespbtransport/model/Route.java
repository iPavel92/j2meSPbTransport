package ru.mobilespbtransport.model;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 19.05.12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class Route {
	private final TransportType transportType;
	private final String routeNumber;
	private final int id;
	private Vector stopsId = new Vector(); //Vector<Int(id)>
	private boolean isStopsLoaded = false;

	public Route(TransportType transportType, String routeNumber, int id) {
		this.transportType = transportType;
		this.routeNumber = routeNumber;
		this.id = id;
	}

	public void addStopId(int stopId){
		Integer id = new Integer(stopId);
		if(!stopsId.contains(id)){
			stopsId.addElement(id);
		}
	}
	
	public TransportType getTransportType() {
		return transportType;
	}

	public boolean isStopsLoaded() {
		return isStopsLoaded;
	}

	public void setStopsLoaded(boolean stopsLoaded) {
		isStopsLoaded = stopsLoaded;
	}

	public String getRouteNumber() {
		return routeNumber;
	}

	public Vector getStopsId() {
		return stopsId;
	}

	public int getId() {
		return id;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Route)) return false;

		Route route = (Route) o;

		if (id != route.id) return false;

		return true;
	}

	public int hashCode() {
		return id;
	}

	public String toString() {
		return "Route{" +
				"transportType=" + transportType +
				", routeNumber='" + routeNumber + '\'' +
				", id=" + id +
				", isStopsLoaded=" + isStopsLoaded +
				'}';
	}
}
