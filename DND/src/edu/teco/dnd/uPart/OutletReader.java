package edu.teco.dnd.uPart;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * This class is for reading from outlets.
 */
public class OutletReader {

	/**
	 * URL of the Outlet.
	 */
	private URL url;

	/**
	 * Initializes a new OutletReader.
	 * 
	 * @param urlString
	 *            URL of the Outlet as a String
	 * @throws MalformedURLException
	 *             if url is malformed
	 */
	public OutletReader(final String urlString) throws MalformedURLException {
		this.url = new URL(urlString);
	}

	/**
	 * returns Energy measured at an outlet.
	 * 
	 * @return energy measured at outlet
	 * @throws SensorException
	 *             if reading from URL didn't work properly
	 */
	public Integer getEnergy() throws SensorException {
		Outlet outlet = getOutlet();
		return outlet.getEnergy();
	}

	/**
	 * Converts a JSON String to an Outlet.
	 * 
	 * @return Outlet derived from String
	 * 
	 * @throws SensorException
	 *             if JSON reading didn't work properly
	 */
	private Outlet getOutlet() throws SensorException {
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		try {
			return gson.fromJson(new InputStreamReader(url.openStream()), Outlet.class);
		} catch (
				JsonSyntaxException | JsonIOException | IOException e) {
			throw new SensorException("failed to parse JSON data", e);
		}
	}

}
