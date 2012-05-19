package ru.mobilespbtransport.screens;

import ru.mobilespbtransport.Controller;
import ru.mobilespbtransport.util.Util;

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
	private Command save = new Command(Util.convertToUtf8("Сохранить"), Command.OK, 1);
	private ChoiceGroup layers;
	private ChoiceGroup settings;
                     	
	public SettingsScreen() {
		super(Util.convertToUtf8("Настройки"));
		addCommand(save);

		try {
			layers = new ChoiceGroup(Util.convertToUtf8("Слои на карте:"), Choice.MULTIPLE);
			layers.append(Util.convertToUtf8("Автобусы"), Image.createImage("/bus.png"));
			layers.append(Util.convertToUtf8("Троллейбусы"), Image.createImage("/trolley.png"));
			layers.append(Util.convertToUtf8("Трамваи"), Image.createImage("/tram.png"));
			append(layers);

			settings = new ChoiceGroup(Util.convertToUtf8("Настройки:"), Choice.MULTIPLE);
			settings.append(Util.convertToUtf8("Автообновление"), Image.createImage("/autoupdate.png"));
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
