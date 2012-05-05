package ru.mobilespbtransport.screens;

import javax.microedition.lcdui.*;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 04.05.12
 * Time: 23:58
 * To change this template use File | SettingsScreen | File Templates.
 */
public class SettingsScreen extends Form{
	private Command save;
	private ChoiceGroup layers;

	public SettingsScreen() {
		super("Settings");
		save = new Command("Save", Command.OK, 1);
		addCommand(save);
		layers = new ChoiceGroup("Select Layers", Choice.MULTIPLE);
		layers.append("bus", null);
		layers.append("trolley", null);
		layers.append("tram", null);
		layers.append("auto-update", null);
		append(layers);
	}

	public Command getSave() {
		return save;
	}

	public boolean isSelected(int i){
		return layers.isSelected(i);
	}

	public void setValue(int i, boolean value){
		layers.setSelectedIndex(i, value);
	}
}
