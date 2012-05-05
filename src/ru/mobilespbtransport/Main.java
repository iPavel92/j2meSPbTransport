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

	private final Command settings = new Command("Settings", Command.ITEM, 2);
	private final Command updateCommand = new Command("Update", Command.ITEM, 3);
	private final Command viewPlacesCommand = new Command("Places", Command.ITEM, 4);
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
		mapCanvas.addCommand(viewPlacesCommand);
		mapCanvas.setCommandListener(this);

		settingsScreen.setCommandListener(new CommandListener() {
			public void commandAction(Command command, Displayable displayable) {
				if (command == settingsScreen.getSave()) {
					controller.setLayers(
							settingsScreen.isSelected(0),
							settingsScreen.isSelected(1),
							settingsScreen.isSelected(2));
					controller.setAutoUpdate(settingsScreen.isSelected(3));
					display.setCurrent(mapCanvas);
				}
			}
		});

		showPlaces();
		updater.start();
	}

	Thread updater = new Thread() {
		private static final int SLEEP_INTERVAL = 30 * 1000;

		public void run() {
			try {
				while (true) {
					sleep(SLEEP_INTERVAL);
					if (display.getCurrent() == mapCanvas && controller.getModel().isUseAutoUpdate()) {
						controller.loadTransportLayer();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();  //ignore
			}
		}
	};

	public void pauseApp() {
	}

	public void destroyApp(boolean unconditional) {
	}

	public void showPlaces() {
		final PlacesList selectPlaceScreen = new PlacesList(controller.getModel().getStops());
		selectPlaceScreen.setCommandListener(new CommandListener() {
			public void commandAction(Command command, Displayable displayable) {
				if (command == selectPlaceScreen.getBackCommand()) {
					display.setCurrent(mapCanvas);
				} else if (command == selectPlaceScreen.getSelectCommand()) {
					controller.selectPlace(selectPlaceScreen.getSelected());
					display.setCurrent(mapCanvas);
				} else if (command == selectPlaceScreen.getAddPlaceCommand()) {
					final AddStopScreen addStopScreen = new AddStopScreen();
					addStopScreen.setCommandListener(new CommandListener() {
						public void commandAction(Command command, Displayable displayable) {
							if (command == addStopScreen.getCancel()) {
								display.setCurrent(selectPlaceScreen);
							} else if (command == addStopScreen.getOk()) {
								try {
									double lat = Double.parseDouble(addStopScreen.getLat());
									double lon = Double.parseDouble(addStopScreen.getLon());
									Place place = new Place(addStopScreen.getName(), lat, lon);
									controller.addPlace(place);
									selectPlaceScreen.append(place.getName(), null);
								} catch (NumberFormatException e) {
									e.printStackTrace();  //TODO
								}
								display.setCurrent(selectPlaceScreen);
							}
						}
					});
					display.setCurrent(addStopScreen);
				} else if (command == selectPlaceScreen.getDeletePlaceCommand()) {
					int selected = selectPlaceScreen.getSelected();
					controller.removePlace(selected);
					selectPlaceScreen.delete(selected);
				} else if (command == selectPlaceScreen.getSearchPlaceCommand()) {
					final SearchPlaceScreen searchPlaceScreen = new SearchPlaceScreen();
					searchPlaceScreen.setCommandListener(new CommandListener() {
						public void commandAction(Command command, Displayable displayable) {
							if (command == searchPlaceScreen.getOk()) {
								new Thread() {
									public void run() {
										final Vector foundPlaces = Geocoder.getPlaces(searchPlaceScreen.getAddress());
										final PlacesList selectFoundScreen = new PlacesList(foundPlaces);

										//dirty hack :)  (re-using one screen class)
										selectFoundScreen.removeCommand(selectFoundScreen.getDeletePlaceCommand());
										selectFoundScreen.removeCommand(selectFoundScreen.getSearchPlaceCommand());

										selectFoundScreen.setCommandListener(new CommandListener() {
											public void commandAction(Command command, Displayable displayable) {
												if (command == selectFoundScreen.getBackCommand()) {
													display.setCurrent(selectPlaceScreen);
												} else if (command == selectFoundScreen.getSelectCommand()) {
													Place selectedPlace = (Place) foundPlaces.elementAt(selectFoundScreen.getSelected());
													System.out.println(selectedPlace.getName());
													System.out.println(selectedPlace.getLat());
													System.out.println(selectedPlace.getLon());
													controller.selectPlace(selectedPlace);
													display.setCurrent(mapCanvas);
												} else if (command == selectFoundScreen.getAddPlaceCommand()) {
													Place selectedPlace = (Place) foundPlaces.elementAt(selectFoundScreen.getSelected());
													controller.addPlace(selectedPlace);
													selectPlaceScreen.append(selectedPlace.getName(), null);
												}
											}
										});
										display.setCurrent(selectFoundScreen);
									}
								}.start();
							} else if (command == searchPlaceScreen.getCancel()) {
								display.setCurrent(selectPlaceScreen);
							}
						}
					});
					display.setCurrent(searchPlaceScreen);
				}
			}
		});
		display.setCurrent(selectPlaceScreen);
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
			settingsScreen.setValue(3, controller.getModel().isUseAutoUpdate());
			display.setCurrent(settingsScreen);
		} else if (c == viewPlacesCommand) {
			showPlaces();
		}
	}
}
