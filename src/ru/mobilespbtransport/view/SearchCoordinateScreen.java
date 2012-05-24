package ru.mobilespbtransport.view;

import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.model.Coordinate;
import ru.mobilespbtransport.model.Place;


import javax.microedition.lcdui.*;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 0:43
 * To change this template use File | SettingsScreen | File Templates.
 */
public class SearchCoordinateScreen extends Form implements CommandListener{
	private TextField lat, lon, name;
	private Command ok;
	private Command cancel;

	public SearchCoordinateScreen() {
		super("Посмотреть место");
		lat = new TextField("Широта (lat):", "", 30, TextField.DECIMAL);
		lon = new TextField("Долгота (lon):", "", 30, TextField.DECIMAL);
		ok = new Command("ОК", Command.OK, 1);
		cancel = new Command("Назад", Command.CANCEL, 2);
		append(lat);
		append(lon);
		addCommand(ok);
		addCommand(cancel);
		setCommandListener(this);
	}

	public void commandAction(Command command, Displayable displayable) {
		if(command == ok){
			try {
				double latValue = Double.parseDouble(lat.getString());
				double lonValue = Double.parseDouble(lon.getString());
				Place place = new Place("", new Coordinate(latValue, lonValue, Coordinate.WGS84));
				Controller.setCurrentPlace(place);
				ScreenStack.push(Controller.getMapScreen());
			} catch (NumberFormatException e) {
				ScreenStack.showAlert("Введены неправельные значения");
				e.printStackTrace();
			}
		} else if (command == cancel){
			ScreenStack.pop();
		}
	}
}
