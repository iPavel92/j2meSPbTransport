package ru.mobilespbtransport.view;

import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.model.Place;


import javax.microedition.lcdui.*;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 0:55
 * To change this template use File | SettingsScreen | File Templates.
 */
public class PlacesListScreen extends List implements CommandListener {
	private final Command backCommand = new Command("Назад", Command.CANCEL, 2);
	private Vector places;
	private boolean isLoaded = false;

	public PlacesListScreen() {
		this(new Vector());
	}

	public PlacesListScreen(Vector places) {
		super("Выберите место", IMPLICIT);
		setPlaces(places);
		addCommand(backCommand);
		setCommandListener(this);

		append("Загрузка...", ImagePool.getImage("/autoupdate.png"));
	}

	public void setPlaces(Vector places) {
		while (size() > 0) {
			delete(0);
		}
		this.places = places;
		if (places == null) {
			return;
		}
		isLoaded = true;
		Image placeIco = ImagePool.getImage("/place.png");

		for (int i = 0; i < places.size(); i++) {
			Place c = (Place) places.elementAt(i);
			append(c.getName(), placeIco);
		}
	}

	public void commandAction(Command command, Displayable displayable) {
		if (command == List.SELECT_COMMAND && isLoaded) {
			if (getSelectedIndex() < 0) {
				return;
			}
			Controller.setCurrentPlace((Place) places.elementAt(getSelectedIndex()));
			ScreenStack.push(Controller.getMapView());
		} else if (command == backCommand) {
			ScreenStack.pop();
		}
	}
}
