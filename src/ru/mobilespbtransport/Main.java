package ru.mobilespbtransport;

import ru.mobilespbtransport.cache.Cache;
import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.controller.Worker;
import ru.mobilespbtransport.location.Locator;
import ru.mobilespbtransport.model.Coordinate;
import ru.mobilespbtransport.model.Place;
import ru.mobilespbtransport.view.*;
import ru.mobilespbtransport.view.mapview.*;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

public class Main extends MIDlet {
    private final MapView mapView = new MapView();
    private final FavouritesScreen favouritesList = new FavouritesScreen();
	private final Worker worker = new Worker();
    private final TransportLayer transportLayer = new TransportLayer();
    private final Coordinate DEFAULT_COORDINATE = new Coordinate(59.95000001,30.316666676667);
    private final int DEFAULT_ZOOM = 10;

	public Main() {
		ScreenStack.setDisplay(Display.getDisplay(this));
	}

	public void exit() {
		worker.setStopping(true);
		destroyApp(false);
		notifyDestroyed();
	}

	public void startApp() {
        ScreenStack.push(new LoadingScreen());

        Cache.loadModel();
		Cache.loadStops();
		Cache.loadRoutes();

		Controller.setMain(this);
		Controller.setMapView(mapView);
		Controller.setFavouritesScreen(favouritesList);

		worker.start();
		updater.start();

		mapView.addLayer(new TileMapLayer());
		mapView.addLayer(transportLayer);
        mapView.addLayer(new StopsMarkersLayer());
		final MyLocationMark myLocationMark = new MyLocationMark();
		mapView.addLayer(myLocationMark);

        if(Controller.getModel().getLastCoordinate() != null){
            mapView.setMapCenter(Controller.getModel().getLastCoordinate());
            mapView.setZoom(Controller.getModel().getLastZoom());
            ScreenStack.push(mapView);
        }

		Controller.sheduleTask(new Runnable() {
			public void run() {
				Coordinate coordinate = Locator.getLocation();
				if (coordinate != null) {
					mapView.setMapCenter(coordinate);
					myLocationMark.setCoordinate(coordinate);
				} else {
                    if(mapView.getMapCenter() == null){
                        mapView.setMapCenter(DEFAULT_COORDINATE);
                        mapView.setZoom(DEFAULT_ZOOM);
                    }
				}

                ScreenStack.push(mapView);
			}
		});
	}

	Thread updater = new Thread() {
		private static final int SLEEP_INTERVAL = 15 * 1000;
		public void run() {
			try {
				while (true) {
					sleep(SLEEP_INTERVAL);
					if (Controller.getModel().isUseAutoUpdate()) {
						if (ScreenStack.peek() == mapView) {
							transportLayer.update();
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


