package ru.mobilespbtransport.view;

import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.controller.GeoConverter;
import ru.mobilespbtransport.model.Stop;
import ru.mobilespbtransport.model.StopsGroup;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.GameCanvas;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 04.05.12
 * Time: 20:44
 * To change this template use File | SettingsScreen | File Templates.
 */
public class LoadingScreen extends GameCanvas implements CommandListener {
	private final Command exitCommand = new Command("Выход", Command.EXIT, 5);
	private final static String LOADING = "Загрузка...";

	public LoadingScreen() {
		super(false);
		addCommand(exitCommand);
		setCommandListener(this);
	}

	public void paint(Graphics graphics) {
		graphics.setColor(0xffffff);
		graphics.fillRect(0, 0, getWidth(), getHeight());
		graphics.setColor(0x000000);
		graphics.drawString(LOADING, getWidth() / 2, getHeight() / 2, Graphics.HCENTER | Graphics.TOP);
	}

	public void commandAction(Command command, Displayable displayable) {
		if (command == exitCommand) {
			Controller.exit();
		}
	}
}


