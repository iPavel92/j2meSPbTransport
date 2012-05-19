package ru.mobilespbtransport.network;

import ru.mobilespbtransport.cache.Cache;
import ru.mobilespbtransport.model.Place;

import javax.microedition.io.Connector;
import javax.microedition.io.ContentConnection;
import javax.microedition.lcdui.Image;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Павел
 * Date: 04.05.12
 * Time: 20:42
 * Code from here:
 * http://www.java2s.com/Code/Java/J2ME/Downloadandviewapngfile.htm
 * Thanks!
 */
public class ImageLoader {

	//load image from cache or inet
	//save downloaded image to cache
	public static Image getMapImage(Place place, String url) throws IOException {
		if (Cache.isImageExists(place)) {
			System.out.println("found in cache");
			return Cache.loadImage(place);
		} else {
			System.out.println("not found in cache");
			byte[] imageData = getImageBytesFromInet(url);
			System.out.println("image loaded");
			Cache.saveImage(place, imageData);
			System.out.println("image saved");
			Image im = null;
			im = Image.createImage(imageData, 0, imageData.length);
			return (im == null ? null : im);
		}
	}

	public static Image getImageFromInet(String url) throws IOException {
		byte[] imageData = getImageBytesFromInet(url);
		Image im = null;
		im = Image.createImage(imageData, 0, imageData.length);
		return (im == null ? null : im);
	}

	/*--------------------------------------------------
		  * Open connection and download png into a byte array.
		  *-------------------------------------------------*/
	private static byte[] getImageBytesFromInet(String url) throws IOException {
		ContentConnection connection = (ContentConnection) Connector.open(url);

		// * There is a bug in MIDP 1.0.3 in which read() sometimes returns
		//   an invalid length. To work around this, I have changed the
		//   stream to DataInputStream and called readFully() instead of read()
		DataInputStream iStrm = connection.openDataInputStream();

		ByteArrayOutputStream bStrm = null;

		try {
			// ContentConnection includes a length method
			byte imageData[];
			int length = (int) connection.getLength();
			if (length != -1) {
				imageData = new byte[length];

				// Read the png into an array
				iStrm.readFully(imageData);
			} else  // Length not available...
			{
				bStrm = new ByteArrayOutputStream();

				int ch;
				while ((ch = iStrm.read()) != -1)
					bStrm.write(ch);

				imageData = bStrm.toByteArray();
				bStrm.close();
			}
			return imageData;
		} finally {
			// Clean up
			if (iStrm != null)
				iStrm.close();
			if (connection != null)
				connection.close();
			if (bStrm != null)
				bStrm.close();
		}
	}
}
