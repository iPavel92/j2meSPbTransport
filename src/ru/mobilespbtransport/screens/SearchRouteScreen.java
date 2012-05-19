package ru.mobilespbtransport.screens;

import ru.mobilespbtransport.util.Util;

import javax.microedition.lcdui.*;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
public class SearchRouteScreen extends Form implements CommandListener{
	private TextField routeNumber = new TextField(Util.convertToUtf8("Введите номер маршрута:"), "", 5, TextField.ANY);
	private Command ok = new Command(Util.convertToUtf8("Найти"), Command.OK, 1);
	private Command cancel = new Command(Util.convertToUtf8("Назад"), Command.CANCEL, 2);

	public SearchRouteScreen() {
		super(Util.convertToUtf8("Поиск маршрута"));
		append(routeNumber);
		addCommand(ok);
		addCommand(cancel);
		setCommandListener(this);
	}

	public void commandAction(Command command, Displayable displayable) {
		if(command == ok){
			//TODO
		} else if (command == cancel){
			ScreenStack.pop();
		}
	}
}
