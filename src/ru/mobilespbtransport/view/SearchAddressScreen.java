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
public class SearchAddressScreen extends Form implements CommandListener{
	private TextField address = new TextField("Введите адрес:", "", 30, TextField.ANY);
	private Command ok = new Command("Найти", Command.OK, 1);
	private Command cancel = new Command("Назад", Command.CANCEL, 2);

	public SearchAddressScreen() {
		super("Поиск места");

		append(address);
		addCommand(ok);
		addCommand(cancel);
		setCommandListener(this);
	}

	public void commandAction(Command command, Displayable displayable) {
		if(command == ok){
			new Thread() {
				public void run() {
					Controller.findPlaces(address.getString());
				}
			}.start();
		} else if(command == cancel){
			ScreenStack.pop();
		}
	}
}
