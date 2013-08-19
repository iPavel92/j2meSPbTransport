package ru.mobilespbtransport;

import ru.mobilespbtransport.location.*;
import ru.mobilespbtransport.model.Coordinate;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;

public class CellIdTest extends MIDlet implements Runnable {

    Display display;
    static Form form;

    public void startApp() {
		try {
			display = Display.getDisplay(this);
			form = new Form("Cell id location");
			display.setCurrent(form);

			new Thread(this).start();
		} catch (Throwable e) {
			Alert message = new Alert("info");
			message.setString(e.toString());
			Display.getDisplay(this).setCurrent(message);
		}
	}

    public void run() {
		try {
			form.append("Platform: " + System.getProperty("microedition.platform")+"\n");
			form.append("Cell id: " + DeviceInfo.getCellId()+"\n");
			form.append("LAC: " + DeviceInfo.getLAC()+"\n");
			form.append("IMSI: " + DeviceInfo.getIMSI()+"\n");
			form.append("MCC: " + DeviceInfo.getMCC()+"\n");
			form.append("MNC: " + DeviceInfo.getMNC()+"\n");
			form.append("IMEI: " + DeviceInfo.getIMEI()+"\n");

			//геоданные
			Coordinate coordinate = Locator.getLocation();
			if(coordinate == null){
				form.append("Can't get position\n");
			} else {
				form.append("Lat: "+coordinate.getLat()+ "\n");
				form.append("Lon: "+coordinate.getLon()+ "\n");
			}
		} catch (Throwable e) {
			form.append(e.toString());
		}
	}

	public static void log(String s){
		 form.append(s);
	}

    public void pauseApp() {
    }

    public void destroyApp(boolean flag) {
    }
}