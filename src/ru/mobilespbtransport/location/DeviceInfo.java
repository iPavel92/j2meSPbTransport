package ru.mobilespbtransport.location;

/**
 * Created with IntelliJ IDEA.
 * User: Павел
 * Date: 17.08.13
 * Time: 14:51
 * To change this template use File | Settings | File Templates.
 */
public class DeviceInfo {
	/**
	 * get the cell id in the phone
	 *
	 * @return
	 */
	public static int getCellId() {
		String out = null;
		boolean hex = false;

		try {
			//Nokia
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("com.nokia.mid.cellid");
			}
			//Sony Ericsson
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("com.sonyericsson.net.cellid");
				hex = true;
			}
			//Motorola
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("phone.cid");
			}
			//Samsung
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("com.samsung.cellid");
			}
			//Siemens
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("com.siemens.cellid");
			}
			//BlackBerry
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("cid");
			}
			//Others
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("Cell-ID");
			}
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("CellID");
			}
			if (out == null || out.equals("null") || out.equals("")){
				System.getProperty("phone.cid");
			}
		} catch (Exception e) {
			if(out == null){
				return -1;
			}
			if(hex){
				return Integer.parseInt(out, 16);
			} else {
				return Integer.parseInt(out);
			}
		}

		if(out == null){
			return -1;
		}
		if(hex){
			return Integer.parseInt(out, 16);
		} else {
			return Integer.parseInt(out);
		}
	}

	/**
	 * get the lac sring from phone
	 */
	public static int getLAC() {
		String out = null;
		boolean hex = false;
		try {
			out = System.getProperty("phone.lac");
			//Nokia
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("com.nokia.mid.lac");
			}
			//Sony-Ericsson
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("com.sonyericsson.net.lac");
				hex = true;
			}
			//Motorola
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("LocAreaCode");
			}
			//Samsung
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("com.samsung.cellid");
			}
			//Siemens
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("com.siemens.cellid");
			}
			//BlackBerry
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("cid");
			}
		} catch (Exception e) {
			if(out == null){
				return -1;
			}
			if(hex){
				return Integer.parseInt(out, 16);
			} else {
				return Integer.parseInt(out);
			}
		}

		if(out == null){
			return -1;
		}
		if(hex){
			return Integer.parseInt(out, 16);
		} else {
			return Integer.parseInt(out);
		}
	}

	/**
	 * Example IMSI (O2 UK): 234103530089555
	 * String mcc = imsi.substring(0,3); // 234 (UK)
	 * String mnc = imsi.substring(3,5); // 10 (O2)
	 *
	 * @return
	 */
	public static String getIMSI() {
		String out = null;
		try {
			out = System.getProperty("IMSI");
			if (out == null || out.equals("null") || out.equals(""))   {
				out = System.getProperty("phone.imsi");
			}
			//Nokia
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("com.nokia.mid.mobinfo.IMSI");
			}
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("com.nokia.mid.imsi");
			}
			//Sony-Ericsson
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("com.sonyericsson.imsi");
			}
			//Motorola
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("IMSI");
			}
			//Samsung
			if (out == null || out.equals("null") || out.equals(""))  {
				out = System.getProperty("com.samsung.imei");
			}
			//Siemens
			if (out == null || out.equals("null") || out.equals("")) {
				out = System.getProperty("com.siemens.imei");
			}
			//BlackBerry
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("imsi");
			}
		} catch (Exception e) {
			return out == null ? "" : out;
		}

		return out == null ? "" : out;
	}

	/**
	 * For moto, Example IMSI (O2 UK): 234103530089555
	 * String mcc = imsi.substring(0,3); // 234 (UK)
	 *
	 * @return
	 */
	public static int getMCC() {
		String out = null;
		try {
			if (out == null || out.equals("null") || out.equals("")){				
				out = System.getProperty("phone.mcc");
			}
			//Nokia
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("com.nokia.mid.mobinfo.IMSI");				
			}
			//Sony-Ericsson
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("com.sonyericsson.net.mcc");
			}
			//Motorola
			if (out == null || out.equals("null") || out.equals("")) {
				out = getIMSI().equals("") ? "" : getIMSI().substring(0, 3);
			}
			//Samsung
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("com.samsung.imei");
			}
			//Siemens
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("com.siemens.imei");
			}
			//BlackBerry
			if (out == null || out.equals("null") || out.equals("")){//getMNC()
				out = System.getProperty("mcc");
			}
		} catch (Exception e) {
			if(out == null){
				return -1;
			}
			return Integer.parseInt(out);
		}

		if(out == null){
			return -1;
		}
		return Integer.parseInt(out);
	}

	/**
	 * For moto, Example IMSI (O2 UK): 234103530089555
	 * String mnc = imsi.substring(3,5); // 10 (O2)
	 *
	 * @return
	 */
	public static int getMNC() {
		String out = null;
		try {
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("phone.mnc");
			}
			//Nokia
			if (out == null || out.equals("null") || out.equals("")) {
				out = getIMSI().equals("") ? "" : getIMSI().substring(3, 5);
			}
			//Sony-Ericsson
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("com.sonyericsson.net.mnc");
			}
			//Motorola
			if (out == null || out.equals("null") || out.equals("")) {
				out = getIMSI().equals("") ? "" : getIMSI().substring(3, 5);
			}
			//Samsung
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("com.samsung.imei");
			}
			//Siemens
			if (out == null || out.equals("null") || out.equals("")){
				out = System.getProperty("com.siemens.imei");
			}
			//BlackBerry
			if (out == null || out.equals("null") || out.equals("")) {//getMNC()
				out = System.getProperty("mnc");
			}
		} catch (Exception e) {
			if(out == null){
				return -1;
			}
			return Integer.parseInt(out);
		}

		if(out == null){
			return -1;
		}
		return Integer.parseInt(out);
	}

	/**
	 * not used now
	 * get the IMEI (International Mobile Equipment Identity (IMEI)) in the phone
	 *
	 * @return
	 */
	public static String getIMEI() {
		String out = "";
		try {
			out = System.getProperty("com.imei");
			//Nokia
			if(out== null ||out.equals("null")|| out.equals(""))
				out = System.getProperty("phone.imei");
			if(out== null ||out.equals("null")|| out.equals(""))
				out = System.getProperty("com.nokia.IMEI");
			if(out== null ||out.equals("null")|| out.equals(""))
				out = System.getProperty("com.nokia.mid.imei");
			if(out== null ||out.equals("null")|| out.equals(""))
				out = System.getProperty("com.nokia.mid.imei");
			//Sony-Ericsson
			if(out== null ||out.equals("null")|| out.equals(""))
				out = System.getProperty("com.sonyericsson.imei");
			//Motorola
			if(out== null ||out.equals("null")|| out.equals(""))
				out = System.getProperty("IMEI");
			if(out== null ||out.equals("null")|| out.equals(""))
				out = System.getProperty("com.motorola.IMEI");
			//Samsung
			if(out== null ||out.equals("null")|| out.equals(""))
				out = System.getProperty("com.samsung.imei");
			//Siemens
			if(out== null ||out.equals("null")|| out.equals(""))
				out = System.getProperty("com.siemens.imei");
			//#else
			if(out== null ||out.equals("null")|| out.equals(""))
				out = System.getProperty("imei");
			//#endif
		} catch (Exception e) {
			return out == null ? "" : out;
		}

		return out == null ? "" : out;
	}
}
