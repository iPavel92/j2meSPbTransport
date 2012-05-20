package ru.mobilespbtransport.view;

import ru.mobilespbtransport.util.Util;

import javax.microedition.lcdui.*;
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
		display.setCurrent(peek());
	}

	public static void showAlert(String message) {
		Alert alert = new Alert(Util.convertToUtf8("Сообщение"),
				message,
				null,
				AlertType.INFO);
		alert.setTimeout(Alert.FOREVER);
		alert.addCommand(new Command(Util.convertToUtf8("OK"), Command.BACK, 0));
		alert.setCommandListener(new CommandListener() {
			public void commandAction(Command command, Displayable displayable) {
				ScreenStack.pop();
			}
		});
		ScreenStack.push(alert);
	}
}
