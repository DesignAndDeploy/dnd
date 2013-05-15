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
	@Input
	private Integer roomTemperature;

	/**
	 * Value to compare the temperature to. If temperature is lower than threshold, activate heating. If it's
	 * bigger, deactivate heating
	 */
	@Option
	private Integer threshold;

	/**
	 * Temperature the user wants the heater to have. - set by user
	 */
	@Option
	private Integer temperature;

	/**
	 * The temperature the heater shall achieve. - sent to heater
	 */
	private Output<Integer> heaterTemperature;

	/**
	 * Creates new TemperatureLogicBlock.
	 * 
	 * @param blockID
	 *            ID of new TemperatureLogicBlock
	 */
	public TemperatureLogicBlock(final String blockID) {
		super(blockID);
	}

	/**
	 * Returns type of this FunctionBlock.
	 * 
	 * @return type of this FunctionBlock
	 */
	@Override
	public String getType() {
		return "operator";
	}

	/**
	 * Initializes TemperatureLogicBlock.
	 */
	@Override
	public void init() {

	}

	/**
	 * Compares temperature with a preset threshold to calculate the setting of the heating.
	 */
	@Override
	protected void update() {
		if (roomTemperature == null || threshold == null || temperature == null) {
			return;
		}
		if (roomTemperature > threshold + 1) {
			heaterTemperature.setValue(0);
		} else if (roomTemperature < threshold) {
			heaterTemperature.setValue(temperature);
		}
	}

}
