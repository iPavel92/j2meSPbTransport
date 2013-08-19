package ru.mobilespbtransport.location;

/**
 * Created with IntelliJ IDEA.
 * User: Павел
 * Date: 18.08.13
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
public class CellData {
	private final int mcc;
	private final int mnc;
	private final int cellId;
	private final int lac;

	public CellData(int mcc, int mnc, int cellId, int lac) {
		this.mcc = mcc;
		this.mnc = mnc;
		this.cellId = cellId;
		this.lac = lac;
	}

	public int getMcc() {
		return mcc;
	}

	public int getMnc() {
		return mnc;
	}

	public int getCellId() {
		return cellId;
	}

	public int getLac() {
		return lac;
	}
}
