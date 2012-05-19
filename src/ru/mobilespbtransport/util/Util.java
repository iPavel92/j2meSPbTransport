package ru.mobilespbtransport.util;

import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 19.05.12
 * Time: 15:40
 * To change this template use File | Settings | File Templates.
 */
public class Util {

	//Используется для конвертации строк в UI, чтобы не писать /u100500...
	public static String convertToUtf8(String src){
		try {
			return new String(src.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			//TODO
			e.printStackTrace();
			return "";
		}
	}
}
