package ru.mobilespbtransport.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 05.05.12
 * Time: 15:30
 * To change this template use File | Settings | File Templates.
 */
public class URLEncoder {
	private static final String URL_UNRESERVED =
			"ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
					"abcdefghijklmnopqrstuvwxyz" +
					"0123456789-_.~";
	private static final char[] HEX = "0123456789ABCDEF".toCharArray();

	public static String encode(String str) {
		StringBuffer buf = new StringBuffer();
		byte[] bytes = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeUTF(str);
			bytes = bos.toByteArray();
		} catch (IOException e) {
			// ignore
		}
		for (int i = 2; i < bytes.length; i++) {
			byte b = bytes[i];
			if (URL_UNRESERVED.indexOf(b) >= 0) {
				buf.append((char) b);
			} else {
				buf.append('%').append(HEX[(b >> 4) & 0x0f]).append(HEX[b & 0x0f]);
			}
		}
		return buf.toString();
	}
}
