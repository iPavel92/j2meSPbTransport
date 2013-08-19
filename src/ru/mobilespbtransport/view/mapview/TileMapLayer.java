package ru.mobilespbtransport.view.mapview;

import ru.mobilespbtransport.cache.Cache;
import ru.mobilespbtransport.controller.Controller;
import ru.mobilespbtransport.model.Coordinate;
import ru.mobilespbtransport.network.HttpClient;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.util.Enumeration;
import java.util.Vector;

public class TileMapLayer extends AbstractMapLayer {
    private final static String LOADING = "Загрузка...";
    private static final int MAX_ZOOM = 18;
	private static final int MIN_ZOOM = 2;
	private Vector tiles = new Vector();

	public void onScreenSizeChanged(int screenWidth, int screenHeight) {
		super.onScreenSizeChanged(screenWidth, screenHeight);
		updateMapTiles();
	}

	public void onMapMoved(Coordinate mapCenter) {
		super.onMapMoved(mapCenter);
		updateMapTiles();
	}

	public void onMapZoomed(int zoom) {
		super.onMapZoomed(zoom);
		if(zoom > MAX_ZOOM || zoom < MIN_ZOOM){
			return;
		}
		tiles.removeAllElements();
		updateMapTiles();
	}

	private void updateMapTiles() {
		if(mapCenter == null || zoom > MAX_ZOOM || zoom < MIN_ZOOM){
			return;
		}

		updateTileScreenCoordinates();
		removeInvisibleTiles();
		fillMapWithTiles();
	}

	private void removeInvisibleTiles(){
		for(int i = tiles.size() - 1; i >=0; i--){
			Tile tile = (Tile)tiles.elementAt(i);
			if(!isTileVisible(tile)){
				removeTile(tile);
			}
		}
	}

	private void updateTileScreenCoordinates(){
		for(int i = tiles.size() - 1; i >=0; i--){
			Tile tile = (Tile)tiles.elementAt(i);
			tile.setScreenCoordinate(getTilePixelPosition(tile, mapCenter));
		}
	}

	private void fillMapWithTiles(){
		Tile centerTile = new Tile(mapCenter, zoom);
		centerTile.setScreenCoordinate(getTilePixelPosition(centerTile, mapCenter));

		int topPxCoordinate = centerTile.getScreenCoordinate().getY();
		int minY = centerTile.getTileY();
		while (topPxCoordinate > 0) {
			topPxCoordinate -= Tile.SIZE_PX;
			minY--;
		}

		int leftPxCoordinate = centerTile.getScreenCoordinate().getX();
		int minX = centerTile.getTileX();
		while (leftPxCoordinate > 0) {
			leftPxCoordinate -= Tile.SIZE_PX;
			minX--;
		}

		int rightPxCoordinate = centerTile.getScreenCoordinate().getX() + Tile.SIZE_PX;
		int maxX = centerTile.getTileX();
		while (rightPxCoordinate < screenWidth) {
			rightPxCoordinate += Tile.SIZE_PX;
			maxX++;
		}

		int bottomPxCoordinate = centerTile.getScreenCoordinate().getY() + Tile.SIZE_PX;
		int maxY = centerTile.getTileY();
		while (bottomPxCoordinate < screenHeight) {
			bottomPxCoordinate += Tile.SIZE_PX;
			maxY++;
		}

		for(int x = minX; x <= maxX; x++){
			for(int y = minY; y <= maxY; y++){
				addTile(new Tile(x, y, zoom));
			}
		}
	}

	private boolean isTileVisible(Tile tile) {
		PixelPoint leftUp = tile.getScreenCoordinate();
		PixelPoint rightBottom = new PixelPoint(leftUp.getX() + Tile.SIZE_PX, leftUp.getY() + Tile.SIZE_PX);
		if (leftUp.getX() > screenWidth) return false;
		if (leftUp.getY() > screenHeight) return false;
		if (rightBottom.getX() < 0) return false;
		if (rightBottom.getY() < 0) return false;
		return true;
	}

	private void addTile(Tile tile) {
		if (!tiles.contains(tile)) {
			tiles.addElement(tile);
			tile.setScreenCoordinate(getTilePixelPosition(tile, mapCenter));
			loadTile(tile);
		}
	}

	private void removeTile(Tile tile) {
		tiles.removeElement(tile);
	}

	private PixelPoint getTilePixelPosition(Tile tile, Coordinate coordinate) {
		BoundingBox tileBoundingBox = tile.getBoundingBox();
		int dx = (int) ((tileBoundingBox.getWest() - coordinate.getLon()) * Tile.SIZE_PX / (tileBoundingBox.getEast() - tileBoundingBox.getWest())) + screenWidth / 2;
		int dy = (int) ((coordinate.getLat() - tileBoundingBox.getNorth()) * Tile.SIZE_PX / (tileBoundingBox.getNorth() - tileBoundingBox.getSouth())) + screenHeight / 2;
		return new PixelPoint(dx, dy);
	}

	private void loadTile(final Tile tile) {
		Controller.sheduleTask(new Runnable() {
			public void run() {
				Image tileImage = loadTileImage(tile);
				tile.setImage(tileImage);
				viewUpdater.updateView();
			}
		});
	}

	private Image loadTileImage(Tile tile) {
		if (Cache.isTileImageExists(tile)) {
			System.out.println("Tile found in cache");
			return Cache.loadTileImage(tile);
		} else {
			System.out.println("Tile not found in cache");
			String url = tile.getTileUrl();
			byte[] imageData = HttpClient.loadImageBytes(url);
			System.out.println("Tile image loaded");
			Cache.saveTileImage(tile, imageData);
			System.out.println("Tile image saved");
			Image img = Image.createImage(imageData, 0, imageData.length);
			return (img == null ? null : img);
		}
	}

	public void paint(Graphics graphics) {
		if(mapCenter == null || zoom > MAX_ZOOM || zoom < MIN_ZOOM){
			return;
		}

		for(Enumeration e = tiles.elements(); e.hasMoreElements(); ){
			Tile tile = (Tile)e.nextElement();
            if(tile.getScreenCoordinate() == null){
                continue;
            }
			if(tile.getImage() != null){
				graphics.drawImage(
						tile.getImage(),
						tile.getScreenCoordinate().getX(),
						tile.getScreenCoordinate().getY(),
						Graphics.TOP | Graphics.LEFT);
			} else {
                graphics.setColor(0x666666);
                graphics.drawString(LOADING,
                        tile.getScreenCoordinate().getX() + Tile.SIZE_PX/2,
                        tile.getScreenCoordinate().getY() + Tile.SIZE_PX/2,
                        Graphics.BASELINE | Graphics.HCENTER);
            }
		}
	}
}
