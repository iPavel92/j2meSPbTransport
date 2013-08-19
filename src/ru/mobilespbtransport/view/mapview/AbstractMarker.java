package ru.mobilespbtransport.view.mapview;

import ru.mobilespbtransport.model.Coordinate;

public abstract class AbstractMarker implements Marker {
    protected Coordinate coordinate;
    protected PixelPoint position;
    protected boolean isFocused = false;

    public void setScreenPosition(PixelPoint position) {
        this.position = position;
    }

    public PixelPoint getScreenPosition() {
        return position;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Coordinate getCoordinate() {
       return coordinate;
    }

    public void onFocused() {
        isFocused = true;
    }

    public void onFocusLost() {
        isFocused = false;
    }
}
