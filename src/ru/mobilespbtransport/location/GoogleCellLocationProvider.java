package ru.mobilespbtransport.location;

import ru.mobilespbtransport.model.Coordinate;
import ru.mobilespbtransport.network.HttpClient;
import java.io.*;

/**
 * This location providers uses private and officially unsupported Google glm/mmap API *
 */
public class GoogleCellLocationProvider implements CellLocationProvider {
	public Coordinate getLocation(CellData cellData) {
		try {
			String url = "http://www.google.com/glm/mmap";
			byte[] request = generateRequestData(cellData);

			byte[] response = HttpClient.sendPost(url, request);
			if(response == null){
				return null;
			}

			return parseResponse(response);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Coordinate parseResponse(byte[] bytes) throws IOException {
		ByteArrayInputStream bais = null;
		DataInputStream dis = null;
		try {
			bais = new ByteArrayInputStream(bytes);
			dis = new DataInputStream(bais);

			//private and unpublic Google API. Some fields is magic.
			dis.readShort();
			dis.readByte();
			int code = dis.readInt();
			if (code == 0) {
				double lat = (double) dis.readInt() / 1000000D;
				double lon = (double) dis.readInt() / 1000000D;
				dis.readInt();
				dis.readInt();
				dis.readUTF();
				return new Coordinate(lat, lon);
			} else {
				return null;
			}
		} finally {
   			if(bais != null){
				bais.close();
			}
			if(dis != null){
				dis.close();
			}
		}
	}

	private static byte[] generateRequestData(CellData cellData) throws IOException	{
		ByteArrayOutputStream baos = null;
		DataOutputStream dos = null;

		try {
			baos = new ByteArrayOutputStream();
			dos = new DataOutputStream(baos);

			//private and unpublic Google API. Some fields is magic.
			dos.writeShort(21); //Function Code?
			dos.writeLong(0);   //Session ID?
			dos.writeUTF("en");  //Contry Code?
			dos.writeUTF("MIDP-2.0"); //Platform?
			dos.writeUTF("1.0");  //Version?
			dos.writeUTF("Web");
			dos.writeByte(27);  //Op Code?
			dos.writeInt(0);
			dos.writeInt(0);
			dos.writeInt(3);
			dos.writeUTF("");
			dos.writeInt(cellData.getCellId());
			dos.writeInt(cellData.getLac());
			dos.writeInt(cellData.getMcc());
			dos.writeInt(cellData.getMnc());
			dos.writeInt(0);
			dos.writeInt(0);
			dos.flush();
			baos.flush();

			return baos.toByteArray();
		} finally {
            if(baos != null){
				baos.close();
			}
			if(dos != null){
				dos.close();
			}
		}
	}
}
