package ru.mobilespbtransport.screens;

import ru.mobilespbtransport.Controller;
import ru.mobilespbtransport.util.Util;

import javax.microedition.lcdui.*;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 04.05.12
 * Time: 20:44
 * To change this template use File | SettingsScreen | File Templates.
 */
public class MapScreen extends Canvas implements CommandListener {
	private Image map;
	private Image transportLayer;

	private final Command viewPlacesCommand = new Command(Util.convertToUtf8("Закладки"), Command.ITEM, 0);
	private final Command addToFavourites = new Command(Util.convertToUtf8("Добавить в закладки"), Command.ITEM, 1);
	private final Command settings = new Command(Util.convertToUtf8("Настройки"), Command.ITEM, 2);
	private final Command updateCommand = new Command(Util.convertToUtf8("Обновить"), Command.ITEM, 3);
	private final Command exitCommand = new Command(Util.convertToUtf8("Выход"), Command.EXIT, 4);

	public MapScreen() {
		addCommand(viewPlacesCommand);
		addCommand(addToFavourites);
		addCommand(settings);
		addCommand(updateCommand);
		addCommand(exitCommand);
		setCommandListener(this);
	}

	protected void paint(Graphics graphics) {
		graphics.setColor(0xffffff);
		graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
		if (map != null) {
			graphics.drawImage(map, 0, 0, Graphics.TOP | Graphics.LEFT);
		}
		if (transportLayer != null) {
			graphics.drawImage(transportLayer, 0, 0, Graphics.TOP | Graphics.LEFT);
		}
	}

	public void setMap(Image map) {
		this.map = map;
	}

	public void setTransportLayer(Image transportLayer) {
		this.transportLayer = transportLayer;
	}

	protected void keyPressed(int i) {
		if (i == Canvas.KEY_NUM5) {
			update();
		}
	}

	private void update(){
		Controller.loadTransportLayer();
	}

	protected void pointerPressed(int i, int i1) {
		Controller.loadTransportLayer();
	}

	public void commandAction(Command command, Displayable displayable) {
		if (command == settings) {
		   ScreenStack.push(new SettingsScreen());
		} else if (command == updateCommand) {
			update();
		} else if (command == viewPlacesCommand) {
			ScreenStack.push(Controller.getFavouritesList());
		} else if(command == addToFavourites){
			ScreenStack.push(new AddToFavouritesScreen(Controller.getCurrentPlace()));
		} else if (command == exitCommand) {
			Controller.exit();
		}
	}
}

