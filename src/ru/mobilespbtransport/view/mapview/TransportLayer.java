package ru.mobilespbtransport.view.mapview;

import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.controller.GeoConverter;
import ru.mobilespbtransport.model.Coordinate;
import ru.mobilespbtransport.network.HttpClient;
import ru.mobilespbtransport.network.RequestGenerator;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


public class TransportLayer extends AbstractMapLayer {
	private Image layerImage;
	BoundingBox currentImageBoundingBox;
	PixelPoint currentImageScreenPosition;

	public void onScreenSizeChanged(int screenWidth, int screenHeight) {
        super.onScreenSizeChanged(screenWidth, screenHeight);
		loadTransportLayer();
	}

	public void onMapMoved(Coordinate mapCenter) {
		super.onMapMoved(mapCenter);
		if (isNeedToReloadLayer()) {
			loadTransportLayer();
		} else {
			currentImageScreenPosition = getCurrentImageScreenPosition();
		}
	}

	public void onMapZoomed(int zoom) {
		super.onMapZoomed(zoom);
		loadTransportLayer();
	}

	private PixelPoint getCurrentImageScreenPosition(){
		Coordinate nw = new Coordinate(currentImageBoundingBox.getWest(), currentImageBoundingBox.getNorth());
		int dx = GeoConverter.getXPixelFromCoordinate(mapCenter, nw, screenWidth, zoom);
		int dy = GeoConverter.getYPixelFromCoordinate(mapCenter, nw, screenHeight, zoom);
		return new PixelPoint(dx, dy);
	}

	private boolean isNeedToReloadLayer() {
		if (currentImageScreenPosition == null || currentImageBoundingBox == null) {
			return true;
		} else {
			return  (currentImageScreenPosition.getX() > screenWidth / 3 ||
					currentImageScreenPosition.getX() < -screenWidth / 3 ||
					currentImageScreenPosition.getY() > screenHeight / 3 ||
					currentImageScreenPosition.getY() < -screenHeight / 3);
		}
	}

    public void update(){
        loadTransportLayer();
    }

	private void loadTransportLayer() {
		Controller.sheduleTask(new Runnable() {
			public void run() {
				try {
					if (mapCenter == null) {
						return;
					}
					layerImage = null;
					currentImageBoundingBox = getBoundingBox();
					currentImageScreenPosition = new PixelPoint(0, 0);

					String bBox = GeoConverter.buildBBox(mapCenter, screenWidth, screenHeight, zoom);
					String url = RequestGenerator.getTransportMapUrl(
							bBox,
							Controller.getModel().isShowBus(),
							Controller.getModel().isShowTrolley(),
							Controller.getModel().isShowTram(),
							screenWidth,
							screenHeight);
					System.out.println(url);
					Image loadedImage = HttpClient.loadImage(url);

					layerImage = loadedImage;

					viewUpdater.updateView();
				} catch (Exception e) {
					e.printStackTrace();  //TODO
				}
			}
		});
	}


	public BoundingBox getBoundingBox() {
		double west = mapCenter.toWGS84().getLat() + GeoConverter.getLatDiff(screenHeight / 2, zoom);
		double east = mapCenter.toWGS84().getLat() - GeoConverter.getLatDiff(screenHeight / 2, zoom);
		double north = mapCenter.toWGS84().getLon() - GeoConverter.getLonDiff(screenWidth / 2, zoom);
		double south = mapCenter.toWGS84().getLon() + GeoConverter.getLonDiff(screenWidth / 2, zoom);

		return new BoundingBox(west, east, south, north);
	}

	public void paint(Graphics graphics) {
		if (layerImage != null && currentImageScreenPosition != null) {
			graphics.drawImage(
					layerImage,
					currentImageScreenPosition.getX(),
					currentImageScreenPosition.getY(),
					Graphics.LEFT | Graphics.TOP);
		}
	}
}
