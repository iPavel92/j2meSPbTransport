package ru.mobilespbtransport.screens;

import ru.mobilespbtransport.Controller;
import ru.mobilespbtransport.geocoder.Geocoder;
import ru.mobilespbtransport.model.Place;
import ru.mobilespbtransport.util.Util;

import javax.microedition.lcdui.*;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
public class AddToFavouritesScreen extends Form implements CommandListener {
	private TextField placeName;
	private Command ok = new Command(Util.convertToUtf8("Добавить"), Command.OK, 1);
	private Command cancel = new Command(Util.convertToUtf8("Назад"), Command.CANCEL, 2);
	private final Place place;

	public AddToFavouritesScreen(Place place) {
		super(Util.convertToUtf8("Добавить в закладки"));
		this.place = place;
		placeName = new TextField(Util.convertToUtf8("Название:"), place.getName(), 200, TextField.ANY);
		append(placeName);
		addCommand(ok);
		addCommand(cancel);
		setCommandListener(this);
	}

	public void commandAction(Command command, Displayable displayable) {
		if (command == ok) {
			Place placeToAdd = new Place(placeName.getString(), place.getLat(), place.getLon());
			Controller.addPlace(placeToAdd);
			ScreenStack.pop();
		} else if (command == cancel) {
			ScreenStack.pop();
		}
	}
}
