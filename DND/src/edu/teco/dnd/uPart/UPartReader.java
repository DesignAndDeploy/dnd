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
 * This class is used for reading from the UParts.
 */
public class UPartReader {

	/**
	 * URL of the UPart to read.
	 */
	private URL url;

	/**
	 * Initializes a new UPartReader.
	 * 
	 * @param urlString
	 *            URL of the UPart to read as a String
	 * @throws MalformedURLException
	 *             if url is malformed
	 */
	public UPartReader(final String urlString) throws MalformedURLException {
		this.url = new URL(urlString);
	}

	/**
	 * Returns the temperature measured by the UPart.
	 * 
	 * @return temperature measured by the UPart if url is malformed
	 * @throws SensorException
	 *             if JSON reading didn't work properly
	 */
	public Integer getTemperature() throws SensorException {
		UPart upart = getUPart();
		return upart.getTemperature();
	}

	/**
	 * Returns the brightness measured by the UPart.
	 * 
	 * @return brightness of muPart
	 * @throws SensorException
	 *             if JSOn reading didn't work properly
	 */
	public Integer getLight() throws SensorException {
		UPart upart = getUPart();
		return upart.getLight();
	}

	/**
	 * Converts a JSON String to a UPart.
	 * 
	 * @return UPart belonging to the url
	 * @throws SensorException
	 *             if JSON reading didn't work properly
	 */
	private UPart getUPart() throws SensorException {
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		try {
			return gson.fromJson(new InputStreamReader(url.openStream()), UPart.class);
		} catch (JsonSyntaxException e) {
			throw new SensorException("failed to parse JSON data/open URL.", e);
		} catch (JsonIOException e) {
			throw new SensorException("failed to parse JSON data/open URL.", e);
		} catch (IOException e) {
			throw new SensorException("failed to parse JSON data/open URL.", e);
		}
	}

}
