package ru.mobilespbtransport.view.mapview;

import ru.mobilespbtransport.cache.Cache;
import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.controller.GeoConverter;
import ru.mobilespbtransport.model.Coordinate;
import ru.mobilespbtransport.model.Stop;
import ru.mobilespbtransport.network.HttpClient;
import ru.mobilespbtransport.network.RequestGenerator;
import ru.mobilespbtransport.network.ResponseParser;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 19.08.13
 * Time: 13:54
 * To change this template use File | Settings | File Templates.
 */
public class StopsMarkersLayer extends MarkersLayer {
    private static final int MIN_ZOOM_TO_LOAD_STOPS = 15;

    public void onMapZoomed(int zoom) {
        super.onMapZoomed(zoom);
        if(zoom >= MIN_ZOOM_TO_LOAD_STOPS){
            loadStops();
            setVisible(true);
        } else {
            setVisible(false);
        }
    }

    public void onMapMoved(Coordinate mapCenter) {
        super.onMapMoved(mapCenter);
        if(zoom >= MIN_ZOOM_TO_LOAD_STOPS){
            loadStops();
        }
    }

    private void loadStops(){
        final Coordinate coordinateToLoad = mapCenter;
        final int zoomToLoad = this.zoom;
        Controller.sheduleTask(new Runnable() {
            public void run() {
                String url = "http://transport.orgp.spb.ru/Portal/transport/stops/list";
                String bBox = GeoConverter.buildBBox(coordinateToLoad, screenWidth, screenHeight, zoomToLoad);
                String request = RequestGenerator.getRequestForStopsOnMap(bBox);
                String response = HttpClient.sendPost(url, request);

                Vector stops = ResponseParser.parseStopsToMap(response); //Vector<Stop>
                for (Enumeration e = stops.elements(); e.hasMoreElements(); ) {
                    Stop stop = (Stop) e.nextElement();
                    Controller.addStop(stop); //to cached stops
                    StopMarker marker = new StopMarker(stop);
                    if(!getMarkers().contains(marker)){
                        addMarker(marker);
                    }
                }

                Cache.saveStops();
                Cache.saveRoutes();

                viewUpdater.updateView();
            }
        });
    }
}
