package ru.mobilespbtransport.model;

import java.util.Enumeration;
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
	private Vector routes = new Vector();
	private Vector arriving = new Vector();

	public Stop(String name, double lat, double lon, TransportType transportType) {
		super(name, lat, lon);
		this.transportType = transportType;
	}

	public TransportType getTransportType() {
		return transportType;
	}

	public Vector getRoutes() {
		return routes;
	}

	public void setRoutes(Vector routes) {
		this.routes = routes;
	}

	public Vector getArriving() {
		return arriving;
	}

	public void setArriving(Vector arriving) {
		this.arriving = arriving;
	}
	
	public Arriving getArriving(Route route){
		for(Enumeration e = arriving.elements(); e.hasMoreElements(); ){
			Arriving item = (Arriving) e.nextElement();
			if(item.getRoute().equals(route)){
				return item;
			}
		}
		return null;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Stop)) return false;
		if (!super.equals(o)) return false;

		Stop stop = (Stop) o;

		if (transportType != null ? !transportType.equals(stop.transportType) : stop.transportType != null)
			return false;

		return true;
	}

	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (transportType != null ? transportType.hashCode() : 0);
		return result;
	}
}
