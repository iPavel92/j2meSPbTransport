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
}
