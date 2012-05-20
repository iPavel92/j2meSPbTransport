package ru.mobilespbtransport.cache;

import ru.mobilespbtransport.model.*;

import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 1:39
 * To change this template use File | Settings | File Templates.
 */
public class Cache {
	private static RecordStore recordStore;
	private static final String MODEL = "model";

	private static final int PLACE = 0;
	private static final int STOP = 1;

	public static void saveModel(Model model) {
		try {
			recordStore = RecordStore.openRecordStore(MODEL, true);

			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			DataOutputStream writer = new DataOutputStream(byteStream);
			writer.writeBoolean(model.isShowBus());
			writer.writeBoolean(model.isShowTrolley());
			writer.writeBoolean(model.isShowTram());
			writer.writeBoolean(model.isUseAutoUpdate());
			int n = model.getFavourites().size();
			writer.writeInt(n);
			for (int i = 0; i < n; i++) {
				Place c = (Place) model.getFavourites().elementAt(i);
				writer.writeUTF(c.getName());
				writer.writeDouble(c.getCoordinate().toWGS84().getLat());
				writer.writeDouble(c.getCoordinate().toWGS84().getLon());
				if (c instanceof Stop) {
					writer.writeInt(STOP);
					Stop stop = (Stop) c;
					writer.writeInt(stop.getTransportType().getType());
					writer.writeInt(stop.getId());
				} else {
					writer.writeInt(PLACE);
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

	public static Model loadModel() {
		Model model = new Model();
		try {
			ByteArrayInputStream stream;
			DataInputStream reader;

			recordStore = RecordStore.openRecordStore(MODEL, true);
			if (recordStore.getNumRecords() == 0) {
				return model;
			}

			byte[] rec = new byte[recordStore.getRecordSize(1)];
			rec = recordStore.getRecord(1);

			stream = new ByteArrayInputStream(rec);
			reader = new DataInputStream(stream);
			model.setShowBus(reader.readBoolean());
			model.setShowTrolley(reader.readBoolean());
			model.setShowTram(reader.readBoolean());
			model.setUseAutoUpdate(reader.readBoolean());
			int n = reader.readInt();
			for (int i = 0; i < n; i++) {
				String name = reader.readUTF();
				double lat = reader.readDouble();
				double lon = reader.readDouble();
				int type = reader.readInt();
				Coordinate coordinate = new Coordinate(lat, lon, Coordinate.WGS84);
				if (type == STOP) {
					int transportType = reader.readInt();
					int id = reader.readInt();
					Stop stop = new Stop(name, coordinate, new TransportType(transportType), id);
					model.getFavourites().addElement(stop);
				} else {
					Place place = new Place(name, coordinate);
					model.getFavourites().addElement(place);
				}
			}
			recordStore.closeRecordStore();
		} catch (RecordStoreException e) {
			e.printStackTrace();  //TODO
		} catch (IOException e) {
			e.printStackTrace();  //TODO
		}
		return model;
	}

	private static String composeImageKey(Place place) {
		String key = "map_" + place.getCoordinate().toWGS84().getLat() + "_" + place.getCoordinate().toWGS84().getLon();
		if (key.length() > 32) {
			key = key.substring(0, 32);
		}
		return key;
	}

	public static void saveImage(Place place, byte[] image) {
		String key = composeImageKey(place);
		System.out.println(key);
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

	public static Image loadImage(Place place) {
		String key = composeImageKey(place);
		Image image = null;
		try {
			ByteArrayInputStream stream;
			DataInputStream reader;

			recordStore = RecordStore.openRecordStore(key, true);
			System.out.println(recordStore.getNumRecords());
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

	public static boolean isImageExists(Place place) {
		String key = composeImageKey(place);
		try {
			recordStore = RecordStore.openRecordStore(key, false);
			recordStore.closeRecordStore();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
