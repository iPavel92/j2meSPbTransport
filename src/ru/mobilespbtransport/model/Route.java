package ru.mobilespbtransport.model;

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

	public Route(TransportType transportType, String routeNumber, int id) {
		this.transportType = transportType;
		this.routeNumber = routeNumber;
		this.id = id;
	}

	public TransportType getTransportType() {
		return transportType;
	}

	public String getRouteNumber() {
		return routeNumber;
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
}
