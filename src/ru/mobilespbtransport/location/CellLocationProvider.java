package ru.mobilespbtransport.location;

import ru.mobilespbtransport.model.Coordinate;

public interface CellLocationProvider {
	public Coordinate getLocation(CellData cellData);
}
