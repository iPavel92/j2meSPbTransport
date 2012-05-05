package ru.mobilespbtransport;

import ru.mobilespbtransport.cache.Cache;
import ru.mobilespbtransport.model.Coordinate;
import ru.mobilespbtransport.model.GeoConverter;
import ru.mobilespbtransport.model.Model;
import ru.mobilespbtransport.network.ImageLoader;
import ru.mobilespbtransport.screens.AddStopScreen;
import ru.mobilespbtransport.screens.PlacesList;
import ru.mobilespbtransport.screens.MapScreen;
import ru.mobilespbtransport.screens.SettingsScreen;

import java.io.IOException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

public class Main extends MIDlet implements CommandListener {
	Model model = new Model();

	MapScreen mapCanvas = new MapScreen();
	SettingsScreen settingsScreen = new SettingsScreen();

	private Command settings = new Command("Layers", Command.ITEM, 2);
	private Command updateCommand = new Command("Update", Command.ITEM, 3);
	private Command selectPlaceCommand = new Command("Select place", Command.ITEM, 5);
	private Command exitCommand = new Command("Exit", Command.EXIT, 1);
	private Display display;

	public Main() {
		display = Display.getDisplay(this);
	}

	public void startApp() {
		mapCanvas.addCommand(exitCommand);
		mapCanvas.addCommand(updateCommand);
		mapCanvas.addCommand(settings);
		mapCanvas.addCommand(selectPlaceCommand);
		mapCanvas.setCommandListener(this);

		settingsScreen.setCommandListener(new CommandListener() {
			public void commandAction(Command command, Displayable displayable) {
				if (command == settingsScreen.getSave()) {
					model.setShowBus(settingsScreen.isSelected(0));
					model.setShowTrolley(settingsScreen.isSelected(1));
					model.setShowTram(settingsScreen.isSelected(2));
					Cache.saveModel(model);
					loadTransportLayer();
					display.setCurrent(mapCanvas);
				}
			}
		});


		display.setCurrent(mapCanvas);

		model = Cache.loadModel();
		//model.getStops().addElement(new Coordinate("1", 60.0465,30.3415));
		//loadMap();
		//loadTransportLayer();
	}

	private void loadMap() {
		new Thread() {
			public void run() {
				try {
					if (model.getCoordinate() == null) {
						return;
					}
					String url = getMapUri(model.getCoordinate(), mapCanvas.getWidth(), mapCanvas.getHeight());
					System.out.println(url);
					Image map = ImageLoader.getMapImage(model.getCoordinate(), url);
					mapCanvas.setMap(map);
					mapCanvas.repaint();
				} catch (IOException e) {
					e.printStackTrace();  //TODO
				}
			}
		}.start();
	}

	private void loadTransportLayer() {
		new Thread() {
			public void run() {
				try {
					if (model.getCoordinate() == null) {
						return;
					}
					String bBox = GeoConverter.buildBBox(model.getCoordinate(), mapCanvas.getWidth(), mapCanvas.getHeight());
					String url = getTransportMapUrl(bBox, model.isShowBus(), model.isShowTrolley(), model.isShowTram(), mapCanvas.getWidth(), mapCanvas.getHeight());
					System.out.println(url);
					Image transportLayer = ImageLoader.getImageFromInet(url);
					mapCanvas.setTransportLayer(transportLayer);
					mapCanvas.repaint();
				} catch (IOException e) {
					e.printStackTrace();  //TODO
				}
			}
		}.start();
	}

	private String getTransportMapUrl(String bbox, boolean showBus, boolean showTrolley, boolean showTram, int screenWidth, int screenHeight) {
		boolean isCommaRequired = false;
		String layers = "";
		if (showBus) {
			layers = layers + "vehicle_bus";
			isCommaRequired = true;
		}
		if (showTrolley) {
			layers = layers + (isCommaRequired ? "," : "") + "vehicle_trolley";
			isCommaRequired = true;
		}
		if (showTram) {
			layers = layers + (isCommaRequired ? "," : "") + "vehicle_tram";
			isCommaRequired = true;
		}
		return "http://transport.orgp.spb.ru/cgi-bin/mapserv?TRANSPARENT=TRUE&FORMAT=image%2Fpng&LAYERS=" + layers + "&MAP=vehicle_typed.map&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&SRS=EPSG%3A900913&_OLSALT=0.1508798657450825&BBOX=" + bbox + "&WIDTH=" + screenWidth + "&HEIGHT=" + screenHeight;
	}

	private String getMapUri(Coordinate center, int screenWidth, int screenHeight) {
		return "http://maps.google.com/maps/api/staticmap?zoom=13&sensor=false&size=" + screenWidth + "x" + screenHeight + "&center=" + center.getLat() + "," + center.getLon();
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
			loadTransportLayer();
		} else if (c == settings) {
			settingsScreen.setValue(0, model.isShowBus());
			settingsScreen.setValue(1, model.isShowTrolley());
			settingsScreen.setValue(2, model.isShowTram());
			display.setCurrent(settingsScreen);
		} else if (c == selectPlaceCommand) {
			final PlacesList selectStopScreen = new PlacesList(model.getStops());
			selectStopScreen.setCommandListener(new CommandListener() {
				public void commandAction(Command command, Displayable displayable) {
					if (command == selectStopScreen.getBackCommand()) {
						display.setCurrent(mapCanvas);
					} else if (command == selectStopScreen.getSelectCommand()) {
						model.setCoordinate((Coordinate) model.getStops().elementAt(selectStopScreen.getSelected()));
						loadMap();
						loadTransportLayer();
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
										Coordinate coordinate = new Coordinate(addStopScreen.getName(), lat, lon);
										model.getStops().addElement(coordinate);
										selectStopScreen.append(coordinate.getName(), null);
										Cache.saveModel(model);
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
						model.getStops().removeElementAt(selected);
						selectStopScreen.delete(selected);
						Cache.saveModel(model);
					}
				}
			});
			display.setCurrent(selectStopScreen);
		}
	}

	//TODO
	public String getUrlForGeocoding(String adress) {
		return
				"http://maps.googleapis.com/maps/api/geocode/xml?" +
						"adress=" + adress +
						"&bounds=59.661,29.518|60.270,30.757" +  //SPB only
						"&sensor=false";
	}

}