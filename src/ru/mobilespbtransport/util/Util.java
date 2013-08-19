package ru.mobilespbtransport.util;

import ru.mobilespbtransport.CellIdTest;

/**
 * Created with IntelliJ IDEA.
 * User: Павел
 * Date: 17.08.13
 * Time: 22:21
 * To change this template use File | Settings | File Templates.
 */
public class Util {
	public static String fillLeft(String src, char c, int length) {
		if (src == null) {
			return null;
		}
		if (src.length() >= length) {
			return src;
		}
		StringBuffer sb = new StringBuffer(length);
		for (int i = 0; i < length - src.length(); ++i) {
			sb.append(c);
		}
		sb.append(src);
		return sb.toString();
	}

	//000E00000000000000000000000000001B000000000000000000000003000000000811000000dd000000fa00000001FFFFFFFF00000000
	//actually
	//" ♫              ←           ♥  ◄   ?   ?   ☺????    "

	//must be
	//000E00000000000000000000000000001B000000000000000000000003000000000811000000dd000000fa00000001FFFFFFFF00000000
	//" ♫              ←           ♥  ◄   ▌   ·   ☺        "

	//%00%0E%00%00%00%00%00%00%00%00%00%00%00%00%00%00%1B%00%00%00%00%00%00%00%00%00%00%00%03%00%00%00%00%08%11%00%00%00%dd%00%00%00%fa%00%00%00%01%FF%FF%FF%FF%00%00%00%00


	public static String convertHexStringToAsciiString(String hex) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < hex.length(); i += 2) {
			String str = hex.substring(i, i + 2);
			sb.append((char) Integer.parseInt(str, 16));
		}
		return sb.toString();
	}
}
