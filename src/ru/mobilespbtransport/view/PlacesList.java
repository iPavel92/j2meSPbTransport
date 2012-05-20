package ru.mobilespbtransport.view;

import ru.mobilespbtransport.Controller;
import ru.mobilespbtransport.model.Place;
import ru.mobilespbtransport.util.Util;

import javax.microedition.lcdui.*;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 0:55
 * To change this template use File | SettingsScreen | File Templates.
 */
public class PlacesList extends List implements CommandListener{
	private final Command backCommand = new Command(Util.convertToUtf8("Назад"), Command.CANCEL, 2);
	private Vector places;

	public PlacesList() {
		this(new Vector());
	}

	public PlacesList(Vector places) {
		super(Util.convertToUtf8("Выберите место"), IMPLICIT);
		setPlaces(places);
		addCommand(backCommand);
		setCommandListener(this);
	}
	
	public void setPlaces(Vector places){
		while(size() > 0){
			delete(0);
		}
		this.places = places;
		if(places == null){
			return;
		}

		Image placeIco = null;
		try {
			placeIco = Image.createImage("/place.png");
		} catch (IOException e) {
			//TODO
			e.printStackTrace();
		}
		for(int i = 0; i<places.size(); i++){
			Place c = (Place)places.elementAt(i);
			append(c.getName(), placeIco);
		}
	} 

	public void commandAction(Command command, Displayable displayable) {
		if(command == List.SELECT_COMMAND){
			Controller.setCurrentPlace((Place) places.elementAt(getSelectedIndex()));
			ScreenStack.push(Controller.getMapScreen());
		} else if (command == backCommand){
			ScreenStack.pop();
		}
	}
}
