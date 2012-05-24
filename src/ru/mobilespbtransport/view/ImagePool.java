package ru.mobilespbtransport.view;

import javax.microedition.lcdui.Image;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 24.05.12
 * Time: 14:52
 * To change this template use File | Settings | File Templates.
 */
public class ImagePool {
	private static Hashtable images = new Hashtable();  //Hashtable<String->Image>

	private static Image loadImageToPool(String path){
		try{
			Image image = Image.createImage(path);
			images.put(path, image);
			return image;
		} catch (IOException e) {
			//ignoring
			e.printStackTrace();
			return null;
		}
	}
	
	public static Image getImage(String path){
		if(images.containsKey(path)){
			return (Image)images.get(path); 
		} else {
			return loadImageToPool(path);
		}
	}		
}
