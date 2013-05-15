package edu.teco.dnd.uPart.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import edu.teco.dnd.uPart.OutletReader;
import edu.teco.dnd.uPart.SensorException;

import org.junit.Before;
import org.junit.Test;

/**
 * This class is for testing the OutletReader.
 */
public class OutletReaderTest {

	/**
	 * URL used for testing.
	 */
	public static final String TEST_URL = "http://cumulus.teco.edu:51525/sensor/entity/000D6F00007294BB";

	/**
	 * OutletReader to read from outlet.
	 */
	private OutletReader reader;

	/**
	 * Energy value that should be measured.
	 */
	public static final Integer ENERGY = 5;

	/**
	 * initializes the ipAddress - now only a random String until actually accessing a real IP Address is
	 * built in.
	 * 
	 * @throws MalformedURLException
	 *             if url was malformed
	 */
	@Before
	public void init() throws MalformedURLException {
		reader = new OutletReader(TEST_URL);
	}

	/**
	 * Sets the energy that shall be read.
	 * 
	 * @param url
	 *            URL of the outlet
	 * @param energy
	 *            energy value to set
	 * @return true if everything worked
	 */
	private boolean setEnergy(final String url, final int energy) {
		URL u;
		try {
			u = new URL(url + "/set.py?energy=" + URLEncoder.encode(Integer.toString(energy), "UTF-8"));
		} catch (
				MalformedURLException | UnsupportedEncodingException e) {
			return false;
		}
		URLConnection connection = null;
		try {
			connection = u.openConnection();
		} catch (IOException e) {
			return false;
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			if (!"OK".equals(reader.readLine())) {
				return false;
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Tests whether OutletReader reads energy correctly.
	 * 
	 * @throws SensorException
	 *             if sensor stuff doesn't work properly
	 */
	@Test(timeout = 2000)
	public void testTemperatureLow() throws SensorException {
		assumeTrue(setEnergy(TEST_URL, ENERGY));
		assertEquals((Integer) ENERGY, reader.getEnergy());
	}

}
