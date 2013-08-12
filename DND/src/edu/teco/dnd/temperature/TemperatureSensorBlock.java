package edu.teco.dnd.temperature;

import java.net.MalformedURLException;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Option;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.graphiti.BlockType;
import edu.teco.dnd.uPart.SensorException;
import edu.teco.dnd.uPart.UPartReader;

/**
 * This class represents a {@link FunctionBlock} that can read a temperature sensor.
 */
@BlockType("Temperature")
public class TemperatureSensorBlock extends FunctionBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1362359340515519805L;

	/**
	 * Time between updates.
	 */
	public static final long UPDATE_TIME = 1000L;

	/**
	 * current temperature.
	 */
	private Output<Integer> temperature;

	/**
	 * URL of the UPart. Default is already set; doesn't contain ID.
	 */
	private Option url;
	// TODO: set default "http://cumulus.teco.edu:51525/sensor/entity/"

	/**
	 * ID of the UPart.
	 */
	private Option uPartID;

	/**
	 * Reads from the UPart.
	 */
	private UPartReader reader;

	/**
	 * Initializes TemperatureSensorBlock.
	 */
	@Override
	public void init() {
		if (url == null || uPartID == null) {
			return;
		}
		String fullURL = url.getValue() + uPartID.getValue();
		try {
			reader = new UPartReader(fullURL);
		} catch (MalformedURLException e) {
		}
	}

	/**
	 * Reads new temperature and sets it on output temperature.
	 */
	@Override
	protected void update() {
		if (reader == null) {
			return;
		}
		try {
			temperature.setValue(reader.getTemperature());
		} catch (SensorException e) {
		}
	}
}
