package ru.mobilespbtransport.view;

import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.model.Place;
import ru.mobilespbtransport.model.Stop;


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
public class StopsListScreen extends List implements CommandListener{
	private final Command backCommand = new Command("Назад", Command.CANCEL, 0);
	private final Command showOnMap = new Command("На карте", Command.ITEM, 1);
	private Vector stops;
	private boolean isLoaded = false;

	public StopsListScreen() {
		this(new Vector());
	}

	public StopsListScreen(Vector stops) {
		super("Выберите остановку", IMPLICIT);

		addCommand(backCommand);
		addCommand(showOnMap);
		setCommandListener(this);

		setStops(stops);

		try {
			append("Загрузка...", Image.createImage("/autoupdate.png"));
		} catch (IOException e) {
			//ignoring
		}
	}
	
	public void setStops(Vector stops){
		while(size() > 0){
			delete(0);
		}
		isLoaded = true;
		this.stops = stops;
		if(stops == null){
			return;
		}
		for(int i = 0; i<stops.size(); i++){
			Stop stop = (Stop)stops.elementAt(i);
			Image image = null;
			try {
				if(stop.isDirect()){
					image = Image.createImage("/direct.png");
				} else {
					image = Image.createImage("/return.png");
				}
			} catch (IOException e) {
				//TODO
				e.printStackTrace();
			}
			append(stop.getName(), image);
		}
	} 

	public void commandAction(Command command, Displayable displayable) {
		if(command == List.SELECT_COMMAND && isLoaded){
			if(getSelectedIndex() < 0){
				return;
			}
			Stop stop = (Stop) stops.elementAt(getSelectedIndex());
			ArrivingScreen arrivingScreen = new ArrivingScreen(stop);
			ScreenStack.push(arrivingScreen);
			Controller.updateArrivingScreen(stop, arrivingScreen);
		} else if(command == showOnMap && isLoaded){
			if(getSelectedIndex() < 0){
				return;
			}
			Stop stop = (Stop) stops.elementAt(getSelectedIndex());
			Place place = new Place(stop.getName(), stop.getCoordinate());
			Controller.setCurrentPlace(place);
			ScreenStack.push(Controller.getMapScreen());
		} else if (command == backCommand){
			ScreenStack.pop();
		}
	}
}
