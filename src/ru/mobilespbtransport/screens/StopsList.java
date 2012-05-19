package ru.mobilespbtransport.screens;

import ru.mobilespbtransport.model.Place;
import ru.mobilespbtransport.model.Stop;
import ru.mobilespbtransport.util.Util;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 0:55
 * To change this template use File | SettingsScreen | File Templates.
 */
public class StopsList extends List implements CommandListener{
	private final Command backCommand = new Command(Util.convertToUtf8("Назад"), Command.CANCEL, 2);

	public StopsList(Vector v) {
		super(Util.convertToUtf8("Выберите место"), IMPLICIT);

		addCommand(backCommand);
		setValues(v);
	}
	
	public void setValues(Vector v){
		for(int i = 0; i<v.size(); i++){
			Stop stop = (Stop)v.elementAt(i);
			append(stop.getName(), null);
		}
	} 
	
	public int getSelected(){
		return getSelectedIndex();
	}


	public void commandAction(Command command, Displayable displayable) {
		if(command == List.SELECT_COMMAND){
			//TODO
		} else if (command == backCommand){
			ScreenStack.pop();
		}
	}
}
