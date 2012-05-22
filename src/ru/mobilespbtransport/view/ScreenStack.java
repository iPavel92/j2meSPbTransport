package ru.mobilespbtransport.view;



import ru.mobilespbtransport.controller.Controller;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.GameCanvas;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 19.05.12
 * Time: 16:06
 * To change this template use File | Settings | File Templates.
 */
public class ScreenStack {
	//stack of the view
	private static final Vector screens = new Vector(); //Vector<Displayable>
	private static Display display;

	public static void setDisplay(Display display) {
		ScreenStack.display = display;
	}

	public static void push(Displayable screen) {
		clearOldHeavyScreens(screen);
		screens.addElement(screen);
		updateCurrentScreen();
	}

	public static Displayable peek() {
		return (Displayable) screens.elementAt(screens.size() - 1);
	}

	public static void pop() {
		if (screens.size() == 1) {
			throw new IllegalStateException("Empty screen stack.");
		}
		screens.removeElementAt(screens.size() - 1);
		updateCurrentScreen();
	}

	private static void updateCurrentScreen() {
		if (display == null) {
			throw new IllegalStateException("Display not setted.");
		}
		Displayable displayable = peek();
		display.setCurrent(displayable);
		if(displayable instanceof GameCanvas){
			((GameCanvas)displayable).setFullScreenMode(true);
		}
	}

	//deleting duplicating and heavy for memory screens from stack
	public static void clearOldHeavyScreens(Displayable displayable){
		for(int i = 0; i < screens.size(); ++i){
			if(displayable instanceof MapScreen && screens.elementAt(i) instanceof MapScreen){
				screens.removeElementAt(i);
				Controller.zoomOut();
				return;
			}
			if(displayable instanceof ArrivingScreen && screens.elementAt(i) instanceof ArrivingScreen){
				screens.removeElementAt(i);
				return;
			}
			if(displayable instanceof StopsListScreen && screens.elementAt(i) instanceof StopsListScreen){
				screens.removeElementAt(i);
				return;
			}
		}
		if(displayable instanceof GameCanvas){
			((GameCanvas)displayable).setFullScreenMode(true);
		}
	}
	
	public static void showAlert(String message) {
		Alert alert = new Alert("Сообщение",
				message,
				null,
				AlertType.INFO);
		alert.setTimeout(Alert.FOREVER);
		alert.addCommand(new Command("OK", Command.BACK, 0));
		alert.setCommandListener(new CommandListener() {
			public void commandAction(Command command, Displayable displayable) {
				ScreenStack.pop();
			}
		});
		ScreenStack.push(alert);
	}
}
