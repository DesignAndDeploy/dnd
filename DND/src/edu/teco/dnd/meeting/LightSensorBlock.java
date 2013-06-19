package edu.teco.dnd.meeting;

import java.net.MalformedURLException;
import java.util.UUID;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Option;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.blocks.Timed;
import edu.teco.dnd.graphiti.BlockType;
import edu.teco.dnd.uPart.SensorException;
import edu.teco.dnd.uPart.UPartReader;

/**
 * This class represents a {@link FunctionBlock} that can read a light sensor.
 * 
 */
@BlockType("Meeting")
@Timed(LightSensorBlock.UPDATE_TIME)
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
	 * Creates new LightSensorBlock.
	 * 
	 * @param blockID
	 *            ID of new LightSensorBlock
	 */
	public LightSensorBlock(final UUID blockID) {
		super(blockID);
	}

	/**
	 * Returns type of this FunctionBlock.
	 * 
	 * @return type of this FunctionBlock
	 */
	@Override
	public String getType() {
		return "sensorLight";
	}

	/**
	 * Initializes LightSensorBlock.
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
	 * measures brightness.
	 */
	@Override
	protected void update() {
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
