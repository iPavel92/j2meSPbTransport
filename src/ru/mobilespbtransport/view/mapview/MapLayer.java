package ru.mobilespbtransport.view.mapview;

import ru.mobilespbtransport.model.Coordinate;
import javax.microedition.lcdui.Graphics;

public interface MapLayer {
	public void init(ViewUpdater viewUpdater, int screenWidth, int screenHeight);
	public void onScreenSizeChanged(int screenWidth, int screenHeight);
	public void onMapMoved(Coordinate mapCenter);
	public void onMapZoomed(int zoom);
	public void paint(Graphics graphics);
    public void setVisible(boolean visible);
    public boolean isVisible();
    public void onPointerPressed(int x, int y);
    public void onAction();
}
