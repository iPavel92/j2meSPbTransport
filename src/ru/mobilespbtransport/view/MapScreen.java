package ru.mobilespbtransport.view;

import ru.mobilespbtransport.Controller;
import ru.mobilespbtransport.util.Util;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.GameCanvas;

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

	private final Command viewPlacesCommand = new Command(Util.convertToUtf8("Закладки"), Command.ITEM, 0);
	private final Command addToFavourites = new Command(Util.convertToUtf8("Добавить в закладки"), Command.ITEM, 1);
	private final Command settings = new Command(Util.convertToUtf8("Настройки"), Command.ITEM, 2);
	private final Command updateCommand = new Command(Util.convertToUtf8("Обновить"), Command.ITEM, 3);
	private final Command backCommand = new Command(Util.convertToUtf8("Назад"), Command.CANCEL, 4);
	private final Command exitCommand = new Command(Util.convertToUtf8("Выход"), Command.EXIT, 5);

	private final static String LOADING = Util.convertToUtf8("Загрузка...");
	
	public MapScreen() {
		super(true);
		setFullScreenMode(true);
		addCommand(viewPlacesCommand);
		addCommand(addToFavourites);
		addCommand(settings);
		addCommand(updateCommand);
		addCommand(backCommand);
		addCommand(exitCommand);
		setCommandListener(this);
	}

	public void paint(Graphics graphics) {
		graphics.setColor(0xffffff);
		graphics.fillRect(0, 0, getWidth(), getHeight());
		
		graphics.setColor(0x000000);
		graphics.drawString(LOADING, getWidth(), getHeight(), Graphics.HCENTER | Graphics.TOP);
		
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
		} else if(command == backCommand){
			ScreenStack.pop();
		}
	}
}

