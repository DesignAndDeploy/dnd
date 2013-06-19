package edu.teco.dnd.temperature;

import java.net.MalformedURLException;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Option;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.blocks.Timed;
import edu.teco.dnd.graphiti.BlockType;
import edu.teco.dnd.uPart.SensorException;
import edu.teco.dnd.uPart.UPartReader;

/**
 * This class represents a {@link FunctionBlock} that can read a temperature sensor.
 */
@BlockType("Temperature")
@Timed(TemperatureSensorBlock.UPDATE_TIME)
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
	@Option
	private String url = "http://cumulus.teco.edu:51525/sensor/entity/";

	/**
	 * ID of the UPart.
	 */
	@Option
	private String uPartID;

	/**
	 * Reads from the UPart.
	 */
	private UPartReader reader;

	/**
	 * Creates new TemperatureSensorBlock.
	 * 
	 * @param blockID
	 *            ID of new TemperatureSensorBlock
	 */
	public TemperatureSensorBlock(final String blockID) {
		super(blockID);
	}

	/**
	 * Returns type of this FunctionBlock.
	 * 
	 * @return type of this FunctionBlock
	 */
	@Override
	public String getType() {
		return "sensorTemperature";
	}

	/**
	 * Initializes TemperatureSensorBlock.
	 */
	@Override
	public void init() {
		if (url == null || uPartID == null) {
			return;
		}
		url = url.concat(uPartID);
		try {
			reader = new UPartReader(url);
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
	
	@Override
	public String toString() {
		return "TemperatureSensorBlock[id='" + getID() + "']";
	}
}
