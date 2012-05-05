package ru.mobilespbtransport;

import ru.mobilespbtransport.cache.Cache;
import ru.mobilespbtransport.geocoder.Geocoder;
import ru.mobilespbtransport.model.Place;
import ru.mobilespbtransport.model.Model;
import ru.mobilespbtransport.screens.*;

import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

public class Main extends MIDlet implements CommandListener {
	private final Controller controller = new Controller();

	private final MapScreen mapCanvas = new MapScreen(controller);
	private final SettingsScreen settingsScreen = new SettingsScreen();

	private final Command settings = new Command("Layers", Command.ITEM, 2);
	private final Command updateCommand = new Command("Update", Command.ITEM, 3);
	private final Command selectPlaceCommand = new Command("Places", Command.ITEM, 4);
	private final Command searchPlaceCommand = new Command("Search place", Command.ITEM, 5);
	private final Command exitCommand = new Command("Exit", Command.EXIT, 1);
	private final Display display;

	public Main() {
		display = Display.getDisplay(this);
	}

	public void startApp() {
		Model model = Cache.loadModel();
		controller.setModel(model);
		controller.setMapScreen(mapCanvas);

		mapCanvas.addCommand(exitCommand);
		mapCanvas.addCommand(updateCommand);
		mapCanvas.addCommand(settings);
		mapCanvas.addCommand(selectPlaceCommand);
		mapCanvas.addCommand(searchPlaceCommand);
		mapCanvas.setCommandListener(this);

		settingsScreen.setCommandListener(new CommandListener() {
			public void commandAction(Command command, Displayable displayable) {
				if (command == settingsScreen.getSave()) {
					controller.setLayers(
							settingsScreen.isSelected(0),
							settingsScreen.isSelected(1),
							settingsScreen.isSelected(2));
					display.setCurrent(mapCanvas);
				}
			}
		});

		display.setCurrent(mapCanvas);
	}

	public void pauseApp() {
	}

	public void destroyApp(boolean unconditional) {
	}

	public void commandAction(Command c, Displayable s) {
		if (c == exitCommand) {
			destroyApp(false);
			notifyDestroyed();
		} else if (c == updateCommand) {
			controller.loadTransportLayer();
		} else if (c == settings) {
			settingsScreen.setValue(0, controller.getModel().isShowBus());
			settingsScreen.setValue(1, controller.getModel().isShowTrolley());
			settingsScreen.setValue(2, controller.getModel().isShowTram());
			display.setCurrent(settingsScreen);
		} else if (c == selectPlaceCommand) {
			final PlacesList selectStopScreen = new PlacesList(controller.getModel().getStops());
			selectStopScreen.setCommandListener(new CommandListener() {
				public void commandAction(Command command, Displayable displayable) {
					if (command == selectStopScreen.getBackCommand()) {
						display.setCurrent(mapCanvas);
					} else if (command == selectStopScreen.getSelectCommand()) {
						controller.selectPlace(selectStopScreen.getSelected());
						display.setCurrent(mapCanvas);
					} else if (command == selectStopScreen.getAddPlaceCommand()) {
						final AddStopScreen addStopScreen = new AddStopScreen();
						addStopScreen.setCommandListener(new CommandListener() {
							public void commandAction(Command command, Displayable displayable) {
								if (command == addStopScreen.getCancel()) {
									display.setCurrent(mapCanvas);
								} else if (command == addStopScreen.getOk()) {
									try {
										double lat = Double.parseDouble(addStopScreen.getLat());
										double lon = Double.parseDouble(addStopScreen.getLon());
										Place place = new Place(addStopScreen.getName(), lat, lon);
										controller.addPlace(place);
										selectStopScreen.append(place.getName(), null);
									} catch (NumberFormatException e) {
										e.printStackTrace();  //TODO
									}
									display.setCurrent(selectStopScreen);
								}
							}
						});
						display.setCurrent(addStopScreen);
					} else if (command == selectStopScreen.getDeletePlaceCommand()) {
						int selected = selectStopScreen.getSelected();
						controller.removePlace(selected);
						selectStopScreen.delete(selected);
					}
				}
			});
			display.setCurrent(selectStopScreen);
		} else if (c == searchPlaceCommand) {
			final SearchPlaceScreen searchPlaceScreen = new SearchPlaceScreen();
			searchPlaceScreen.setCommandListener(new CommandListener() {
				public void commandAction(Command command, Displayable displayable) {
					if (command == searchPlaceScreen.getOk()) {
						new Thread() {
							public void run() {
								final Vector foundPlaces = Geocoder.getPlaces(searchPlaceScreen.getAddress());
								final PlacesList selectStopScreen = new PlacesList(foundPlaces);
								selectStopScreen.removeCommand(selectStopScreen.getDeletePlaceCommand()); //dirty hack :)
								selectStopScreen.setCommandListener(new CommandListener() {
									public void commandAction(Command command, Displayable displayable) {
										if (command == selectStopScreen.getBackCommand()) {
											display.setCurrent(mapCanvas);
										} else if (command == selectStopScreen.getSelectCommand()) {
											Place selectedPlace = (Place) foundPlaces.elementAt(selectStopScreen.getSelected());
											System.out.println(selectedPlace.getName());
											System.out.println(selectedPlace.getLat());
											System.out.println(selectedPlace.getLon());
											controller.selectPlace(selectedPlace);
											display.setCurrent(mapCanvas);
										} else if (command == selectStopScreen.getAddPlaceCommand()) {
											Place selectedPlace = (Place) foundPlaces.elementAt(selectStopScreen.getSelected());
											controller.addPlace(selectedPlace);
										}
									}
								});
								display.setCurrent(selectStopScreen);
							}
						}.start();
					} else if (command == searchPlaceScreen.getCancel()) {
						display.setCurrent(mapCanvas);
					}
				}
			});
			display.setCurrent(searchPlaceScreen);
		}
	}
}
