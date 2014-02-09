package edu.teco.dnd.temperature;

import java.net.MalformedURLException;
import java.util.Map;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.uPart.SensorException;
import edu.teco.dnd.uPart.UPartReader;

/**
 * This class represents a {@link FunctionBlock} that can read a temperature sensor.
 */
public class TemperatureSensorBlock extends FunctionBlock {
	private static final long serialVersionUID = -1362359340515519805L;

	/**
	 * Type of this block.
	 */
	public static final String BLOCK_TYPE = "sensorTemperature";

	/**
	 * Time between updates.
	 */
	public static final Long BLOCK_UPDATE_INTERVAL = 5000L;

	/**
	 * current temperature.
	 */
	private Output<Integer> temperature;

	/**
	 * URL of the UPart. Default is already set; doesn't contain ID.
	 */
	public static final String OPTION_URL = "http://cumulus.teco.edu:51525/sensor/entity/";

	/**
	 * ID of the UPart.
	 */
	public static final String OPTION_UPART_ID = null;

	/**
	 * Reads from the UPart.
	 */
	private UPartReader reader;

	/**
	 * Initializes TemperatureSensorBlock.
	 */
	@Override
	public void init(final Map<String, String> options) {
		final String url = options.get("URL");
		final String uPartID = options.get("UPART_ID");
		if (url == null || uPartID == null) {
			return;
		}
		String fullURL = url + uPartID;
		try {
			reader = new UPartReader(fullURL);
		} catch (MalformedURLException e) {
		}
	}

	/**
	 * Shutdown Block.
	 */
	@Override
	public void shutdown() {
	}

	/**
	 * Reads new temperature and sets it on output temperature.
	 */
	@Override
	public void update() {
		if (reader == null) {
			return;
		}
		try {
			temperature.setValue(reader.getTemperature());
		} catch (SensorException e) {
		}
	}
}
