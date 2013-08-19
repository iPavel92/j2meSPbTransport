package ru.mobilespbtransport.view.mapview;

import akme.mobile.util.MathUtil;
import ru.mobilespbtransport.model.Coordinate;

import javax.microedition.lcdui.Image;

public class Tile {
	public static final int SIZE_PX = 256;

	private final int tileX;
	private final int tileY;
	private final int zoom;

	private Image image;
	private PixelPoint screenCoordinate;


	public Tile(Coordinate coordinate, int zoom) {
		this(
				lonToTileX(coordinate.getLon(), zoom),
				latToTileY(coordinate.getLat(), zoom),
				zoom);
	}

	public Tile(int x, int y, int zoom) {
		this.tileX = x;
		this.tileY = y;
		this.zoom = zoom;
	}

	public PixelPoint getScreenCoordinate() {
		return screenCoordinate;
	}

	public void setScreenCoordinate(PixelPoint screenCoordinate) {
		this.screenCoordinate = screenCoordinate;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public int getTileX() {
		return tileX;
	}

	public int getTileY() {
		return tileY;
	}

	public int getZoom() {
		return zoom;
	}

	public String getTilePath() {
		return (zoom + "/" + tileX + "/" + tileY);
	}

	public String getTileUrl() {
		//String urlBase = "http://tile.openstreetmap.org/"; //alternative renderer
		String urlBase = "http://otile1.mqcdn.com/tiles/1.0.0/map/";
		String imageFormat = ".png";
		return urlBase + getTilePath() + imageFormat;
	}

	public BoundingBox getBoundingBox() {
		BoundingBox bb = new BoundingBox(
				tileToLon(tileX, zoom),
				tileToLon(tileX + 1, zoom),
				tileToLat(tileY + 1, zoom),
				tileToLat(tileY, zoom)
		);
		return bb;
	}

	private static int lonToTileX(double lon, int zoom) {
		return (int) Math.floor((lon + 180) / 360 * (1 << zoom));
	}

	private static int latToTileY(double lat, int zoom) {
		return (int) Math.floor((1 - MathUtil.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
	}

	private static double tileToLon(int x, int z) {
		return x / MathUtil.pow(2.0, z) * 360.0 - 180;
	}

	private static double tileToLat(int y, int z) {
		double n = Math.PI - (2.0 * Math.PI * y) / MathUtil.pow(2.0, z);
		return Math.toDegrees(MathUtil.atan(MathUtil.sinh(n)));
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Tile)) return false;

		Tile tile = (Tile) o;

		if (tileX != tile.tileX) return false;
		if (tileY != tile.tileY) return false;
		if (zoom != tile.zoom) return false;

		return true;
	}

	public int hashCode() {
		int result = tileX;
		result = 31 * result + tileY;
		result = 31 * result + zoom;
		return result;
	}
}

