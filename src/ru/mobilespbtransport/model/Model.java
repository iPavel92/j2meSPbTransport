package ru.mobilespbtransport.model;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 0:22
 * To change this template use File | SettingsScreen | File Templates.
 */
public class Model {
	private Coordinate coordinate;
	private boolean showBus = true;
	private boolean showTrolley = true;
	private boolean showTram = true;
	private Vector stops = new Vector();

	public Vector getStops() {
		return stops;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public boolean isShowBus() {
		return showBus;
	}

	public void setShowBus(boolean showBus) {
		this.showBus = showBus;
	}

	public boolean isShowTrolley() {
		return showTrolley;
	}

	public void setShowTrolley(boolean showTrolley) {
		this.showTrolley = showTrolley;
	}

	public boolean isShowTram() {
		return showTram;
	}

	public void setShowTram(boolean showTram) {
		this.showTram = showTram;
	}
}
