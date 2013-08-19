package ru.mobilespbtransport.view;

import ru.mobilespbtransport.controller.Controller;


import javax.microedition.lcdui.*;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 0:55
 * To change this template use File | SettingsScreen | File Templates.
 */
public class SearchPlaceMenu extends List implements CommandListener {
	private final Command backCommand = new Command("Назад", Command.CANCEL, 2);

	public SearchPlaceMenu() {
		super("Поиск", IMPLICIT);
		append("по адресу", ImagePool.getImage("/address.png"));
		append("по № маршрута", ImagePool.getImage("/transport.png"));
		append("по координатам", ImagePool.getImage("/place.png"));
		append("моё расположение (GPS)", ImagePool.getImage("/gps.png"));
		addCommand(backCommand);
		setCommandListener(this);
	}

	public void commandAction(Command command, Displayable displayable) {
		if (command == List.SELECT_COMMAND) {
			switch (getSelectedIndex()) {
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
					//if(Controller.isLocationSupported()){
					ScreenStack.push(Controller.getMapView());
					Controller.locateMe();
					/*} else {
						ScreenStack.showAlert("Ваш телефон не поддерживает GPS");
					}      */
					break;
			}
			//TODO
		} else if (command == backCommand) {
			ScreenStack.pop();
		}
	}
}
