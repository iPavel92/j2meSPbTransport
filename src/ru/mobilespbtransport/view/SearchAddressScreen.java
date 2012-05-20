package ru.mobilespbtransport.view;

import ru.mobilespbtransport.Controller;
import ru.mobilespbtransport.util.Util;

import javax.microedition.lcdui.*;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
public class SearchAddressScreen extends Form implements CommandListener{
	private TextField address = new TextField(Util.convertToUtf8("Введите адрес:"), "", 30, TextField.ANY);
	private Command ok = new Command(Util.convertToUtf8("Найти"), Command.OK, 1);
	private Command cancel = new Command(Util.convertToUtf8("Назад"), Command.CANCEL, 2);

	public SearchAddressScreen() {
		super(Util.convertToUtf8("Поиск места"));

		append(address);
		addCommand(ok);
		addCommand(cancel);
		setCommandListener(this);
	}

	public void commandAction(Command command, Displayable displayable) {
		if(command == ok){
			new Thread() {
				public void run() {
					PlacesList placesList = new PlacesList();
					ScreenStack.push(placesList);
					Controller.findPlaces(address.getString(), placesList);
				}
			}.start();
		} else if(command == cancel){
			ScreenStack.pop();
		}
	}
}