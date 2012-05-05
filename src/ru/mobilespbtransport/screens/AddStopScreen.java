package ru.mobilespbtransport.screens;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 0:43
 * To change this template use File | SettingsScreen | File Templates.
 */
public class AddStopScreen extends Form {
	private TextField lat, lon, name;
	private Command ok;
	private Command cancel;

	public AddStopScreen() {
		super("Add stop");
		name = new TextField("Name:", "", 30, TextField.ANY);
		lat = new TextField("Lat:", "", 30, TextField.ANY);
		lon = new TextField("Lon:", "", 30, TextField.ANY);
		ok = new Command("OK", Command.OK, 1);
		cancel = new Command("Cancel", Command.CANCEL, 2);
		append(name);
		append(lat);
		append(lon);
		addCommand(ok);
		addCommand(cancel);
	}

	public Command getOk() {
		return ok;
	}

	public Command getCancel() {
		return cancel;
	}
	
	public String getName(){
		return name.getString();
	}
	
	public String getLat(){
		return lat.getString();
	}

	public String getLon(){
		return lon.getString();
	}
}
