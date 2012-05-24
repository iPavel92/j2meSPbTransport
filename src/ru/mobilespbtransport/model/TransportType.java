package ru.mobilespbtransport.model;

import ru.mobilespbtransport.view.ImagePool;

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
		switch (type) {
			case BUS:
				return ImagePool.getImage("/bus.png");
			case TROLLEY:
				return ImagePool.getImage("/trolley.png");
			case TRAM:
				return ImagePool.getImage("/tram.png");
		}
		return null;
	}

	public Image getArrivingImage() {
		switch (type) {
			case BUS:
				return ImagePool.getImage("/arriving_bus.png");
			case TROLLEY:
				return ImagePool.getImage("/arriving_trolley.png");
			case TRAM:
				return ImagePool.getImage("/arriving_tram.png");
		}
		return null;
	}

	public int getColor() {
		switch (type) {
			case BUS:
				return 0xA2A8B1;
			case TROLLEY:
				return 0x7190D6;
			case TRAM:
				return 0xAD3F47;
		}
		return 0x000000;
	}

	public String toString() {
		return "TransportType{" +
				"type=" + (type == BUS ? "bus" : (type == TROLLEY ? "trolley" : "tram")) +
				'}';
	}
}
