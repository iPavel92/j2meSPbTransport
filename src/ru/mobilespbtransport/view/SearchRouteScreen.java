package ru.mobilespbtransport.view;

import ru.mobilespbtransport.controller.Controller;


import javax.microedition.lcdui.*;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
public class SearchRouteScreen extends Form implements CommandListener{
	private TextField routeNumber = new TextField("Введите номер маршрута:", "", 3, TextField.NUMERIC);
	private Command ok = new Command("Найти", Command.OK, 1);
	private Command cancel = new Command("Назад", Command.CANCEL, 2);

	public SearchRouteScreen() {
		super("Поиск маршрута");
		append(routeNumber);
		addCommand(ok);
		addCommand(cancel);
		setCommandListener(this);
	}

	public void commandAction(Command command, Displayable displayable) {
		if(command == ok){
			Controller.findRoutes(routeNumber.getString());
		} else if (command == cancel){
			ScreenStack.pop();
		}
	}
}
