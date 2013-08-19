package ru.mobilespbtransport.model;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 0:22
 * To change this template use File | SettingsScreen | File Templates.
 */
public class Model {
	private boolean showBus = true;
	private boolean showTrolley = true;
	private boolean showTram = true;
	private boolean useAutoUpdate = false;
	private Vector favourites = new Vector(); //Vector<Favourite>
	private Hashtable stops = new Hashtable(); //Hashtable<Int(id)->Stop>
	private Hashtable routes = new Hashtable(); //Hashtable<Int(id)->Route>
    private Coordinate lastCoordinate;
    private int lastZoom;

    public Vector getFavourites() {
		return favourites;
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

	public boolean isUseAutoUpdate() {
		return useAutoUpdate;
	}

	public void setUseAutoUpdate(boolean useAutoUpdate) {
		this.useAutoUpdate = useAutoUpdate;
	}

	public Hashtable getStops() {
		return stops;
	}

	public Hashtable getRoutes() {
		return routes;
	}

    public void setLastCoordinate(Coordinate lastCoordinate) {
        this.lastCoordinate = lastCoordinate;
    }

    public Coordinate getLastCoordinate() {
        return lastCoordinate;
    }

    public int getLastZoom() {
        return lastZoom;
    }

    public void setLastZoom(int lastZoom) {
        this.lastZoom = lastZoom;
    }
}
