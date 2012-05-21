package ru.mobilespbtransport.network;


import ru.mobilespbtransport.view.ScreenStack;

import javax.microedition.io.Connector;
import javax.microedition.io.ContentConnection;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Image;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: �����
 * Date: 05.05.12
 * Time: 13:01
 * <p/>
 * Thanks to http://stackoverflow.com/questions/6745256/j2me-nokia-httpconnection
 */
public class HttpClient {
	public static int BUFFER_LENGTH = 1024;

	public static String sendGET(String request) {
		HttpConnection httpConn = null;

		InputStream in = null;
		OutputStream os = null;

		try {
			// Open an HTTP Connection object
			httpConn = (HttpConnection) Connector.open(request);

			// Setup HTTP Request
			httpConn.setRequestMethod(HttpConnection.GET);
			httpConn.setRequestProperty("User-Agent", "Profile/MIDP-2.0 Confirguration/CLDC-1.1");
			httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			httpConn.setRequestProperty("Connection", "keep-alive");
			httpConn.setRequestProperty("Accept-Language", "ru");
			httpConn.setRequestProperty("Accept-Charset", "utf-8");

			/** Initiate connection and check for the response code. If the
			 response code is HTTP_OK then get the content from the target
			 **/
			int respCode = httpConn.getResponseCode();
			if (respCode == httpConn.HTTP_OK) {
				byte[] buff = new byte[BUFFER_LENGTH];
				in = httpConn.openInputStream();
				if (in == null) {
					throw new IOException("InputStream is null, please check Access Point configuration");
				}
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int n = in.read(buff);
				while (n > -1) {
					baos.write(buff, 0, n);
					n = in.read(buff);
				}
				baos.flush();
				baos.close();
				String response = new String(baos.toByteArray(), "UTF-8");
				return response;
			} else {
				//ignoring
				return null;
			}
		} catch (IOException e) {
			ScreenStack.showAlert("Не удалось подключиться к Интернету");
			e.printStackTrace();
			return null;
		} catch (SecurityException e){
			ScreenStack.showAlert("Для работы нужен Интернет. Пожалуйста, дайте разрешите доступ к сети");
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (in != null)
					in.close();
				if (os != null)
					os.close();
				if (httpConn != null)
					httpConn.close();
			} catch (IOException e) {
				//ignoring
			}
		}
	}


	public static String sendPost(String url, String message) {
		HttpConnection httpConn = null;
		InputStream in = null;
		OutputStream os = null;

		try {
			// Open an HTTP Connection object
			httpConn = (HttpConnection) Connector.open(url);

			// Setup HTTP Request to POST
			httpConn.setRequestMethod(HttpConnection.POST);

			httpConn.setRequestProperty("User-Agent", "Profile/MIDP-2.0 Confirguration/CLDC-1.1");
			httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			httpConn.setRequestProperty("Connection", "keep-alive");
			httpConn.setRequestProperty("Accept-Language", "ru");
			httpConn.setRequestProperty("Accept-Charset", "utf-8");

			os = httpConn.openOutputStream();
			os.write(message.getBytes());

			/*
			Caution: os.flush() is controversial. It may create unexpected behavior
			on certain mobile devices. Try it out for your mobile device
			*/
			//os.flush();

			byte[] buff = new byte[BUFFER_LENGTH];
			in = httpConn.openInputStream();
			if (in == null) {
				throw new IOException("InputStream is null, please check Access Point configuration");
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int n = in.read(buff);
			while (n > -1) {
				baos.write(buff, 0, n);
				n = in.read(buff);
			}
			baos.flush();
			baos.close();
			String response = new String(baos.toByteArray(), "UTF-8");
			return response;
		} catch (UnsupportedEncodingException e) {
			//ignoring
			e.printStackTrace();
		} catch (IOException e) {
			ScreenStack.showAlert("Не удалось подключиться к Интернету");
			e.printStackTrace();
		} catch (SecurityException e) {
			ScreenStack.showAlert("Для работы нужен Интернет. Пожалуйста, дайте разрешите доступ к сети");
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
				if (os != null)
					os.close();
				if (httpConn != null)
					httpConn.close();
			} catch (IOException e) {
				//ignoring
			}
		}
		return null;
	}

	public static Image loadImage(String url) throws IOException {
		byte[] imageData = loadImageBytes(url);
		Image im = null;
		im = Image.createImage(imageData, 0, imageData.length);
		return (im == null ? null : im);
	}

	/*--------------------------------------------------
		  * Open connection and download png into a byte array.
		  *-------------------------------------------------*/
	public static byte[] loadImageBytes(String url) {
		ContentConnection connection = null;
		DataInputStream iStrm = null;
		ByteArrayOutputStream bStrm = null;
		try {
			connection = (ContentConnection) Connector.open(url);

			// * There is a bug in MIDP 1.0.3 in which read() sometimes returns
			//   an invalid length. To work around this, I have changed the
			//   stream to DataInputStream and called readFully() instead of read()
			iStrm = connection.openDataInputStream();

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
		} catch (IOException e) {
			ScreenStack.showAlert("Не удалось подключиться к Интернету");
			return null;
		} catch (SecurityException e) {
			ScreenStack.showAlert("Для работы нужен Интернет. Пожалуйста, дайте разрешите доступ к сети");
			return null;
		} finally {
			try {
				// Clean up
				if (iStrm != null)
					iStrm.close();
				if (connection != null)
					connection.close();
				if (bStrm != null)
					bStrm.close();
			} catch (IOException e) {
				//ignoring
			}
		}
	}
}
