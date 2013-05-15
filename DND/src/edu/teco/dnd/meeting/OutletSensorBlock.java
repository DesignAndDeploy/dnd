package edu.teco.dnd.meeting;

import java.net.MalformedURLException;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Option;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.blocks.Timed;
import edu.teco.dnd.graphiti.BlockType;
import edu.teco.dnd.uPart.OutletReader;
import edu.teco.dnd.uPart.SensorException;

/**
 * This {@link FunctionBlock} is used to detect the use of an outlet.
 */
@BlockType("Meeting")
@Timed(OutletSensorBlock.UPDATE_TIME)
public class OutletSensorBlock extends FunctionBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5476105010804335530L;

	/**
	 * Time between updates.
	 */
	public static final long UPDATE_TIME = 1000L;

	/**
	 * The measured energy at the outlet.
	 */
	private Output<Integer> outlet;

	/**
	 * URL of the Outlet. Default is already set; doesn't contain ID.
	 */
	@Option
	private String url = "http://cumulus.teco.edu:51525/sensor/entity/";

	/**
	 * ID of the outlet.
	 */
	@Option
	private String outletID;

	/**
	 * Reads from the outlet.
	 */
	private OutletReader reader;

	/**
	 * Creates new OutletSensorBlock.
	 * 
	 * @param blockID
	 *            ID of new OutletSensorBlock
	 */
	public OutletSensorBlock(final String blockID) {
		super(blockID);
	}

	/**
	 * Returns type of this FunctionBlock.
	 * 
	 * @return type of this FunctionBlock
	 */
	@Override
	public String getType() {
		return "sensorOutlet";
	}

	/**
	 * Initializes OutletSensorBlock.
	 */
	@Override
	public void init() {
		if (url == null || outletID == null) {
			return;
		}
		url = url.concat(outletID);
		try {
			reader = new OutletReader(url);
		} catch (MalformedURLException e) {
		}
	}

	/**
	 * Checks use of the outlet.
	 */
	@Override
	protected void update() {
		if (reader == null) {
			return;
		}
		try {
			outlet.setValue(reader.getEnergy());
		} catch (SensorException e) {
		}
	}

}
