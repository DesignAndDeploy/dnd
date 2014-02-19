package edu.teco.dnd.temperature;

import java.net.MalformedURLException;
import java.util.Map;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.uPart.HeaterControl;
import edu.teco.dnd.uPart.SensorException;

/**
 * This acting {@link FunctionBlock} is used to turn the heating on / off.
 * 
 */
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
	public static final String OPTION_URL = "http://cumulus.teco.edu:51525/actuator/entity/";

	/**
	 * ID of the heater.
	 */
	public static final String OPTION_HEATER_ID = null;

	/**
	 * Used to control the heater.
	 */
	private HeaterControl control;

	/**
	 * w Initializes TemperatureActorBlock.
	 */
	@Override
	public void init(final Map<String, String> options) {
		final String url = options.get("URL");
		final String heaterID = options.get("HEATER_ID");
		if (url == null || heaterID == null) {
			return;
		}
		String fullURL = url + heaterID;
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
