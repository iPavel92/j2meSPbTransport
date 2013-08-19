package ru.mobilespbtransport.view.mapview;
import ru.mobilespbtransport.model.Coordinate;

public abstract class AbstractMapLayer implements MapLayer {
    protected ViewUpdater viewUpdater;
    protected int screenWidth;
    protected int screenHeight;
    protected Coordinate mapCenter;
    protected int zoom;
    protected boolean visible = true;

    public void init(ViewUpdater viewUpdater, int screenWidth, int screenHeight) {
        this.viewUpdater = viewUpdater;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void onScreenSizeChanged(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void onMapMoved(Coordinate mapCenter) {
        this.mapCenter = mapCenter;
    }

    public void onMapZoomed(int zoom) {
        this.zoom = zoom;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void onPointerPressed(int x, int y) {
    }

    public void onAction() {
    }
}
