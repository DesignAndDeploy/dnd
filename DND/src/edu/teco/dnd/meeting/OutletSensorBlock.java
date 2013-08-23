package edu.teco.dnd.meeting;

import java.net.MalformedURLException;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Option;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.graphiti.BlockType;
import edu.teco.dnd.uPart.OutletReader;
import edu.teco.dnd.uPart.SensorException;

/**
 * This {@link FunctionBlock} is used to detect the use of an outlet.
 */
@BlockType("Meeting")
public class OutletSensorBlock extends FunctionBlock {
	private static final long serialVersionUID = -5476105010804335530L;

	/**
	 * Type of this block.
	 */
	public static final String BLOCK_TYPE = "sensorOutlet";

	/**
	 * Time between updates.
	 */
	public static final long BLOCK_UPDATE_INTERVAL = 1000L;

	/**
	 * The measured energy at the outlet.
	 */
	private Output<Integer> outlet;

	/**
	 * URL of the Outlet. Default is already set; doesn't contain ID.
	 */
	private Option url;
	// TODO: set default "http://cumulus.teco.edu:51525/sensor/entity/"

	/**
	 * ID of the outlet.
	 */
	private Option outletID;

	/**
	 * Reads from the outlet.
	 */
	private OutletReader reader;

	/**
	 * Initializes OutletSensorBlock.
	 */
	@Override
	public void init() {
		if (url == null || outletID == null) {
			return;
		}
		String fullURL = url.getValue() + outletID.getValue();
		try {
			reader = new OutletReader(fullURL);
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
	 * Checks use of the outlet.
	 */
	@Override
	public void update() {
		if (reader == null) {
			return;
		}
		try {
			outlet.setValue(reader.getEnergy());
		} catch (SensorException e) {
		}
	}

}
