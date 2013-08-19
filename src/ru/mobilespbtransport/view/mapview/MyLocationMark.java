package ru.mobilespbtransport.view.mapview;

import ru.mobilespbtransport.controller.GeoConverter;
import ru.mobilespbtransport.model.Coordinate;

import javax.microedition.lcdui.Graphics;

public class MyLocationMark extends AbstractMapLayer {
	private Coordinate coordinate;
	private PixelPoint pointScreenPosition;
	private static final int RADIUS = 5;

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
		updatePointScreenPosition();
		if(viewUpdater != null){
			viewUpdater.updateView();
		}
	}

	private void updatePointScreenPosition(){
		if(mapCenter == null || coordinate == null){
			return;
		}
		int x = GeoConverter.getXPixelFromCoordinate(mapCenter, coordinate, screenWidth, zoom);
		int y = GeoConverter.getYPixelFromCoordinate(mapCenter, coordinate, screenHeight, zoom);
		pointScreenPosition = new PixelPoint(x, y);
	}

	public void init(ViewUpdater viewUpdater, int screenWidth, int screenHeight) {
		super.init(viewUpdater, screenWidth, screenHeight);
		updatePointScreenPosition();
	}

	public void onScreenSizeChanged(int screenWidth, int screenHeight) {
		super.onScreenSizeChanged(screenWidth, screenHeight);
		updatePointScreenPosition();
	}

	public void onMapMoved(Coordinate mapCenter) {
		super.onMapMoved(mapCenter);
		updatePointScreenPosition();
	}

	public void onMapZoomed(int zoom) {
		super.onMapZoomed(zoom);
		updatePointScreenPosition();
	}

	public void paint(Graphics graphics) {
		if(pointScreenPosition != null){
			graphics.setColor(0x1569C7);
			graphics.fillArc(
                    pointScreenPosition.getX() - RADIUS,
                    pointScreenPosition.getY() - RADIUS,
                    2 * RADIUS,
                    2 * RADIUS,
                    0, 360);
		}
	}
}
