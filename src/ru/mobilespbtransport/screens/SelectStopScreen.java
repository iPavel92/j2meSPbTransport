package ru.mobilespbtransport.screens;

import ru.mobilespbtransport.model.Coordinate;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 0:55
 * To change this template use File | SettingsScreen | File Templates.
 */
public class SelectStopScreen extends List {
	private Command select;
	private Command cancel;

	public SelectStopScreen(Vector v) {
		super("Select stop", IMPLICIT);
		select = new Command("Select", Command.OK, 1);
		cancel = new Command("Cancel", Command.CANCEL, 2);
		addCommand(select);
		addCommand(cancel);
		setValues(v);
	}
	
	public void setValues(Vector v){
		for(int i = 0; i<v.size(); i++){
			Coordinate c = (Coordinate)v.elementAt(i);
			append(c.getName(), null);
		}
	} 
	
	public int getSelected(){
		return getSelectedIndex();
	}

	public Command getSelect() {
		return select;
	}

	public Command getCancel() {
		return cancel;
	}
}
