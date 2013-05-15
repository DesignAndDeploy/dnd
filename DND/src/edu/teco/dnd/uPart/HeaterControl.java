package edu.teco.dnd.uPart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

/**
 * This Class is used for controling the heater.
 */
public class HeaterControl {

	/**
	 * URL of the heater.
	 */
	private URL url;

	/**
	 * temperature to send to the heater.
	 */
	private Temperature temp;

	/**
	 * Initializes a new HeaterControl.
	 * 
	 * @param heaterURL
	 *            URL of the heater to control
	 * @throws MalformedURLException
	 *             if url is malformed
	 */
	public HeaterControl(final String heaterURL) throws MalformedURLException {
		this.url = new URL(heaterURL);
		this.temp = new Temperature(0);
	}

	/**
	 * Sends temperature to heater.
	 * 
	 * @param temperature
	 *            Value of temperature sent to heater
	 * @throws SensorException
	 *             if JSON failed
	 */
	public void updateHeater(final Integer temperature) throws SensorException {
		temp.setTemp(temperature);
		OutputStream out = null;
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("PUT");
			out = connection.getOutputStream();
		} catch (IOException e) {
			throw new SensorException("Opening Stream failed", e);
		}
		OutputStreamWriter writer = new OutputStreamWriter(out);
		Gson gson = new Gson();
		try {
			gson.toJson(temp, writer);
			writer.close();
		} catch (JsonIOException e) {
			throw new SensorException("Writing JSON fail", e);
		} catch (IOException e) {
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while (reader.readLine() != null) {
			}
		} catch (IOException e) {
			throw new SensorException("Reading from Heater failed", e);
		}

	}

}
