package ru.mobilespbtransport.screens;

import ru.mobilespbtransport.model.Coordinate;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 0:55
 * To change this template use File | SettingsScreen | File Templates.
 */
public class PlacesList extends List {
	private Command selectCommand;
	private Command backCommand;
	private Command addPlaceCommand;
	private Command deletePlaceCommand;

	public PlacesList(Vector v) {
		super("Select place", IMPLICIT);
		selectCommand = new Command("Select", Command.OK, 1);
		backCommand = new Command("Back", Command.CANCEL, 2);
		addPlaceCommand = new Command("Add place", Command.ITEM, 3);
		deletePlaceCommand = new Command("Delete place", Command.ITEM, 4);

		addCommand(addPlaceCommand);
		addCommand(deletePlaceCommand);
		addCommand(selectCommand);
		addCommand(backCommand);
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

	public Command getSelectCommand() {
		return selectCommand;
	}

	public Command getBackCommand() {
		return backCommand;
	}

	public Command getAddPlaceCommand() {
		return addPlaceCommand;
	}

	public Command getDeletePlaceCommand() {
		return deletePlaceCommand;
	}
}
