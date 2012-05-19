package ru.mobilespbtransport.screens;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
public class SearchPlaceScreen extends Form {
	private TextField address;
	private Command ok;
	private Command cancel;

	public SearchPlaceScreen() {
		super("Add stop");
		address = new TextField("Enter address:", "", 30, TextField.ANY);
		ok = new Command("OK", Command.OK, 1);
		cancel = new Command("Cancel", Command.CANCEL, 2);
		append(address);
		addCommand(ok);
		addCommand(cancel);
	}

	public Command getOk() {
		return ok;
	}

	public Command getCancel() {
		return cancel;
	}

	public String getAddress() {
		return address.getString();
	}
}
