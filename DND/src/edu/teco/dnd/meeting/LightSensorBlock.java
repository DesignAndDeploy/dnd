package edu.teco.dnd.meeting;

import java.net.MalformedURLException;
import java.util.Map;

import edu.teco.dnd.blocks.FunctionBlock;
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
	 * Type of this block.
	 */
	public static final String BLOCK_TYPE = "sensorLight";

	/**
	 * Time between updates.
	 */
	public static final Long BLOCK_UPDATE_INTERVAL = 1000L;

	/**
	 * URL of the UPart. Default is already set; doesn't contain ID.
	 */
	public static final String OPTION_URL = "http://cumulus.teco.edu:51525/sensor/entity/";

	/**
	 * ID of the UPart.
	 */
	public static final String OPTION_UPART_ID = null;

	/**
	 * Indicates brightness.
	 */
	private Output<Integer> lights;

	/**
	 * Reads from the UPart.
	 */
	private UPartReader reader;

	/**
	 * Initializes LightSensorBlock.
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
