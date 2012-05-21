package ru.mobilespbtransport.view;

import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.model.Place;
import ru.mobilespbtransport.model.Stop;

import javax.microedition.lcdui.*;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
public class AddToFavouritesScreen extends Form implements CommandListener {
	private TextField placeName;
	private Command ok = new Command("Добавить", Command.OK, 1);
	private Command cancel = new Command("Назад", Command.CANCEL, 2);
	private final Place place;

	public AddToFavouritesScreen(Place place) {
		super("Добавить в закладки");
		this.place = place;
		placeName = new TextField("Название:", place.getName(), 200, TextField.ANY);
		append(placeName);
		addCommand(ok);
		addCommand(cancel);
		setCommandListener(this);
	}

	public void commandAction(Command command, Displayable displayable) {
		if (command == ok) {
			Place placeToAdd = place;
			if(!(place instanceof Stop)){
				placeToAdd = new Place(placeName.getString(), place.getCoordinate());
			}
			Controller.addFavourite(placeToAdd);
			ScreenStack.pop();
		} else if (command == cancel) {
			ScreenStack.pop();
		}
	}
}
