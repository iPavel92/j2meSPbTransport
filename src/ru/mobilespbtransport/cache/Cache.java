package ru.mobilespbtransport.cache;

import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.location.CellData;
import ru.mobilespbtransport.model.*;
import ru.mobilespbtransport.view.mapview.Tile;

import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 1:39
 * To change this template use File | Settings | File Templates.
 */
public class Cache {
	private static RecordStore recordStore;
	private static final int VERSION = 3;
	private static final String MODEL = "model_" + VERSION;
	private static final String STOPS = "stops_" + VERSION;
	private static final String ROUTES = "routes_" + VERSION;
	private static final String CELL_ID_LOCATIONS = "cell_id_locations" + VERSION;

	private static final int PLACE = 0;
	private static final int STOP_GROUP = 1;
	private static final int ROUTE = 2;

	public static void saveModel() {
		try {
			System.out.println("Saving model");
			recordStore = RecordStore.openRecordStore(MODEL, true);

			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			DataOutputStream writer = new DataOutputStream(byteStream);

			Model model = Controller.getModel();

            writer.writeBoolean(model.getLastCoordinate() != null);
            if(model.getLastCoordinate() != null){
                writer.writeDouble(model.getLastCoordinate().getLat());
                writer.writeDouble(model.getLastCoordinate().getLon());
                writer.writeInt(model.getLastZoom());
            }
			writer.writeBoolean(model.isShowBus());
			writer.writeBoolean(model.isShowTrolley());
			writer.writeBoolean(model.isShowTram());
			writer.writeBoolean(model.isUseAutoUpdate());
			int n = model.getFavourites().size();
			writer.writeInt(n);
			for (int i = 0; i < n; i++) {
				Object obj = model.getFavourites().elementAt(i);
				if (obj instanceof StopsGroup) {
					writer.writeInt(STOP_GROUP);
					StopsGroup stopsGroup = (StopsGroup) obj;
					writer.writeUTF(stopsGroup.getName());
					int m = stopsGroup.getStops().size();
					writer.writeInt(m);
					for (int j = 0; j < m; j++) {
						saveStop((Stop) stopsGroup.getStops().elementAt(j), writer);
					}
				} else if (obj instanceof Place) {
					writer.writeInt(PLACE);
					Place place = (Place) obj;
					writer.writeUTF(place.getName());
					writer.writeDouble(place.getCoordinate().toWGS84().getLat());
					writer.writeDouble(place.getCoordinate().toWGS84().getLon());
				} else if (obj instanceof Route) {
					writer.writeInt(ROUTE);
					Route route = (Route) obj;
					saveRoute(route, writer);
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

            boolean hasLastCoordinate = reader.readBoolean();
            if(hasLastCoordinate){
                Coordinate lastCoordinate = new Coordinate(reader.readDouble(), reader.readDouble());
                model.setLastCoordinate(lastCoordinate);
                model.setLastZoom(reader.readInt());
            } else {
                model.setLastCoordinate(null);
            }
			model.setShowBus(reader.readBoolean());
			model.setShowTrolley(reader.readBoolean());
			model.setShowTram(reader.readBoolean());
			model.setUseAutoUpdate(reader.readBoolean());
			int n = reader.readInt();
			for (int i = 0; i < n; i++) {
				int type = reader.readInt();
				if (type == STOP_GROUP) {
					String name = reader.readUTF();
					int m = reader.readInt();
					Vector stops = new Vector(m);
					for (int j = 0; j < m; j++) {
						Stop stop = loadStop(reader);
						stops.addElement(stop);
					}
					StopsGroup stopsGroup = new StopsGroup(name, stops);
					model.getFavourites().addElement(stopsGroup);
				} else if (type == PLACE) {
					String name = reader.readUTF();
					double lat = reader.readDouble();
					double lon = reader.readDouble();
					Coordinate coordinate = new Coordinate(lat, lon, Coordinate.WGS84);
					Place place = new Place(name, coordinate);
					model.getFavourites().addElement(place);
				} else if (type == ROUTE) {
					Route route = loadRoute(reader);
					model.getFavourites().addElement(route);
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
				saveRoute(route, writer);
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
				Route route = loadRoute(reader);
				Controller.addRoute(route);
			}
			recordStore.closeRecordStore();
		} catch (RecordStoreException e) {
			e.printStackTrace();  //TODO
		} catch (IOException e) {
			e.printStackTrace();  //TODO
		}
	}

	private static void saveRoute(Route route, DataOutputStream writer) throws IOException {
		writer.writeInt(route.getId());
		writer.writeUTF(route.getRouteNumber());
		writer.writeInt(route.getTransportType().getType());
		writer.writeBoolean(route.isStopsLoaded());

		int m = route.getStopsId().size();
		writer.writeInt(m);
		for (int j = 0; j < m; j++) {
			writer.writeInt(((Integer) route.getStopsId().elementAt(j)).intValue());
		}
	}

	private static Route loadRoute(DataInputStream reader) throws IOException {
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
		return route;
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

	//////////////////////////


	private static String composeTileKey(Tile tile) {
		String key = "tile_" + tile.getTilePath();
		return key;
	}

	public static void saveTileImage(Tile tile, byte[] image) {
		String key = composeTileKey(tile);
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

	public static Image loadTileImage(Tile tile) {
		String key = composeTileKey(tile);
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

	public static boolean isTileImageExists(Tile tile) {
		String key = composeTileKey(tile);
		try {
			recordStore = RecordStore.openRecordStore(key, false);
			recordStore.closeRecordStore();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/////////////////////////////

	public static void saveLocationsCache(Hashtable locationsCache) {
		try {
			recordStore = RecordStore.openRecordStore(CELL_ID_LOCATIONS, true);

			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			DataOutputStream writer = new DataOutputStream(byteStream);

			int n = locationsCache.size();
			writer.writeInt(n);
			Enumeration e = locationsCache.keys();
			while (e.hasMoreElements()) {
				CellData cellData = (CellData) e.nextElement();
				writer.writeInt(cellData.getMcc());
				writer.writeInt(cellData.getMnc());
				writer.writeInt(cellData.getCellId());
				writer.writeInt(cellData.getLac());

				Coordinate coordinate = (Coordinate) locationsCache.get(cellData);
				writer.writeDouble(coordinate.getLat());
				writer.writeDouble(coordinate.getLon());
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

	public static void loadLocationsCache(Hashtable locationsCache) {
		try {
			ByteArrayInputStream stream;
			DataInputStream reader;

			recordStore = RecordStore.openRecordStore(CELL_ID_LOCATIONS, true);
			if (recordStore.getNumRecords() == 0) {
				return;
			}

			byte[] rec = recordStore.getRecord(1);

			stream = new ByteArrayInputStream(rec);
			reader = new DataInputStream(stream);

			int n = reader.readInt();
			for (int i = 0; i < n; i++) {
				CellData cellData = new CellData(
						reader.readInt(),
						reader.readInt(),
						reader.readInt(),
						reader.readInt()
				);
				Coordinate coordinate = new Coordinate(
						reader.readDouble(),
						reader.readDouble()
				);
				locationsCache.put(cellData, coordinate);
			}
			recordStore.closeRecordStore();
		} catch (RecordStoreException e) {
			e.printStackTrace();  //TODO
		} catch (IOException e) {
			e.printStackTrace();  //TODO
		}
	}
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          