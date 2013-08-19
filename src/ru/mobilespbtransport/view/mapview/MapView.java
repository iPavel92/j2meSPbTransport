package ru.mobilespbtransport.view.mapview;

import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.controller.GeoConverter;
import ru.mobilespbtransport.model.Coordinate;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.GameCanvas;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 04.05.12
 * Time: 20:44
 * To change this template use File | SettingsScreen | File Templates.
 */
public class MapView extends GameCanvas implements CommandListener, ViewUpdater{
	private final Command exitCommand = new Command("Выход", Command.EXIT, 5);

    private static final int MOVE_DELTA_PX_ZOOM_GREATER_15 = 10;
    private static final int MOVE_DELTA_PX = 20;
	private static final int MAX_ZOOM = 18;
	private static final int MIN_ZOOM = 7;

	private Vector layers = new Vector(); //Vector<MapLayer>
	private Coordinate mapCenter;
	private int zoom = 13;

	public MapView() {
		super(false);
		setFullScreenMode(true);
		addCommand(exitCommand);
		setCommandListener(this);
	}

	public void addLayer(MapLayer mapLayer){
		mapLayer.init(this, getWidth(), getHeight());
		mapLayer.onMapMoved(mapCenter);
		mapLayer.onMapZoomed(zoom);
		layers.addElement(mapLayer);
	}

	protected void sizeChanged(int width, int height) {
		super.sizeChanged(width, height);
		for(Enumeration e = layers.elements(); e.hasMoreElements(); ){
			((MapLayer)e.nextElement()).onScreenSizeChanged(width, height);
		}
		repaint();
	}

	public void setMapCenter(Coordinate coordinate) {
		this.mapCenter = coordinate;
		for(Enumeration e = layers.elements(); e.hasMoreElements(); ){
			((MapLayer)e.nextElement()).onMapMoved(coordinate);
		}
		repaint();
	}

    public Coordinate getMapCenter() {
        return mapCenter;
    }

    public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		if(zoom < MIN_ZOOM || zoom > MAX_ZOOM){
			return;
		}
		this.zoom = zoom;
		for(Enumeration e = layers.elements(); e.hasMoreElements(); ){
			((MapLayer)e.nextElement()).onMapZoomed(zoom);
		}
	}

	public void paint(Graphics graphics) {
		graphics.setColor(0xD4D7E0);
		graphics.fillRect(0, 0, getWidth(), getHeight());

		if (mapCenter == null) {
			return;
		}

		for(Enumeration e = layers.elements(); e.hasMoreElements(); ){
			MapLayer layer = (MapLayer)e.nextElement();
            if(layer.isVisible()){
                layer.paint(graphics);
            }
		}
	}

	protected void keyPressed(int keyCode) {
		switch (keyCode) {
			case KEY_STAR:
				setZoom(zoom + 1);
				break;
			case KEY_POUND:
				setZoom(zoom - 1);
				break;
		}

		int gameAction = getGameAction(keyCode);
        int moveDelta = zoom > 15 ? MOVE_DELTA_PX_ZOOM_GREATER_15 : MOVE_DELTA_PX;
		switch (gameAction) {
			case FIRE:
                for(Enumeration e = layers.elements(); e.hasMoreElements(); ){
                    MapLayer layer = (MapLayer)e.nextElement();
                    if(layer.isVisible()){
                        layer.onAction();
                    }
                }
				break;
			case UP:
				setMapCenter(new Coordinate(mapCenter.getLat() + GeoConverter.getLatDiff(moveDelta, zoom), mapCenter.getLon()));
				break;
			case DOWN:
				setMapCenter(new Coordinate(mapCenter.getLat() - GeoConverter.getLatDiff(moveDelta, zoom), mapCenter.getLon()));
				break;
			case LEFT:
				setMapCenter(new Coordinate(mapCenter.getLat(), mapCenter.getLon() - GeoConverter.getLonDiff(moveDelta, zoom)));
				break;
			case RIGHT:
				setMapCenter(new Coordinate(mapCenter.getLat(), mapCenter.getLon() + GeoConverter.getLonDiff(moveDelta, zoom)));
				break;
		}
		repaint();
	}

	protected void keyRepeated(int i) {
		keyPressed(i);
	}

	private int oldPointerX;
	private int oldPointerY;
	protected void pointerPressed(int x, int y) {
		oldPointerX = x;
		oldPointerY = y;

        for(Enumeration e = layers.elements(); e.hasMoreElements(); ){
            MapLayer layer = (MapLayer)e.nextElement();
            if(layer.isVisible()){
                layer.onPointerPressed(x, y);
            }
        }

		repaint();
	}

	protected void pointerDragged(int x, int y) {
		int dx = x - oldPointerX;
		int dy = y - oldPointerY;
		oldPointerX = x;
		oldPointerY = y;
		setMapCenter(new Coordinate(
				mapCenter.getLat() + GeoConverter.getLatDiff(dy, zoom),
				mapCenter.getLon() - GeoConverter.getLonDiff(dx, zoom)));
	}

	protected void pointerReleased(int x, int y) {
		oldPointerX = x;
		oldPointerY = y;
	}

	public void commandAction(Command command, Displayable displayable) {
		if (command == exitCommand) {
			Controller.exit();
		}
	}

	public void updateView() {
		repaint();
	}
}


