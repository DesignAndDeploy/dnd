package edu.teco.dnd.temperature;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.Option;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.graphiti.BlockType;

/**
 * This class is an operating {@link FunctionBlock} that uses a temperature it gets to control the heating.
 * 
 */
@BlockType("Temperature")
public class TemperatureLogicBlock extends FunctionBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2352500913660771287L;

	/**
	 * Temperature sensed by a TemperatureSensor.
	 */
	private Input<Integer> roomTemperature;

	/**
	 * Value to compare the temperature to. If temperature is lower than threshold, activate heating. If it's bigger,
	 * deactivate heating
	 */
	private Option threshold;

	private int _threshold;

	/**
	 * Temperature the user wants the heater to have. - set by user
	 */
	private Option temperature;

	private int _temperature;

	/**
	 * The temperature the heater shall achieve. - sent to heater
	 */
	private Output<Integer> heaterTemperature;

	/**
	 * Initializes TemperatureLogicBlock.
	 */
	@Override
	public void init() {
		try {
			_threshold = Integer.parseInt(threshold.getValue());
		} catch (NumberFormatException e) {
			_threshold = Integer.MIN_VALUE;
		}
		try {
			_temperature = Integer.parseInt(temperature.getValue());
		} catch (NumberFormatException e) {
			_temperature = Integer.MIN_VALUE;
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
			if (roomTemp > _threshold + 1) {
				heaterTemperature.setValue(0);
			} else if (roomTemp < _threshold) {
				heaterTemperature.setValue(_temperature);
			}
		}
	}

}
