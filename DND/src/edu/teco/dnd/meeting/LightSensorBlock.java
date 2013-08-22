package edu.teco.dnd.meeting;

import java.net.MalformedURLException;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Option;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.graphiti.BlockType;
import edu.teco.dnd.uPart.SensorException;
import edu.teco.dnd.uPart.UPartReader;

/**
 * This class represents a {@link FunctionBlock} that can read a light sensor.
 * 
 */
@BlockType("Meeting")
public class LightSensorBlock extends FunctionBlock {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8168482988717037076L;

	/**
	 * Time between updates.
	 */
	public static final long UPDATE_TIME = 1000L;

	/**
	 * Indicates brightness.
	 */
	private Output<Integer> lights;

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
	 * Initializes LightSensorBlock.
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
	 * Shutdown Block.
	 */
	@Override
	public void shutdown() {
	}

	/**
	 * measures brightness.
	 */
	@Override
	public void update() {
		if (reader == null) {
			return;
		}
		Integer light = null;
		try {
			light = reader.getLight();
		} catch (SensorException e) {
			return;
		}
		lights.setValue(light);
	}
}
