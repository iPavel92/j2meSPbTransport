package ru.mobilespbtransport.view;

import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.model.Place;


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
	private final Command backCommand = new Command("Назад", Command.CANCEL, 2);
	private Vector places;
	private boolean isLoaded = false;
	
	public PlacesList() {
		this(new Vector());
	}

	public PlacesList(Vector places) {
		super("Выберите место", IMPLICIT);
		setPlaces(places);
		addCommand(backCommand);
		setCommandListener(this);

		try {
			append("Загрузка...", Image.createImage("/autoupdate.png"));
		} catch (IOException e) {
			//ignoring
		}
	}
	
	public void setPlaces(Vector places){
		while(size() > 0){
			delete(0);
		}
		this.places = places;
		if(places == null){
			return;
		}
		isLoaded = true;
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
		if(command == List.SELECT_COMMAND && isLoaded){
			Controller.setCurrentPlace((Place) places.elementAt(getSelectedIndex()));
			ScreenStack.push(Controller.getMapScreen());
		} else if (command == backCommand){
			ScreenStack.pop();
		}
	}
}
