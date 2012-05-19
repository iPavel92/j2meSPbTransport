package ru.mobilespbtransport.screens;

import ru.mobilespbtransport.Controller;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 04.05.12
 * Time: 20:44
 * To change this template use File | SettingsScreen | File Templates.
 */
public class MapScreen extends Canvas {
	private final Controller controller;
	private Image map;
	private Image transportLayer;
	public String olo = "";

	public MapScreen(Controller controller) {
		this.controller = controller;
	}

	protected void paint(Graphics graphics) {
		graphics.setColor(0xffffff);
		graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
		if (map != null) {
			graphics.drawImage(map, 0, 0, Graphics.TOP | Graphics.LEFT);
		}
		if (transportLayer != null) {
			graphics.drawImage(transportLayer, 0, 0, Graphics.TOP | Graphics.LEFT);
		}
		graphics.setColor(0, 0, 255);
		graphics.drawString(olo, 20, 50, Graphics.TOP | Graphics.LEFT);
		System.out.println(olo);
	}

	public void setMap(Image map) {
		this.map = map;
	}

	public void setTransportLayer(Image transportLayer) {
		this.transportLayer = transportLayer;
	}

	protected void keyPressed(int i) {
		controller.loadTransportLayer();

		controller.doMagic();

	}

	protected void pointerPressed(int i, int i1) {
		controller.loadTransportLayer();
	}
}

