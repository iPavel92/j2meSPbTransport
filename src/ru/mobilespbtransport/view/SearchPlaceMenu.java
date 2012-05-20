package ru.mobilespbtransport.view;

import ru.mobilespbtransport.Controller;
import ru.mobilespbtransport.util.Util;

import javax.microedition.lcdui.*;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 0:55
 * To change this template use File | SettingsScreen | File Templates.
 */
public class SearchPlaceMenu extends List implements CommandListener {
	private final Command backCommand = new Command(Util.convertToUtf8("Назад"), Command.CANCEL, 2);

	public SearchPlaceMenu() {
		super(Util.convertToUtf8("Поиск"), IMPLICIT);
		try{
			append(Util.convertToUtf8("по адресу"), Image.createImage("/address.png"));
			append(Util.convertToUtf8("по № маршрута"), Image.createImage("/transport.png"));
			append(Util.convertToUtf8("по координатам"), Image.createImage("/place.png"));
			append(Util.convertToUtf8("моё расположение (GPS)"), Image.createImage("/gps.png"));
		} catch (IOException e) {
			//TODO
			e.printStackTrace();
		}
		addCommand(backCommand);
		setCommandListener(this);
	}

	public void commandAction(Command command, Displayable displayable) {
		if (command == List.SELECT_COMMAND) {
			switch(getSelectedIndex()){
				case 0: //address
					ScreenStack.push(new SearchAddressScreen());
					break;
				case 1: //route
					ScreenStack.push(new SearchRouteScreen());
					break;
				case 2: //coordinates
					ScreenStack.push(new SearchCoordinateScreen());
					break;
				case 3: //gps
					if(Controller.isLocationSupported()){
						Controller.locateMe();
						ScreenStack.push(Controller.getMapScreen());
					} else {
						ScreenStack.showAlert(Util.convertToUtf8("Ваш телефон не поддерживает GPS"));
					}
					break;
			}
			//TODO
		} else if (command == backCommand) {
			ScreenStack.pop();
		}
	}
}
