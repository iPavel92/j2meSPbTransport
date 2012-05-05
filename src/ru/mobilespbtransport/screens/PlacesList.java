package ru.mobilespbtransport.screens;

import ru.mobilespbtransport.model.Place;

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
	private final Command selectCommand = new Command("Select", Command.OK, 1);
	private final Command backCommand = new Command("Back", Command.CANCEL, 2);
	private final Command addPlaceCommand = new Command("Add place", Command.ITEM, 3);
	private final Command deletePlaceCommand = new Command("Delete place", Command.ITEM, 4);
	private final Command searchPlaceCommand = new Command("Search place", Command.ITEM, 5);

	public PlacesList(Vector v) {
		super("Select place", IMPLICIT);

		addCommand(addPlaceCommand);
		addCommand(deletePlaceCommand);
		addCommand(selectCommand);
		addCommand(backCommand);
		addCommand(searchPlaceCommand);
		setValues(v);
	}
	
	public void setValues(Vector v){
		for(int i = 0; i<v.size(); i++){
			Place c = (Place)v.elementAt(i);
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

	public Command getSearchPlaceCommand() {
		return searchPlaceCommand;
	}
}
