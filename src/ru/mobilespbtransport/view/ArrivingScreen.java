package ru.mobilespbtransport.view;

import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.model.*;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.GameCanvas;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 04.05.12
 * Time: 20:44
 * To change this template use File | SettingsScreen | File Templates.
 */
public class ArrivingScreen extends GameCanvas implements CommandListener {
	private final Command viewPlacesCommand = new Command("Закладки", Command.ITEM, 0);
	private final Command addToFavourites = new Command("Добавить в закладки", Command.ITEM, 1);
	private final Command updateCommand = new Command("Обновить", Command.ITEM, 2);
	private final Command showOnMap = new Command("На карте", Command.ITEM, 3);
	private final Command showRoute = new Command("Посмотреть маршрут", Command.ITEM, 4);
	private final Command backCommand = new Command("Назад", Command.CANCEL, 5);
	private final Command exitCommand = new Command("Выход", Command.EXIT, 6);

	private final Stop stop;
	private final Image arrivingImage;
	private int selectedIndex = 0;

	private final static int TOUCH_BORDER_TO_SLIDE = 50;

	public ArrivingScreen(Stop stop) {
		super(false);
		setFullScreenMode(true);
		this.stop = stop;
		arrivingImage = stop.getTransportType().getArrivingImage();
		addCommand(viewPlacesCommand);
		addCommand(addToFavourites);
		addCommand(updateCommand);
		addCommand(showOnMap);
		addCommand(backCommand);
		addCommand(exitCommand);
		setCommandListener(this);
	}

	public Stop getStop() {
		return stop;
	}

	public void paint(Graphics graphics) {
		final int ELEMENTS_PADDING = 3;
		final int FONT_HEIGHT = Font.getDefaultFont().getHeight();

		//background
		graphics.setColor(0xF3C854);
		graphics.fillRect(0, 0, getWidth(), getHeight());

		//transport type image
		graphics.setColor(stop.getTransportType().getColor());
		graphics.fillRect(ELEMENTS_PADDING, ELEMENTS_PADDING, getWidth() - 2 * ELEMENTS_PADDING, 4 * ELEMENTS_PADDING + FONT_HEIGHT + arrivingImage.getHeight());
		graphics.drawImage(arrivingImage, ELEMENTS_PADDING, 4 * ELEMENTS_PADDING + FONT_HEIGHT, Graphics.LEFT | Graphics.TOP);

		//stop name
		graphics.setColor(0xFFFFFF);
		graphics.fillRect(2 * ELEMENTS_PADDING, 2 * ELEMENTS_PADDING, getWidth() - 4 * ELEMENTS_PADDING, FONT_HEIGHT + 2 * ELEMENTS_PADDING);

		graphics.setColor(0x1C2125);
		String stopName = stop.getName();
		Font.getDefaultFont().stringWidth(stopName);
		int maxLength = stopName.length();
		while (Font.getDefaultFont().substringWidth(stopName, 0, maxLength) > getWidth() - 4 * ELEMENTS_PADDING) {
			maxLength--;
		}
		if (maxLength < stopName.length()) {
			stopName = stopName.substring(0, maxLength - 3) + "...";
		}
		graphics.drawString(stopName, getWidth() / 2, 3 * ELEMENTS_PADDING, Graphics.HCENTER | Graphics.TOP);

		//arriving table
		final int ARRIVING_X_COL1 = ELEMENTS_PADDING;
		final int ARRIVING_X_COL2 = ARRIVING_X_COL1 + ELEMENTS_PADDING + 35;
		final int ARRIVING_X_COL3 = ARRIVING_X_COL2 + ELEMENTS_PADDING + 50;
		final int ARRIVING_Y = 5 * ELEMENTS_PADDING + FONT_HEIGHT + arrivingImage.getHeight();

		if (stop.getRoutes() == null) {
			graphics.drawString("Загрузка...", getWidth() / 2, getHeight() / 2, Graphics.HCENTER | Graphics.TOP);
			return;
		} else {
			//selection
			graphics.setColor(0xD0AC4B);
			graphics.fillRect(ARRIVING_X_COL1 - 1, ARRIVING_Y + 1, getWidth() - ARRIVING_X_COL1 - 1, FONT_HEIGHT + 2);
		}

		graphics.setColor(0x1C2125);
		graphics.drawLine(ARRIVING_X_COL2, ARRIVING_Y + ELEMENTS_PADDING, ARRIVING_X_COL2, getHeight() - ELEMENTS_PADDING);
		graphics.drawLine(ARRIVING_X_COL3, ARRIVING_Y + ELEMENTS_PADDING, ARRIVING_X_COL3, getHeight() - ELEMENTS_PADDING);

		int yIndex = 0;
		for (int i = selectedIndex; i < stop.getRoutes().size(); ++i, yIndex++) {
			Route route = (Route) stop.getRoutes().elementAt(i);
			Arriving arriving = stop.getArriving(route);

			String routeNumber = route.getRouteNumber();
			String arrivingTime = arriving == null ? "  -" : arriving.getArrivingTime();
			String delay = arriving == null ? "  -" : "(через " + arriving.getMinutesToArrive() + " мин.)";

			int y = ARRIVING_Y + yIndex * (FONT_HEIGHT + ELEMENTS_PADDING);
			if (y > getHeight()) {
				break;
			}
			graphics.drawString(routeNumber, ARRIVING_X_COL2 / 2, y, Graphics.HCENTER | Graphics.TOP);
			graphics.drawString(arrivingTime, ARRIVING_X_COL2 + ELEMENTS_PADDING, y, Graphics.LEFT | Graphics.TOP);
			graphics.drawString(delay, ARRIVING_X_COL3 + ELEMENTS_PADDING, y, Graphics.LEFT | Graphics.TOP);
		}

		if (selectedIndex > 0) {
			graphics.drawLine(getWidth() - 10, ARRIVING_Y + 5, getWidth() - 15, ARRIVING_Y + 10);
			graphics.drawLine(getWidth() - 10, ARRIVING_Y + 5, getWidth() - 5, ARRIVING_Y + 10);
			graphics.drawLine(getWidth() - 10, ARRIVING_Y + 4, getWidth() - 15, ARRIVING_Y + 9);
			graphics.drawLine(getWidth() - 10, ARRIVING_Y + 4, getWidth() - 5, ARRIVING_Y + 9);
		}
		if (selectedIndex < stop.getRoutes().size()) {
			graphics.drawLine(getWidth() - 10, getHeight() - 5, getWidth() - 15, getHeight() - 10);
			graphics.drawLine(getWidth() - 10, getHeight() - 5, getWidth() - 5, getHeight() - 10);
			graphics.drawLine(getWidth() - 10, getHeight() - 4, getWidth() - 15, getHeight() - 9);
			graphics.drawLine(getWidth() - 10, getHeight() - 4, getWidth() - 5, getHeight() - 9);
		}
	}

