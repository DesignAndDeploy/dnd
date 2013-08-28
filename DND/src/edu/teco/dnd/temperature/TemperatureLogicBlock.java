package edu.teco.dnd.temperature;

import java.util.Map;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.graphiti.BlockType;

/**
 * This class is an operating {@link FunctionBlock} that uses a temperature it gets to control the heating.
 * 
 */
@BlockType("Temperature")
public class TemperatureLogicBlock extends FunctionBlock {
	private static final long serialVersionUID = -2352500913660771287L;

	/**
	 * Type of this block.
	 */
	public static final String BLOCK_TYPE = "operator";

	/**
	 * Temperature sensed by a TemperatureSensor.
	 */
	private Input<Integer> roomTemperature;

	/**
	 * Value to compare the temperature to. If temperature is lower than threshold, activate heating. If it's bigger,
	 * deactivate heating
	 */
	public static final String OPTION_THRESHOLD = null;

	private int threshold;

	/**
	 * Temperature the user wants the heater to have. - set by user
	 */
	public static final String OPTION_TEMPERATURE = null;

	private int temperature;

	/**
	 * The temperature the heater shall achieve. - sent to heater
	 */
	private Output<Integer> heaterTemperature;

	/**
	 * Initializes TemperatureLogicBlock.
	 */
	@Override
	public void init(final Map<String, String> options) {
		try {
			threshold = Integer.parseInt(options.get("THRESHOLD"));
		} catch (NumberFormatException e) {
			threshold = Integer.MIN_VALUE;
		}
		try {
			temperature = Integer.parseInt(options.get("TEMPERATURE"));
		} catch (NumberFormatException e) {
			temperature = Integer.MIN_VALUE;
		}
	}

	/**
	 * Shutdown Block.
	 */
	@Override
	public void shutdown() {
	}

	/**
	 * Compares temperature with a preset threshold to calculate the setting of the heating.
	 */
	@Override
	public void update() {
		if (roomTemperature == null) {
			return;
		}
		Integer roomTemp = null;
		while (roomTemperature.hasMoreValues()) {
			Integer t = roomTemperature.popValue();
			if (t != null) {
				roomTemp = t;
			}
		}
		if (roomTemp != null) {
			if (roomTemp > threshold + 1) {
				heaterTemperature.setValue(0);
			} else if (roomTemp < threshold) {
				heaterTemperature.setValue(temperature);
			}
		}
	}

}
