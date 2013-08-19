package ru.mobilespbtransport.view.mapview;

import ru.mobilespbtransport.controller.GeoConverter;
import ru.mobilespbtransport.model.Coordinate;

import javax.microedition.lcdui.Graphics;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 19.08.13
 * Time: 13:00
 * To change this template use File | Settings | File Templates.
 */
public class MarkersLayer extends AbstractMapLayer {
    private static final int SELECTION_RADIUS = 40;
    private Vector markers = new Vector(); //Vector<Marker>
    private Marker selectedMarker;

    public Vector getMarkers() {
        return markers;
    }

    public void setMarkers(Vector markers) {
        this.markers = markers;
        selectedMarker = null;
        update();
    }

    public void addMarker(Marker marker){
        markers.addElement(marker);
        update();
    }

    public void removeMarker(Marker marker){
        markers.removeElement(marker);
        if(selectedMarker == marker){
            selectedMarker = null;
        }
    }

    public void onMapZoomed(int zoom) {
        super.onMapZoomed(zoom);
        update();
    }

    protected void update(){
        if(markers == null){
            return;
        }
        for(Enumeration e = markers.elements(); e.hasMoreElements(); ){
            Marker marker = (Marker)e.nextElement();
            updateMarkerScreenPosition(marker);
        }

        //убираем фокус с выделенного маркера, если далеко от него отодвинулись
        PixelPoint screenCenter = new PixelPoint(screenWidth / 2, screenHeight / 2);
        if(selectedMarker != null
                && getPointsDistancePx(screenCenter, selectedMarker.getScreenPosition()) > SELECTION_RADIUS){
            selectedMarker.onFocusLost();
            selectedMarker = null;
        }
        //ищем маркер, на который можно поставить фокус
        Marker markerToSelect = getSelectedMarker(screenCenter.getX(), screenCenter.getY());
        if(markerToSelect != null && selectedMarker != markerToSelect){
            if(selectedMarker != null){
                selectedMarker.onFocusLost();
            }
            markerToSelect.onFocused();
            selectedMarker = markerToSelect;
        }

        viewUpdater.updateView();
    }

    protected Marker getSelectedMarker(int x, int y){
        PixelPoint point = new PixelPoint(x, y);
        double minMarkerDistanceToCenter = Double.MAX_VALUE;
        Marker markerToSelect = null;

        for(Enumeration e = markers.elements(); e.hasMoreElements(); ){
            Marker marker = (Marker)e.nextElement();
            double markerDistanceToCenter = getPointsDistancePx(point, marker.getScreenPosition());
            if(markerDistanceToCenter < minMarkerDistanceToCenter
                    && markerDistanceToCenter < SELECTION_RADIUS
                    && marker.isFocusable()){
                minMarkerDistanceToCenter = markerDistanceToCenter;
                markerToSelect = marker;
            }
        }
        return markerToSelect;
    }

    protected double getPointsDistancePx(PixelPoint p1, PixelPoint p2){
        if(p1 == null || p2 ==null){
            return Double.MAX_VALUE;
        }
        int dx = p1.getX() - p2.getX();
        int dy = p1.getY() - p2.getY();
        return Math.sqrt(dx*dx + dy*dy);
    }

    protected void updateMarkerScreenPosition(Marker marker){
        if(mapCenter == null || marker.getCoordinate() == null){
            return;
        }
        int x = GeoConverter.getXPixelFromCoordinate(mapCenter, marker.getCoordinate(), screenWidth, zoom);
        int y = GeoConverter.getYPixelFromCoordinate(mapCenter, marker.getCoordinate(), screenHeight, zoom);
        marker.setScreenPosition(new PixelPoint(x, y));
    }

    public void paint(Graphics graphics) {
        if(markers == null){
            return;
        }
        for(Enumeration e = markers.elements(); e.hasMoreElements(); ){
            ((Marker)e.nextElement()).paint(graphics);
        }
        if(selectedMarker != null){
            selectedMarker.paint(graphics);
        }
    }

    public void onScreenSizeChanged(int screenWidth, int screenHeight) {
        super.onScreenSizeChanged(screenWidth, screenHeight);
        update();
    }

    public void onMapMoved(Coordinate mapCenter) {
        super.onMapMoved(mapCenter);
        update();
    }

    public void onPointerPressed(int x, int y) {
        Marker marker = getSelectedMarker(x, y);
        if(marker != null){
            marker.onClicked();
        }
    }

    public void onAction() {
        if(selectedMarker != null){
            selectedMarker.onClicked();
        }
    }
}
