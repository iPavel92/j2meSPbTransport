package ru.mobilespbtransport.cache;

import ru.mobilespbtransport.model.Place;
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
			writer.writeBoolean(model.isUseAutoUpdate());
			int n = model.getPlaces().size();
			writer.writeInt(n);
			for (int i = 0; i < n; i++) {
				Place c = (Place) model.getPlaces().elementAt(i);
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
				Place c = new Place(name, lat, lon);
				model.getPlaces().addElement(c);
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
		String key = "map_" + place.getLat() + "_" + place.getLon();
		if(key.length() > 32){
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
