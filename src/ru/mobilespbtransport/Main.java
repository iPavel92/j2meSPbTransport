package ru.mobilespbtransport;

import ru.mobilespbtransport.cache.Cache;
import ru.mobilespbtransport.model.*;
import ru.mobilespbtransport.screens.*;
import ru.mobilespbtransport.util.Util;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

public class Main extends MIDlet {
	private final MapScreen mapCanvas = new MapScreen();
	private final FavouritesList favouritesList = new FavouritesList();

	public Main() {
		ScreenStack.setDisplay(Display.getDisplay(this));
	}

	public void exit(){
		destroyApp(false);
		notifyDestroyed();
	}

	public void startApp() {
		Model model = Cache.loadModel();
		Controller.setModel(model);
		Controller.setMain(this);
		Controller.setMapScreen(mapCanvas);
		Controller.setFavouritesList(favouritesList);

		updater.start();

		ScreenStack.push(favouritesList);
		Stop stop = new Stop(Util.convertToUtf8("ст. м. пр. Просвещения и Художников"), 1, 1, new TransportType(TransportType.BUS));
		Route r1 = new Route(new TransportType(TransportType.BUS), "5");
		Route r2 = new Route(new TransportType(TransportType.BUS), "10");
		Route r3 = new Route(new TransportType(TransportType.BUS), "178");
		Arriving a1 = new Arriving(r1, 2);
		Arriving a2 = new Arriving(r3, 20);
		stop.getRoutes().addElement(r1);
		stop.getRoutes().addElement(r2);
		stop.getRoutes().addElement(r3);
		stop.getArriving().addElement(a1);
		stop.getArriving().addElement(a2);
		ArrivingScreen sct = new ArrivingScreen(stop);
		ScreenStack.push(sct);
	}

	Thread updater = new Thread() {
		private static final int SLEEP_INTERVAL = 15 * 1000;

		public void run() {
			try {
				while (true) {
					sleep(SLEEP_INTERVAL);
					if (ScreenStack.peek() == mapCanvas && Controller.getModel().isUseAutoUpdate()) {
						Controller.loadTransportLayer();
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
}

