package ru.mobilespbtransport.model;

import javax.microedition.lcdui.Image;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 19.05.12
 * Time: 17:04
 * To change this template use File | Settings | File Templates.
 */

public class TransportType {
	public static final int BUS = 0;
	public static final int TROLLEY = 1;
	public static final int TRAM = 2;

	private int type;

	public TransportType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public Image getIcon() {
		try {
			switch (type) {
				case BUS:
					return Image.createImage("/bus.png");
				case TROLLEY:
					return Image.createImage("/trolley.png");
				case TRAM:
					return Image.createImage("/tram.png");
			}
		} catch (IOException e) {
			//TODO:
			e.printStackTrace();
		}
		return null;
	}
}
