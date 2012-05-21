package ru.mobilespbtransport.view;

import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.model.Place;
import ru.mobilespbtransport.model.Stop;


import javax.microedition.lcdui.*;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 0:55
 * To change this template use File | SettingsScreen | File Templates.
 */
public class FavouritesScreen extends List implements CommandListener {
	private final Command searchCommand = new Command("Поиск места", Command.ITEM, 0);
	private final Command deletePlaceCommand = new Command("Удалить", Command.ITEM, 1);
	private final Command exitCommand = new Command("Выход", Command.EXIT, 2);

	private Vector favourites;

	public FavouritesScreen() {
		super("Закладки", IMPLICIT);

		addCommand(searchCommand);
		addCommand(deletePlaceCommand);
		addCommand(exitCommand);
		setCommandListener(this);

		update();
	}

	public void setFavourites(Vector favourites) {
		this.favourites = favourites;
	}

	public void update(){
		while (size() > 0) {
			delete(0);
		}

		if(favourites == null){
			return;
		}
		for (int i = 0; i < favourites.size(); i++) {
			Object item = favourites.elementAt(i);
			if (item instanceof Stop) {
				Stop stop = (Stop) item;
				append(stop.getName(), stop.getTransportType().getIcon());
			} else if (item instanceof Place) {
				Place c = (Place) item;
				Image ico = null;
				try {
					ico = Image.createImage("/place.png");
				} catch (IOException e) {
					//TODO
					e.printStackTrace();

				}
				append(c.getName(), ico);
			}
		}
	}

	public void commandAction(Command command, Displayable displayable) {
		if (command == List.SELECT_COMMAND) {
			Object obj = favourites.elementAt(getSelectedIndex());
			if (obj instanceof Stop) {
				Stop stop = (Stop) obj;
				//TODO
				ScreenStack.push(new ArrivingScreen(stop));
			} else if (obj instanceof Place) {
				Controller.setCurrentPlace((Place) obj);
				ScreenStack.push(Controller.getMapScreen());
			}
		} else if (command == searchCommand) {
			ScreenStack.push(new SearchPlaceMenu());
		} else if (command == deletePlaceCommand) {
			Object obj = favourites.elementAt(getSelectedIndex());
			if (obj instanceof Place) {
				Controller.removeFavourite((Place) obj);
			}
		} else if (command == exitCommand) {
			Controller.exit();
		}
	}
}
