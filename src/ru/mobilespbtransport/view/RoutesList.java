package ru.mobilespbtransport.view;

import ru.mobilespbtransport.Controller;
import ru.mobilespbtransport.model.Route;
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
public class RoutesList extends List implements CommandListener{
	private final Command backCommand = new Command(Util.convertToUtf8("Назад"), Command.CANCEL, 2);
	private Vector routes;

	public RoutesList() {
		this(new Vector());
	}

	public RoutesList(Vector routes) {
		super(Util.convertToUtf8("Выберите маршрут"), IMPLICIT);
		setRoutes(routes);

		addCommand(backCommand);
		setCommandListener(this);
	}
	
	public void setRoutes(Vector routes){
		while(size() > 0){
			delete(0);
		}
		this.routes = routes;
		if(routes == null){
			return;
		}
		for(int i = 0; i<routes.size(); i++){
			Route transport = (Route)routes.elementAt(i);
			append(transport.getRouteNumber(), transport.getTransportType().getIcon());
		}
	} 

	public void commandAction(Command command, Displayable displayable) {
		if(command == List.SELECT_COMMAND){
			StopsList stopsList = new StopsList();
			ScreenStack.push(stopsList);
			Controller.findStops((Route)routes.elementAt(getSelectedIndex()), stopsList);
		} else if (command == backCommand){
			ScreenStack.pop();
		}
	}
}
