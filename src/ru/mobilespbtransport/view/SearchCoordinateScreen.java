package ru.mobilespbtransport.view;

import ru.mobilespbtransport.Controller;
import ru.mobilespbtransport.model.Place;
import ru.mobilespbtransport.util.Util;

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
		super(Util.convertToUtf8("Посмотреть место"));
		name = new TextField(Util.convertToUtf8("Название"), "", 30, TextField.ANY);
		lat = new TextField(Util.convertToUtf8("Широта (lat):"), "", 30, TextField.DECIMAL);
		lon = new TextField(Util.convertToUtf8("Долгота (lon):"), "", 30, TextField.DECIMAL);
		ok = new Command(Util.convertToUtf8("ОК"), Command.OK, 1);
		cancel = new Command(Util.convertToUtf8("Назад"), Command.CANCEL, 2);
		append(name);
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
				Place place = new Place(name.getString(), latValue, lonValue);
				Controller.setCurrentPlace(place);
				ScreenStack.push(Controller.getMapScreen());
			} catch (NumberFormatException e) {
				//TODO
				e.printStackTrace();
			}
		} else if (command == cancel){
			ScreenStack.pop();
		}
	}
}
