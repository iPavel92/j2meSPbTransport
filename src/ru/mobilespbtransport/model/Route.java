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

	public Route(TransportType transportType, String routeNumber) {
		this.transportType = transportType;
		this.routeNumber = routeNumber;
	}

	public TransportType getTransportType() {
		return transportType;
	}

	public String getRouteNumber() {
		return routeNumber;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Route route = (Route) o;

		if (routeNumber != null ? !routeNumber.equals(route.routeNumber) : route.routeNumber != null) return false;
		if (transportType != null ? !transportType.equals(route.transportType) : route.transportType != null)
			return false;

		return true;
	}

	public int hashCode() {
		int result = transportType != null ? transportType.hashCode() : 0;
		result = 31 * result + (routeNumber != null ? routeNumber.hashCode() : 0);
		return result;
	}
}
