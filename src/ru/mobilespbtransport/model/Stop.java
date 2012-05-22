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
	private final TransportType transportType;
	private final int id;
	private Vector routesId = new Vector(); //Vector<Int(id)>
	private Vector arriving = new Vector();
	private boolean isDirect;

	public Stop(String name, Coordinate coordinate, TransportType transportType, int id, boolean direct) {
		super(name, coordinate);
		this.transportType = transportType;
		this.id = id;
		isDirect = direct;
	}

	public Stop(String name, Coordinate coordinate, TransportType transportType, int id) {
		this(name, coordinate, transportType, id, false);
	}

	public void addRouteId(int routeId){
		Integer id = new Integer(routeId);
		if(!routesId.contains(id)){
			routesId.addElement(id);
		}
	}

	public void setDirect(boolean direct) {
		isDirect = direct;
	}

	public boolean isDirect() {
		return isDirect;
	}

	public TransportType getTransportType() {
		return transportType;
	}

	public Vector getRoutesId() {
		return routesId;
	}

	public void setRoutesId(Vector routesId) {
		this.routesId = routesId;
	}

	public void setArriving(Vector arriving) {
		this.arriving = arriving;
	}

	public int getId() {
		return id;
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

		if (id != stop.id) return false;

		return true;
	}

	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + id;
		return result;
	}

	public String toString() {
		return "Stop{" +
				"transportType=" + transportType +
				", id=" + id +
				", isDirect=" + isDirect +
				'}';
	}
}
