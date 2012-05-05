package ru.mobilespbtransport.cache;

import ru.mobilespbtransport.model.Coordinate;
import ru.mobilespbtransport.model.Model;

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

	public static void saveModel(Model model) {
		try {
			recordStore = RecordStore.openRecordStore(MODEL, true);

			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			DataOutputStream writer = new DataOutputStream(byteStream);
			writer.writeBoolean(model.isShowBus());
			writer.writeBoolean(model.isShowTrolley());
			writer.writeBoolean(model.isShowTram());
			int n = model.getStops().size();
			writer.writeInt(n);
			for (int i = 0; i < n; i++) {
				Coordinate c = (Coordinate) model.getStops().elementAt(i);
				writer.writeUTF(c.getName());
				writer.writeDouble(c.getLat());
				writer.writeDouble(c.getLon());
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
			System.out.println(recordStore.getNumRecords());
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
			int n = reader.readInt();
			for (int i = 0; i < n; i++) {
				String name = reader.readUTF();
				double lat = reader.readDouble();
				double lon = reader.readDouble();
				Coordinate c = new Coordinate(name, lat, lon);
				model.getStops().addElement(c);
			}
			recordStore.closeRecordStore();
		} catch (RecordStoreException e) {
			e.printStackTrace();  //TODO
		} catch (IOException e) {
			e.printStackTrace();  //TODO
		}
		return model;
	}

	private static String composeImageKey(Coordinate coordinate) {
		return "map_" + coordinate.getLat() + "_" + coordinate.getLon();
	}

	public static void saveImage(Coordinate coordinate, byte[] image) {
		String key = composeImageKey(coordinate);
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

	public static Image loadImage(Coordinate coordinate) {
		String key = composeImageKey(coordinate);
		Image image = null;
		try {
			ByteArrayInputStream stream;
			DataInputStream reader;

			recordStore = RecordStore.openRecordStore(key, true);
			System.out.println(recordStore.getNumRecords());
			if (recordStore.getNumRecords() == 0) {
				return image;
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

	public static boolean isImageExists(Coordinate coordinate) {
		String key = composeImageKey(coordinate);
		try {
			recordStore = RecordStore.openRecordStore(key, false);
			recordStore.closeRecordStore();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
