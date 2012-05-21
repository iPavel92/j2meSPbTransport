package ru.mobilespbtransport.view;

import ru.mobilespbtransport.controller.Controller;


import javax.microedition.lcdui.*;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 04.05.12
 * Time: 23:58
 * To change this template use File | SettingsScreen | File Templates.
 */
public class SettingsScreen extends Form implements CommandListener{
	private Command save = new Command("Сохранить", Command.OK, 1);
	private ChoiceGroup layers;
	private ChoiceGroup settings;
                     	
	public SettingsScreen() {
		super("Настройки");
		addCommand(save);

		try {
			layers = new ChoiceGroup("Слои на карте:", Choice.MULTIPLE);
			layers.append("Автобусы", Image.createImage("/bus.png"));
			layers.append("Троллейбусы", Image.createImage("/trolley.png"));
			layers.append("Трамваи", Image.createImage("/tram.png"));
			append(layers);

			settings = new ChoiceGroup("Настройки:", Choice.MULTIPLE);
			settings.append("Автообновление", Image.createImage("/autoupdate.png"));
			append(settings);
		} catch (IOException e) {
			//TODO
			e.printStackTrace();
		}
		setCommandListener(this);
		setPreferences();
	}

	private void setPreferences(){
		layers.setSelectedIndex(0, Controller.getModel().isShowBus());
		layers.setSelectedIndex(1, Controller.getModel().isShowTrolley());
		layers.setSelectedIndex(2, Controller.getModel().isShowTram());
		settings.setSelectedIndex(0, Controller.getModel().isUseAutoUpdate());
	}

	public void commandAction(Command command, Displayable displayable) {
		if(command==save){
			Controller.setLayers(
					layers.isSelected(0),
					layers.isSelected(1),
					layers.isSelected(2));
			Controller.setAutoUpdate(settings.isSelected(0));
			ScreenStack.pop();
		}
	}
}
