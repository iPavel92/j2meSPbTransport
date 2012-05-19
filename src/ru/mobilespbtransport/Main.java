package ru.mobilespbtransport;

import ru.mobilespbtransport.cache.Cache;
import ru.mobilespbtransport.model.Model;
import ru.mobilespbtransport.screens.*;

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
