package ru.mobilespbtransport.view;

import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.model.Route;


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
public class RoutesList extends List implements CommandListener{
	private final Command backCommand = new Command("Назад", Command.CANCEL, 2);
	private Vector routes;
	private boolean isLoaded = false;

	public RoutesList() {
		this(new Vector());
	}

	public RoutesList(Vector routes) {
		super("Выберите маршрут", IMPLICIT);
		setRoutes(routes);

		addCommand(backCommand);
		setCommandListener(this);

		try {
			append("Загрузка...", Image.createImage("/autoupdate.png"));
		} catch (IOException e) {
			//ignoring
		}
	}
	
	public void setRoutes(Vector routes){
		while(size() > 0){
			delete(0);
		}
		this.routes = routes;
		if(routes == null){
			return;
		}
		isLoaded = true;
		for(int i = 0; i<routes.size(); i++){
			Route transport = (Route)routes.elementAt(i);
			append(transport.getRouteNumber(), transport.getTransportType().getIcon());
		}
	} 

	public void commandAction(Command command, Displayable displayable) {
		if(command == List.SELECT_COMMAND && isLoaded){
			StopsList stopsList = new StopsList();
			ScreenStack.push(stopsList);
			Controller.findStops((Route) routes.elementAt(getSelectedIndex()), stopsList);
		} else if (command == backCommand){
			ScreenStack.pop();
		}
	}
}
