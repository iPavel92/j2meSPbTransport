package ru.mobilespbtransport.model;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 19.05.12
 * Time: 17:03
 * To change this template use File | Settings | File Templates.
 */
public class Stop extends Place{
	private TransportType transportType;

	public Stop(String name, double lat, double lon, TransportType transportType) {
		super(name, lat, lon);
		this.transportType = transportType;
	}

	public TransportType getTransportType() {
		return transportType;
	}
}
