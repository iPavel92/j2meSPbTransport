package ru.mobilespbtransport.view.mapview;

import ru.mobilespbtransport.model.Coordinate;

import javax.microedition.lcdui.Graphics;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 19.08.13
 * Time: 13:08
 * To change this template use File | Settings | File Templates.
 */
public interface Marker {
    public void setScreenPosition(PixelPoint position);
    public PixelPoint getScreenPosition();
    public Coordinate getCoordinate();
    public boolean isFocusable();
    public void onFocused();
    public void onFocusLost();
    public void onClicked();
    public void paint(Graphics graphics);
}
