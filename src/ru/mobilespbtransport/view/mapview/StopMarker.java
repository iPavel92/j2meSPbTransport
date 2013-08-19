package ru.mobilespbtransport.view.mapview;

import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.model.Coordinate;
import ru.mobilespbtransport.model.Stop;
import ru.mobilespbtransport.model.StopsGroup;
import ru.mobilespbtransport.view.ArrivingScreen;
import ru.mobilespbtransport.view.ScreenStack;

import javax.microedition.lcdui.Graphics;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 19.08.13
 * Time: 13:15
 * To change this template use File | Settings | File Templates.
 */
public class StopMarker extends AbstractMarker {
    private final static int STOP_RADIUS = 6;
    private final static int SELECTED_STOP_RADIUS = 10;
    private final Stop stop;

    public StopMarker(Stop stop) {
        this.stop = stop;
        setCoordinate(stop.getCoordinate());
    }

    public boolean isFocusable() {
        return true;
    }

    public void onClicked() {
        ArrivingScreen arrivingScreen = new ArrivingScreen(new StopsGroup(stop));
        ScreenStack.push(arrivingScreen);
        Controller.updateArrivingScreen(stop, arrivingScreen);
    }

    public void paint(Graphics graphics) {
        graphics.setColor(0x000000);
        if(isFocused){
            graphics.fillArc(
                    position.getX() - SELECTED_STOP_RADIUS,
                    position.getY() - SELECTED_STOP_RADIUS,
                    2 * SELECTED_STOP_RADIUS,
                    2 * SELECTED_STOP_RADIUS,
                    0, 360);
        }

        //border
        graphics.fillArc(
                position.getX() - STOP_RADIUS - 1,
                position.getY() - STOP_RADIUS - 1,
                2 * STOP_RADIUS + 2,
                2 * STOP_RADIUS + 2,
                0, 360);

        //filling
        graphics.setColor(stop.getTransportType().getColor());
        graphics.fillArc(
                position.getX() - STOP_RADIUS,
                position.getY() - STOP_RADIUS,
                2 * STOP_RADIUS,
                2 * STOP_RADIUS,
                0, 360);
    }
}
