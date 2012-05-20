package ru.mobilespbtransport.model;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 19.05.12
 * Time: 23:39
 * To change this template use File | Settings | File Templates.
 */
public class Arriving {
	private final Route route;
	private final int minutesToArrive;

	public Arriving(Route route, int minutesToArrive) {
		this.route = route;
		this.minutesToArrive = minutesToArrive;
	}

	public Route getRoute() {
		return route;
	}

	public int getMinutesToArrive() {
		return minutesToArrive;
	}
	
	public String getArrivingTime(){
		Calendar c = Calendar.getInstance();
		Date d = new Date();
		d.setTime(d.getTime() + minutesToArrive * 1000 * 60);
		c.setTime(d);
		String time = c.get(Calendar.HOUR_OF_DAY) + ":" +c.get(Calendar.MINUTE);
		return time;
	}
}
