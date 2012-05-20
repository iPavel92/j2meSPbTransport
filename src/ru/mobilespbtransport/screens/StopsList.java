package ru.mobilespbtransport.screens;

import ru.mobilespbtransport.Controller;
import ru.mobilespbtransport.model.Place;
import ru.mobilespbtransport.model.Stop;
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
public class StopsList extends List implements CommandListener{
	private final Command backCommand = new Command(Util.convertToUtf8("Назад"), Command.CANCEL, 0);
	private final Command showOnMap = new Command(Util.convertToUtf8("На карте"), Command.ITEM, 1);
	private Vector stops;

	public StopsList() {
		this(new Vector());
	}

	public StopsList(Vector stops) {
		super(Util.convertToUtf8("Выберите остановку"), IMPLICIT);

		setStops(stops);
		addCommand(backCommand);
		addCommand(showOnMap);
		setCommandListener(this);
	}
	
	public void setStops(Vector stops){
		while(size() > 0){
			delete(0);
		}
		this.stops = stops;
		if(stops == null){
			return;
		}
		for(int i = 0; i<stops.size(); i++){
			Stop stop = (Stop)stops.elementAt(i);
			append(stop.getName(), null);
		}
	} 
	
	public void addStop(Stop stop, boolean isDirect){
		stops.addElement(stop);
		Image image = null;
		try {
			if(isDirect){
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

	public void commandAction(Command command, Displayable displayable) {
		if(command == List.SELECT_COMMAND){
			Stop stop = (Stop) stops.elementAt(getSelectedIndex());
			ArrivingScreen arrivingScreen = new ArrivingScreen(stop);
			ScreenStack.push(arrivingScreen);
			Controller.updateArrivingScreen(stop, arrivingScreen);
		} else if(command == showOnMap){
			Stop stop = (Stop) stops.elementAt(getSelectedIndex());
			//TODO convert coordinates
			Place place = new Place(stop.getName(), stop.getLat(), stop.getLon());
			Controller.setCurrentPlace(place);
			ScreenStack.push(Controller.getMapScreen());
		} else if (command == backCommand){
			ScreenStack.pop();
		}
	}
}
