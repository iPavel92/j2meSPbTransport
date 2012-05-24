package ru.mobilespbtransport.view;

import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.model.*;


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
	private final Command settings = new Command("Настройки", Command.ITEM, 2);
	private final Command exitCommand = new Command("Выход", Command.EXIT, 3);

	private Vector favourites; //Vector<Favourite>

	public FavouritesScreen() {
		super("Закладки", IMPLICIT);

		addCommand(searchCommand);
		addCommand(deletePlaceCommand);
		addCommand(settings);
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
			if (item instanceof StopsGroup) {
				StopsGroup stopsGroup = (StopsGroup) item;
				if(stopsGroup.getStops().size() == 1){
					append(stopsGroup.getName(), ((Stop) stopsGroup.getStops().elementAt(0)).getTransportType().getIcon());
				} else {
					Image ico = ImagePool.getImage("/transport.png");
					append(stopsGroup.getName(), ico);
				}
			} else if (item instanceof Place) {
				Place place = (Place) item;
				Image ico = ImagePool.getImage("/place.png");
				append(place.getName(), ico);
			} else if (item instanceof Route) {
				Route route = (Route) item;
				append(route.getName(), route.getTransportType().getIcon());
			}
		}
	}

	public void commandAction(Command command, Displayable displayable) {
		if (command == List.SELECT_COMMAND) {
			if(getSelectedIndex() < 0){
				return;
			}
			Object obj = favourites.elementAt(getSelectedIndex());
			if (obj instanceof StopsGroup) {
				StopsGroup stops = (StopsGroup) obj;
				ArrivingScreen arrivingScreen = new ArrivingScreen(stops);
				ScreenStack.push(arrivingScreen);
				Controller.updateArrivingScreen(arrivingScreen.getCurrentStop(), arrivingScreen);
			} else if (obj instanceof Place) {
				Controller.setCurrentPlace((Place) obj);
				ScreenStack.push(Controller.getMapScreen());
			} else if (obj instanceof Route){
				Controller.findStops(Controller.getRoute(((Route)obj).getId()));
			}
		} else if (command == searchCommand) {
			ScreenStack.push(new SearchPlaceMenu());
		} else if (command == deletePlaceCommand) {
			Object obj = favourites.elementAt(getSelectedIndex());
			Controller.removeFavourite((Favourite) obj);
		} else if (command == exitCommand) {
			Controller.exit();
		} else if(command == settings){
			ScreenStack.push(new SettingsScreen());
		}
	}
}
