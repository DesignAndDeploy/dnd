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

import edu.teco.dnd.uPart.SensorException;
import edu.teco.dnd.uPart.UPartReader;

import org.junit.Before;
import org.junit.Test;

/**
 * This class is for testing the UPartReader.
 */
public class UPartReaderTest {
	/**
	 * default time before timeout occurs.
	 */
	private static final int DEFAULT_TIMEOUT = 2000;
	/**
	 * URL used for testing.
	 */
	private static final String TEST_URL = "http://cumulus.teco.edu:51525/sensor/entity/1.2.3.4.0.6.1.148";

	/**
	 * A low value for a temperature.
	 */
	private static final int LOW_TEMPERATURE = 8;

	/**
	 * A high value for a temperature.
	 */
	private static final int HIGH_TEMPERATURE = 30;

	/**
	 * A low value for the light.
	 */
	private static final int LOW_LIGHT = 2;

	/**
	 * A high value for the light.
	 */
	private static final int HIGH_LIGHT = 300;

	/**
	 * UPartReader to read from UPart.
	 */
	private UPartReader reader;

	/**
	 * initializes the ipAddress.
	 * 
	 * @throws MalformedURLException
	 *             if url was malformed
	 */
	@Before
	public void init() throws MalformedURLException {
		reader = new UPartReader(TEST_URL);
	}

	/**
	 * Sets values for a uPart.
	 * 
	 * @param url
	 *            URL of the uPart
	 * @param temperature
	 *            Temperature value to set
	 * @param light
	 *            Light value to set
	 * @return true if everything worked
	 */
	private boolean setUPart(final String url, final int temperature, final int light) {
		URL u;
		try {
			u = new URL(url + "/set.py?temperature="
					+ URLEncoder.encode(Integer.toString(temperature), "UTF-8") + "&light="
					+ URLEncoder.encode(Integer.toString(light), "UTF-8"));
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
	 * Tests whether uPartReader reads temperature correctly.
	 * 
	 * @throws SensorException
	 *             if sensor stuff doesn't work properly
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void testTemperatureLow() throws SensorException {
		assumeTrue(setUPart(TEST_URL, LOW_TEMPERATURE, LOW_LIGHT));
		assertEquals((Integer) LOW_TEMPERATURE, reader.getTemperature());
	}

	/**
	 * Tests whether uPartReader reads high temperatures correctly.
	 * 
	 * @throws SensorException
	 *             if sensor stuff doesn't work properly
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void testTemperatureHigh() throws SensorException {
		assumeTrue(setUPart(TEST_URL, HIGH_TEMPERATURE, LOW_LIGHT));
		assertEquals((Integer) HIGH_TEMPERATURE, reader.getTemperature());
	}

	/**
	 * Tests that the sensor can be read twice correctly.
	 * 
	 * @throws SensorException
	 *             if sensor stuff doesn't work properly
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void testTemperatureTwice() throws SensorException {
		assumeTrue(false);
		assumeTrue(setUPart(TEST_URL, LOW_TEMPERATURE, LOW_LIGHT));
		assumeTrue(((Integer) LOW_TEMPERATURE).equals(reader.getTemperature()));
		assertEquals((Integer) LOW_TEMPERATURE, reader.getTemperature());
	}

	/**
	 * Tests that the sensor can be read correctly after the temperature has changed.
	 * 
	 * @throws SensorException
	 *             if sensor stuff doesn't work properly
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void testTemperatureChanged() throws SensorException {
		assumeTrue(setUPart(TEST_URL, LOW_TEMPERATURE, LOW_LIGHT));
		assumeTrue(((Integer) LOW_TEMPERATURE).equals(reader.getTemperature()));
		assumeTrue(setUPart(TEST_URL, HIGH_TEMPERATURE, LOW_LIGHT));
		assertEquals((Integer) HIGH_TEMPERATURE, reader.getTemperature());
	}

	/**
	 * Tests whether uPartReader reads lights correctly.
	 * 
	 * @throws Exception
	 *             if sensor stuff doesn't work properly
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void testLightLow() throws Exception {
		assumeTrue(setUPart(TEST_URL, LOW_TEMPERATURE, LOW_LIGHT));
		assertEquals((Integer) LOW_LIGHT, reader.getLight());
	}

	/**
	 * Tests whether uPartReader reads high light values correctly.
	 * 
	 * @throws SensorException
	 *             if sensor stuff doesn't work properly
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void testLightHigh() throws SensorException {
		assumeTrue(setUPart(TEST_URL, LOW_TEMPERATURE, HIGH_LIGHT));
		assertEquals((Integer) HIGH_LIGHT, reader.getLight());
	}

	/**
	 * Tests that the sensor can be read twice correctly.
	 * 
	 * @throws SensorException
	 *             if sensor stuff doesn't work properly
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void testLightTwice() throws SensorException {
		assumeTrue(false);
		assumeTrue(setUPart(TEST_URL, LOW_TEMPERATURE, LOW_LIGHT));
		assumeTrue(((Integer) LOW_LIGHT).equals(reader.getLight()));
		assertEquals((Integer) LOW_LIGHT, reader.getLight());
	}

	/**
	 * Tests that the sensor can be read correctly after the light value has changed.
	 * 
	 * @throws SensorException
	 *             if sensor stuff doesn't work properly
	 */
	@Test(timeout = DEFAULT_TIMEOUT)
	public void testLightChanged() throws SensorException {
		assumeTrue(setUPart(TEST_URL, LOW_TEMPERATURE, LOW_LIGHT));
		assumeTrue(((Integer) LOW_LIGHT).equals(reader.getLight()));
		assumeTrue(setUPart(TEST_URL, LOW_TEMPERATURE, HIGH_LIGHT));
		assertEquals((Integer) HIGH_LIGHT, reader.getLight());
	}
}
