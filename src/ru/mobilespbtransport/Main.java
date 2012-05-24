package ru.mobilespbtransport;

import ru.mobilespbtransport.cache.Cache;
import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.controller.Worker;
import ru.mobilespbtransport.view.*;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

public class Main extends MIDlet {
	private final MapScreen mapCanvas = new MapScreen();
	private final FavouritesScreen favouritesList = new FavouritesScreen();
	private final Worker worker = new Worker();

	public Main() {
		ScreenStack.setDisplay(Display.getDisplay(this));
	}

	public void exit() {
		worker.setStopping(true);
		destroyApp(false);
		notifyDestroyed();
	}

	public void startApp() {
		worker.start();
		Controller.setMain(this);
		Controller.setMapScreen(mapCanvas);
		Controller.setFavouritesScreen(favouritesList);

		Cache.loadModel();
		Cache.loadStops();
		Cache.loadRoutes();

		updater.start();

		ScreenStack.push(favouritesList);
	}

	Thread updater = new Thread() {
		private static final int SLEEP_INTERVAL = 15 * 1000;

		public void run() {
			try {
				while (true) {
					sleep(SLEEP_INTERVAL);
					if (Controller.getModel().isUseAutoUpdate()) {
						if (ScreenStack.peek() == mapCanvas) {
							if (!Controller.isZoomedIn() && !mapCanvas.isLocked()) {
								Controller.loadTransportLayer();
							}
						}
						if (ScreenStack.peek() instanceof ArrivingScreen) {
							ArrivingScreen arrivingScreen = (ArrivingScreen) ScreenStack.peek();
							if (!arrivingScreen.isLocked()) {
								Controller.updateArrivingScreen(arrivingScreen.getCurrentStop(), arrivingScreen);
							}
						}
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

