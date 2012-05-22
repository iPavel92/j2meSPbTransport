package ru.mobilespbtransport.cache;

import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.model.*;

import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import java.io.*;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 1:39
 * To change this template use File | Settings | File Templates.
 */
public class Cache {
	private static RecordStore recordStore;
	private static final int VERSION = 2;
	private static final String MODEL = "model_" + VERSION;
	private static final String STOPS = "stops_" + VERSION;
	private static final String ROUTES = "routes_" + VERSION;

	private static final int PLACE = 0;
	private static final int STOP = 1;

	public static void saveModel() {
		try {
			System.out.println("Saving model");
			recordStore = RecordStore.openRecordStore(MODEL, true);

			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			DataOutputStream writer = new DataOutputStream(byteStream);
			
			Model model = Controller.getModel();
			
			writer.writeBoolean(model.isShowBus());
			writer.writeBoolean(model.isShowTrolley());
			writer.writeBoolean(model.isShowTram());
			writer.writeBoolean(model.isUseAutoUpdate());
			int n = model.getFavourites().size();
			writer.writeInt(n);
			for (int i = 0; i < n; i++) {
				Object obj = model.getFavourites().elementAt(i);
				if (obj instanceof Stop) {
					writer.writeInt(STOP);
					Stop stop = (Stop) obj;
					saveStop(stop, writer);
				} else if (obj instanceof Place) {
					writer.writeInt(PLACE);
					Place place = (Place) obj;
					writer.writeUTF(place.getName());
					writer.writeDouble(place.getCoordinate().toWGS84().getLat());
					writer.writeDouble(place.getCoordinate().toWGS84().getLon());
				}
			}
			writer.flush();

			byte[] rec = byteStream.toByteArray();
			if (recordStore.getNumRecords() == 0) {
				recordStore.addRecord(rec, 0, rec.length);
			} else {
				recordStore.setRecord(1, rec, 0, rec.length);
			}
			recordStore.closeRecordStore();
		} catch (RecordStoreException e) {
			e.printStackTrace();  //TODO
		} catch (IOException e) {
			e.printStackTrace();  //TODO
		}
	}

	public static void loadModel() {
		try {
			System.out.println("Loading model");
			ByteArrayInputStream stream;
			DataInputStream reader;

			recordStore = RecordStore.openRecordStore(MODEL, true);
			if (recordStore.getNumRecords() == 0) {
				return;
			}

			byte[] rec = recordStore.getRecord(1);

			stream = new ByteArrayInputStream(rec);
			reader = new DataInputStream(stream);

			Model model = new Model();

			model.setShowBus(reader.readBoolean());
			model.setShowTrolley(reader.readBoolean());
			model.setShowTram(reader.readBoolean());
			model.setUseAutoUpdate(reader.readBoolean());
			int n = reader.readInt();
			for (int i = 0; i < n; i++) {
				int type = reader.readInt();
				if (type == STOP) {
					Stop stop = loadStop(reader);
					model.getFavourites().addElement(stop);
				} else if (type == PLACE) {
					String name = reader.readUTF();
					double lat = reader.readDouble();
					double lon = reader.readDouble();
					Coordinate coordinate = new Coordinate(lat, lon, Coordinate.WGS84);
					Place place = new Place(name, coordinate);
					model.getFavourites().addElement(place);
				}
			}

			Controller.setModel(model);
			recordStore.closeRecordStore();
		} catch (RecordStoreException e) {
			e.printStackTrace();  //TODO
		} catch (IOException e) {
			e.printStackTrace();  //TODO
		}
	}

	////////////////////////////////////////////////

	public static void saveRoutes() {
		try {
			System.out.println("Saving routes");
			recordStore = RecordStore.openRecordStore(ROUTES, true);

			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			DataOutputStream writer = new DataOutputStream(byteStream);

			int n = Controller.getModel().getRoutes().size();
			writer.writeInt(n);
			Enumeration e = Controller.getModel().getRoutes().keys();
			while (e.hasMoreElements()) {
				int key = ((Integer) e.nextElement()).intValue();
				Route route = Controller.getRoute(key);
				writer.writeInt(route.getId());
				writer.writeUTF(route.getRouteNumber());
				writer.writeInt(route.getTransportType().getType());
				writer.writeBoolean(route.isStopsLoaded());

				int m = route.getStopsId().size();
				writer.writeInt(m);
				for (int j = 0; j < m; j++) {
					writer.writeInt(((Integer) route.getStopsId().elementAt(j)).intValue());
				}
				//System.out.println(route + " stops: " + m);
			}
			writer.flush();

			byte[] rec = byteStream.toByteArray();
			if (recordStore.getNumRecords() == 0) {
				recordStore.addRecord(rec, 0, rec.length);
			} else {
				recordStore.setRecord(1, rec, 0, rec.length);
			}
			recordStore.closeRecordStore();
		} catch (RecordStoreException e) {
			e.printStackTrace();  //TODO
		} catch (IOException e) {
			e.printStackTrace();  //TODO
		}
	}

