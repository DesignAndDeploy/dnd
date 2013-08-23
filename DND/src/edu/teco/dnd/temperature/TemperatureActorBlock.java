package edu.teco.dnd.temperature;

import java.net.MalformedURLException;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.Option;
import edu.teco.dnd.graphiti.BlockType;
import edu.teco.dnd.uPart.HeaterControl;
import edu.teco.dnd.uPart.SensorException;

/**
 * This acting {@link FunctionBlock} is used to turn the heating on / off.
 * 
 */
@BlockType("Temperature")
public class TemperatureActorBlock extends FunctionBlock {
	private static final long serialVersionUID = -9221185917263406514L;

	/**
	 * Type of this block.
	 */
	public static final String BLOCK_TYPE = "actorTemperature";

	/**
	 * Temperature the heater shall achieve.
	 */
	private Input<Integer> temperature;

	/**
	 * URL of the heater. Default is already set; doesn't contain ID.
	 */
	private Option url;
	// TODO: set default "http://cumulus.teco.edu:51525/actuator/entity/"

	/**
	 * ID of the heater.
	 */
	private Option heaterID;

	/**
	 * Used to control the heater.
	 */
	private HeaterControl control;

	/**
	 * Initializes TemperatureActorBlock.
	 */
	@Override
	public void init() {
		if (url == null || heaterID == null) {
			return;
		}
		String fullURL = url.getValue() + heaterID.getValue();
		try {
			control = new HeaterControl(fullURL);
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
	 * This method is used to turn the heating off or on, depending on the input.
	 */
	@Override
	public void update() {
		if (control == null || temperature == null) {
			return;
		}
		Integer newTemperature = null;
		while (temperature.hasMoreValues()) {
			Integer t = temperature.popValue();
			if (t != null) {
				newTemperature = t;
			}
		}
		if (newTemperature != null) {
			try {
				control.updateHeater(newTemperature);
			} catch (SensorException e) {
			}
		}
	}

}