	protected void keyPressed(int keyCode) {
		if (keyCode == KEY_NUM5) {
			Controller.updateArrivingScreen(stop, this);
		}
		int gameAction = getGameAction(keyCode);
		switch (gameAction) {
			case UP:
				slideUp();
				break;
			case DOWN:
				slideDown();
				break;
			case FIRE:
				if (stop.getRoutes() != null) {
					Controller.findStops((Route) stop.getRoutes().elementAt(selectedIndex));
				}
				break;
		}
	}

	protected void keyRepeated(int i) {
		keyPressed(i);
	}

	protected void pointerPressed(int x, int y) {
		if (y < TOUCH_BORDER_TO_SLIDE) {
			slideUp();
		} else if (y > getHeight() - TOUCH_BORDER_TO_SLIDE) {
			slideDown();
		}
	}

	private void slideUp() {
		if (selectedIndex > 0) {
			selectedIndex--;
			repaint();
		}
	}

	private void slideDown() {
		if (selectedIndex < stop.getRoutes().size() - 1) {
			selectedIndex++;
			repaint();
		}
	}

	public void commandAction(Command command, Displayable displayable) {
		if (command == updateCommand) {
			Controller.updateArrivingScreen(stop, this);
		} else if (command == viewPlacesCommand) {
			ScreenStack.push(Controller.getFavouritesScreen());
		} else if (command == addToFavourites) {
			ScreenStack.push(new AddToFavouritesScreen(stop));
		} else if (command == backCommand) {
			ScreenStack.pop();
		} else if (command == exitCommand) {
			Controller.exit();
		} else if (command == showOnMap) {
			Coordinate q = stop.getCoordinate();
			Place place = new Place(stop.getName(), stop.getCoordinate());
			Controller.setCurrentPlace(place);
			ScreenStack.push(Controller.getMapScreen());
		} else if (command == showRoute && stop.getRoutes() != null) {
			Controller.findStops((Route) stop.getRoutes().elementAt(selectedIndex));
		}
	}
}

