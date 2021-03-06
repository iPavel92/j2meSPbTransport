package ru.mobilespbtransport.view;

import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.controller.GeoConverter;
import ru.mobilespbtransport.model.*;


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
public class MapScreen extends GameCanvas {
    protected MapScreen(boolean suppressKeyEvents) {
        super(suppressKeyEvents);
    }
	/*
    private Image map;
	private Image transportLayer;
	private Vector stops; //Vector<Stop>

	private final Command viewFavouritesCommand = new Command("Закладки", Command.ITEM, 0);
	private final Command addToFavourites = new Command("Добавить в закладки", Command.ITEM, 1);
	private final Command settings = new Command("Настройки", Command.ITEM, 2);
	private final Command updateCommand = new Command("Обновить", Command.ITEM, 3);
	private final Command backCommand = new Command("Назад", Command.CANCEL, 4);
	private final Command exitCommand = new Command("Выход", Command.EXIT, 5);

	private final static String LOADING = "Загрузка...";

	private final static int CURSOR_DELTA = 5;
	private final static int STOP_RADIUS = 6;
	private final static int SELECTED_STOP_RADIUS = 10;
	private final static int MAX_RADIUS_TO_SELECT_STOP = 30;
	Vector selectedStops;
	int[] stopsX;
	int[] stopsY;

	private Stop selectedStop = null;
	private int cursorX;
	private int cursorY;

	private long lastClickTime;
	private static final long MAX_DOUBLECLICK_TIME = 600;

	private final static int TOUCH_BORDER_TO_SLIDE = 50;

	private final static String LOCKED = "* для разблокировки";
	private boolean isLocked = false;
	private boolean isLoading = true;

	public void setLoading(boolean loading) {
		isLoading = loading;
	}

	public MapScreen() {
		super(false);
		setFullScreenMode(true);
		addCommand(viewFavouritesCommand);
		addCommand(addToFavourites);
		addCommand(settings);
		addCommand(updateCommand);
		addCommand(backCommand);
		addCommand(exitCommand);
		setCommandListener(this);
		cursorX = getWidth() / 2;
		cursorY = getHeight() / 2;
	}

	public void setStops(Vector stops) {
		this.stops = stops;
		calculateStopsCoordinates();
	}

	public boolean isLocked() {
		return isLocked;
	}
	
	private void calculateStopsCoordinates(){
		stopsX = new int[stops.size()];
		stopsY = new int[stops.size()];
		int i = 0;
		
		for (Enumeration e = stops.elements(); e.hasMoreElements(); i++) {
			Stop stop = (Stop) e.nextElement();

			stopsX[i] = GeoConverter.getXPixelFromCoordinate(Controller.getCurrentPlace().getCoordinate(),
					stop.getCoordinate(),
					getWidth(),
					Controller.getZoom());
			stopsY[i] = GeoConverter.getYPixelFromCoordinate(Controller.getCurrentPlace().getCoordinate(),
					stop.getCoordinate(),
					getHeight(),
					Controller.getZoom());
		}
	}

	private void calculateSelectedStops(){
		if(!Controller.isZoomedIn()){
			return;
		}
		selectedStops = new Vector();
		int i = 0;
		for (Enumeration e = stops.elements(); e.hasMoreElements(); i++) {
			Stop stop = (Stop) e.nextElement();
			int difX = cursorX - stopsX[i];
			int difY = cursorY - stopsY[i];
			if (Math.sqrt(difX * difX + difY * difY) < MAX_RADIUS_TO_SELECT_STOP && selectedStop == null) {
				selectedStops.addElement(stop);
			}
		}
	}

	public void paint(Graphics graphics) {
		graphics.setColor(0xffffff);
		graphics.fillRect(0, 0, getWidth(), getHeight());

		graphics.setColor(0x000000);

		if (isLocked) {
			graphics.drawString(LOCKED, getWidth() / 2, getHeight() / 2, Graphics.HCENTER | Graphics.TOP);
			return;
		}

		if (isLoading) {
			graphics.drawString(LOADING, getWidth() / 2, getHeight() / 2, Graphics.HCENTER | Graphics.TOP);
			return;
		}

		graphics.drawImage(map, 0, 0, Graphics.TOP | Graphics.LEFT);

		if (Controller.isZoomedIn()) {
			if (stops != null) {
				int i = 0;
				for (Enumeration e = stops.elements(); e.hasMoreElements(); i++) {
					Stop stop = (Stop) e.nextElement();

					int stopX = stopsX[i];
					int stopY = stopsY[i];

					//selection
					graphics.setColor(0x000000);
					if(selectedStops != null && selectedStops.contains(stop)){
						graphics.fillArc(stopX - SELECTED_STOP_RADIUS, stopY - SELECTED_STOP_RADIUS, 2 * SELECTED_STOP_RADIUS, 2 * SELECTED_STOP_RADIUS, 0, 360);
					}

					//border
					graphics.fillArc(stopX - STOP_RADIUS - 1, stopY - STOP_RADIUS - 1, 2 * STOP_RADIUS + 2, 2 * STOP_RADIUS + 2, 0, 360);

					//filling
					graphics.setColor(stop.getTransportType().getColor());
					graphics.fillArc(stopX - STOP_RADIUS, stopY - STOP_RADIUS, 2 * STOP_RADIUS, 2 * STOP_RADIUS, 0, 360);
				}
			}

			graphics.setColor(0x000000);
			graphics.drawArc(cursorX - MAX_RADIUS_TO_SELECT_STOP, cursorY - MAX_RADIUS_TO_SELECT_STOP, 2*MAX_RADIUS_TO_SELECT_STOP, 2*MAX_RADIUS_TO_SELECT_STOP, 0, 360);
		} else {
			if (transportLayer != null) {
				graphics.drawImage(transportLayer, 0, 0, Graphics.TOP | Graphics.LEFT);
			}

			graphics.setColor(0x000000);
			int borderWidth = GeoConverter.getBorderWidth(getWidth(), Controller.DEFAULT_ZOOM, Controller.SELECT_STOP_ZOOM);
			int borderHeight = GeoConverter.getBorderHeight(getHeight(), Controller.DEFAULT_ZOOM, Controller.SELECT_STOP_ZOOM);
			graphics.drawRect(cursorX - borderWidth / 2, cursorY - borderHeight / 2, borderWidth, borderHeight);
		}
	}

	public void setMap(Image map) {
		this.map = map;
		if(map != null){
			setLoading(false);
		}
	}

	public void setTransportLayer(Image transportLayer) {
		this.transportLayer = transportLayer;
	}

	protected void keyPressed(int keyCode) {
		if (isLocked) {
			if (keyCode == KEY_STAR) {
				isLocked = false;
				repaint();
				return;
			} else {
				return;
			}
		}
		switch (keyCode) {
			case KEY_NUM2:
				Controller.moveMapUp();
				return;
			case KEY_NUM4:
				Controller.moveMapLeft();
				return;
			case KEY_NUM8:
				Controller.moveMapDown();
				return;
			case KEY_NUM6:
				Controller.moveMapRight();
				return;
			case KEY_NUM5:
				update();
				return;
			case KEY_STAR:
				isLocked = true;
				repaint();
				return;
		}
		int gameAction = getGameAction(keyCode);
		switch (gameAction) {
			case FIRE:
				if (Controller.isZoomedIn()) {
					if (selectedStops.size() > 0) {
						ArrivingScreen arrivingScreen = new ArrivingScreen(new StopsGroup(selectedStops));
						ScreenStack.push(arrivingScreen);
						return;
					}
				} else {
					map = null;
					Controller.zoomIn();
				}
				break;
			case UP:
				if (cursorY > 0) {
					cursorY -= CURSOR_DELTA;
				} else {
					Controller.moveMapUp();
					cursorY = getHeight();
				}
				calculateSelectedStops();
				break;
			case DOWN:
				if (cursorY < getHeight()) {
					cursorY += CURSOR_DELTA;
				} else {
					Controller.moveMapDown();
					cursorY = 0;
				}
				calculateSelectedStops();
				break;
			case LEFT:
				if (cursorX > 0) {
					cursorX -= CURSOR_DELTA;
				} else {
					Controller.moveMapLeft();
					cursorX = getWidth();
				}
				calculateSelectedStops();
				break;
			case RIGHT:
				if (cursorX < getWidth()) {
					cursorX += CURSOR_DELTA;
				} else {
					Controller.moveMapRight();
					cursorX = 0;
				}
				calculateSelectedStops();
				break;
		}
		repaint();
	}

	protected void keyRepeated(int i) {
		keyPressed(i);
	}

	protected void pointerPressed(int x, int y) {
		if (isLocked) {
			return;
		}
		if (!Controller.isZoomedIn()) {
			if (System.currentTimeMillis() - lastClickTime < MAX_DOUBLECLICK_TIME) {
				cursorX = x;
				cursorY = y;
				map = null;
				Controller.zoomIn();
			} else {
				lastClickTime = System.currentTimeMillis();
			}
		} else {
			if (System.currentTimeMillis() - lastClickTime < MAX_DOUBLECLICK_TIME) {
				if (selectedStops.size() > 0) {
					ArrivingScreen arrivingScreen = new ArrivingScreen(new StopsGroup(selectedStops));
					ScreenStack.push(arrivingScreen);
					Controller.updateArrivingScreen(selectedStop, arrivingScreen);
				}
			} else {
				cursorX = x;
				cursorY = y;
				calculateSelectedStops();
				lastClickTime = System.currentTimeMillis();
				repaint();
			}
		}
		if (x < TOUCH_BORDER_TO_SLIDE) {
			Controller.moveMapLeft();
		} else if (x > getWidth() - TOUCH_BORDER_TO_SLIDE) {
			Controller.moveMapRight();
		} else if (y < TOUCH_BORDER_TO_SLIDE) {
			Controller.moveMapUp();
		} else if (y > getHeight() - TOUCH_BORDER_TO_SLIDE) {
			Controller.moveMapDown();
		}
	}

	public int getCursorX() {
		return cursorX;
	}

	public int getCursorY() {
		return cursorY;
	}

	private void update() {
		if (!Controller.isZoomedIn()) {
			Controller.loadTransportLayer();
		}
	}

	public void commandAction(Command command, Displayable displayable) {
		if (command == settings) {
			ScreenStack.push(new SettingsScreen());
		} else if (command == updateCommand) {
			update();
		} else if (command == viewFavouritesCommand) {
			Controller.zoomOut();
			ScreenStack.push(Controller.getFavouritesScreen());
		} else if (command == addToFavourites) {
			ScreenStack.push(new AddToFavouritesScreen(Controller.getCurrentPlace()));
		} else if (command == exitCommand) {
			Controller.exit();
		} else if (command == backCommand) {
			Controller.zoomOut();
			ScreenStack.pop();
		}
	}            */
}