	public static void loadRoutes() {
		try {
			System.out.println("Loading routes");
			ByteArrayInputStream stream;
			DataInputStream reader;

			recordStore = RecordStore.openRecordStore(ROUTES, true);
			if (recordStore.getNumRecords() == 0) {
				return;
			}

			byte[] rec = recordStore.getRecord(1);

			stream = new ByteArrayInputStream(rec);
			reader = new DataInputStream(stream);

			int n = reader.readInt();
			for (int i = 0; i < n; i++) {
				int id = reader.readInt();
				String routeNumber = reader.readUTF();
				TransportType transportType = new TransportType(reader.readInt());
				boolean isStopsLoaded = reader.readBoolean();

				Route route = new Route(transportType, routeNumber, id);
				route.setStopsLoaded(isStopsLoaded);
				
				int m = reader.readInt();
				for (int j = 0; j < m; j++) {
					route.addStopId(reader.readInt());
				}
				Controller.addRoute(route);
			}
			recordStore.closeRecordStore();
		} catch (RecordStoreException e) {
			e.printStackTrace();  //TODO
		} catch (IOException e) {
			e.printStackTrace();  //TODO
		}
	}

	////////////////////////////////////////////////

	public static void saveStops() {
		try {
			System.out.println("Saving stops");
			recordStore = RecordStore.openRecordStore(STOPS, true);

			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			DataOutputStream writer = new DataOutputStream(byteStream);

			int n = Controller.getModel().getStops().size();
			writer.writeInt(n);
			Enumeration e = Controller.getModel().getStops().keys();
			while (e.hasMoreElements()) {
				int key = ((Integer) e.nextElement()).intValue();
				Stop stop = Controller.getStop(key);
				saveStop(stop, writer);
			}
			writer.flush();

			byte[] rec = byteStream.toByteArray();
			if (recordStore.getNumRecords() == 0) {
				recordStore.addRecord(rec, 0, rec.length);
			} else {
				recordStore.setRecord(1, rec, 0, rec.length);
			}
			recordStore.closeRecordStore();
		} catch (RecordStoreException e) {
			e.printStackTrace();  //TODO
		} catch (IOException e) {
			e.printStackTrace();  //TODO
		}
	}

	public static void loadStops() {
		try {
			System.out.println("Loading stops");
			ByteArrayInputStream stream;
			DataInputStream reader;

			recordStore = RecordStore.openRecordStore(STOPS, true);
			if (recordStore.getNumRecords() == 0) {
				return;
			}

			byte[] rec = recordStore.getRecord(1);

			stream = new ByteArrayInputStream(rec);
			reader = new DataInputStream(stream);

			int n = reader.readInt();
			for (int i = 0; i < n; i++) {
				Stop stop = loadStop(reader);
				Controller.addStop(stop);
			}
			recordStore.closeRecordStore();
		} catch (RecordStoreException e) {
			e.printStackTrace();  //TODO
		} catch (IOException e) {
			e.printStackTrace();  //TODO
		}
	}

	////////////////////////////////////////////////

	private static void saveStop(Stop stop, DataOutputStream writer) throws IOException {
		writer.writeInt(stop.getId());
		writer.writeUTF(stop.getName());
		writer.writeBoolean(stop.isDirect());
		writer.writeInt(stop.getTransportType().getType());
		writer.writeDouble(stop.getCoordinate().toWGS84().getLat());
		writer.writeDouble(stop.getCoordinate().toWGS84().getLon());
		int m = stop.getRoutesId().size();
		writer.writeInt(m);
		for (int j = 0; j < m; j++) {
			writer.writeInt(((Integer) stop.getRoutesId().elementAt(j)).intValue());
		}
	}

	private static Stop loadStop(DataInputStream reader) throws IOException {
		int id = reader.readInt();
		String name = reader.readUTF();
		boolean isDirect = reader.readBoolean();
		TransportType transportType = new TransportType(reader.readInt());
		double lat = reader.readDouble();
		double lon = reader.readDouble();
		Coordinate coordinate = new Coordinate(lat, lon, Coordinate.WGS84);

		Stop stop = new Stop(name, coordinate, transportType, id, isDirect);

		int m = reader.readInt();
		for (int j = 0; j < m; j++) {
			stop.addRouteId(reader.readInt());
		}

		return stop;
	}

	////////////////////////////////////////////////

	private static String composeImageKey(Place place, int zoom) {
		String key = "map_" + zoom + "_" + place.getCoordinate().toWGS84().getLat() + "_" + place.getCoordinate().toWGS84().getLon();
		if (key.length() > 32) {
			key = key.substring(0, 32);
		}
		return key;
	}

	public static void saveImage(Place place, byte[] image, int zoom) {
		String key = composeImageKey(place, zoom);
		try {
			recordStore = RecordStore.openRecordStore(key, true);

			if (recordStore.getNumRecords() == 0) {
				recordStore.addRecord(image, 0, image.length);
			} else {
				recordStore.setRecord(1, image, 0, image.length);
			}
			recordStore.closeRecordStore();
		} catch (RecordStoreException e) {
			e.printStackTrace();  //TODO
		}
	}

	public static Image loadImage(Place place, int zoom) {
		String key = composeImageKey(place, zoom);
		Image image = null;
		try {
			ByteArrayInputStream stream;
			DataInputStream reader;

			recordStore = RecordStore.openRecordStore(key, true);
			if (recordStore.getNumRecords() == 0) {
				return null;
			}

			byte[] rec = new byte[recordStore.getRecordSize(1)];
			rec = recordStore.getRecord(1);
			recordStore.closeRecordStore();

			Image im = null;
			im = Image.createImage(rec, 0, rec.length);
			return (im == null ? null : im);
		} catch (RecordStoreException e) {
			e.printStackTrace();  //TODO
			return null;
		}
	}

	public static boolean isImageExists(Place place, int zoom) {
		String key = composeImageKey(place, zoom);
		try {
			recordStore = RecordStore.openRecordStore(key, false);
			recordStore.closeRecordStore();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
