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
public class MapScreen extends GameCanvas implements CommandListener {
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
	private final static int CROSS_SISE = 10;
	private final static int STOP_RADIUS = 6;
	private final static int SELECTED_STOP_RADIUS = 10;
	private final static int MAX_RADIUS_TO_SELECT_STOP = 15;

	private Stop selectedStop = null;
	private int cursorX;
	private int cursorY;

	private long lastClickTime;
	private static final long MAX_DOUBLECLICK_TIME = 600;

	private final static int TOUCH_BORDER_TO_SLIDE = 50;

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
	}

	public void paint(Graphics graphics) {
		graphics.setColor(0xffffff);
		graphics.fillRect(0, 0, getWidth(), getHeight());

		graphics.setColor(0x000000);
		graphics.drawString(LOADING, getWidth(), getHeight(), Graphics.HCENTER | Graphics.TOP);

		if (map != null) {
			graphics.drawImage(map, 0, 0, Graphics.TOP | Graphics.LEFT);
		}

		if (Controller.isZoomedIn()) {
			if (stops != null) {
				selectedStop = null; //yes, logic in paint o_O get mad!!!
				for (Enumeration e = stops.elements(); e.hasMoreElements(); ) {
					Stop stop = (Stop) e.nextElement();

					//pixel coordinates
					final int stopX = GeoConverter.getXPixelFromCoordinate(Controller.getCurrentPlace().getCoordinate(),
							stop.getCoordinate(),
							getWidth(),
							Controller.getZoom());
					final int stopY = GeoConverter.getYPixelFromCoordinate(Controller.getCurrentPlace().getCoordinate(),
							stop.getCoordinate(),
							getHeight(),
							Controller.getZoom());

					//selection
					int difX = cursorX - stopX;
					int difY = cursorY - stopY;
					if (Math.sqrt(difX * difX + difY * difY) < MAX_RADIUS_TO_SELECT_STOP && selectedStop == null) {
						selectedStop = stop;
						graphics.setColor(0x000000);
						graphics.fillArc(stopX - SELECTED_STOP_RADIUS, stopY - SELECTED_STOP_RADIUS, 2 * SELECTED_STOP_RADIUS, 2 * SELECTED_STOP_RADIUS, 0, 360);
					}

					//border
					graphics.setColor(0x000000);
					graphics.fillArc(stopX - STOP_RADIUS - 1, stopY - STOP_RADIUS - 1, 2 * STOP_RADIUS + 2, 2 * STOP_RADIUS + 2, 0, 360);

					//filling
					graphics.setColor(stop.getTransportType().getColor());
					graphics.fillArc(stopX - STOP_RADIUS, stopY - STOP_RADIUS, 2 * STOP_RADIUS, 2 * STOP_RADIUS, 0, 360);
				}
			}

			graphics.setColor(0x000000);
			graphics.drawLine(cursorX - CROSS_SISE, cursorY, cursorX + CROSS_SISE, cursorY);
			graphics.drawLine(cursorX, cursorY - CROSS_SISE, cursorX, cursorY + CROSS_SISE);
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
	}

	public void setTransportLayer(Image transportLayer) {
		this.transportLayer = transportLayer;
	}

	protected void keyPressed(int keyCode) {
		switch (keyCode) {
			case KEY_NUM2:
				Controller.moveMapUp();
				break;
			case KEY_NUM4:
				Controller.moveMapLeft();
				break;
			case KEY_NUM8:
				Controller.moveMapDown();
				break;
			case KEY_NUM6:
				Controller.moveMapRight();
				break;
			case KEY_NUM5:
				update();
				break;
		}
		int gameAction = getGameAction(keyCode);
		switch (gameAction) {
			case FIRE:
				if (Controller.isZoomedIn()) {
					if (selectedStop != null) {
						ArrivingScreen arrivingScreen = new ArrivingScreen(selectedStop);
						ScreenStack.push(arrivingScreen);
						Controller.updateArrivingScreen(selectedStop, arrivingScreen);
					}
				} else {
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
				break;
			case DOWN:
				if (cursorY < getHeight()) {
					cursorY += CURSOR_DELTA;
				} else {
					Controller.moveMapDown();
					cursorY = 0;
				}
				break;
			case LEFT:
				if (cursorX > 0) {
					cursorX -= CURSOR_DELTA;
				} else {
					Controller.moveMapLeft();
					cursorX = getWidth();
				}
				break;
			case RIGHT:
				if (cursorX < getWidth()) {
					cursorX += CURSOR_DELTA;
				} else {
					Controller.moveMapRight();
					cursorX = 0;
				}
				break;
		}
		repaint();
	}

	protected void keyRepeated(int i) {
		keyPressed(i);
	}

	protected void pointerPressed(int x, int y) {
		if (!Controller.isZoomedIn()) {
			if (System.currentTimeMillis() - lastClickTime < MAX_DOUBLECLICK_TIME) {
				cursorX = x;
				cursorY = y;
				Controller.zoomIn();
			} else {
				lastClickTime = System.currentTimeMillis();
			}
		} else {
			if (System.currentTimeMillis() - lastClickTime < MAX_DOUBLECLICK_TIME) {
				if (selectedStop != null) {
					ArrivingScreen arrivingScreen = new ArrivingScreen(selectedStop);
					ScreenStack.push(arrivingScreen);
					Controller.updateArrivingScreen(selectedStop, arrivingScreen);
				}
			} else {
				cursorX = x;
				cursorY = y;
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
	}
}


