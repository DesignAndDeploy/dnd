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

	/**
	 * 
	 */
	private static final long serialVersionUID = -9221185917263406514L;

	/**
	 * Temperature the heater shall achieve.
	 */
	@Input
	private Integer temperature;

	/**
	 * URL of the heater. Default is already set; doesn't contain ID.
	 */
	@Option
	private String url = "http://cumulus.teco.edu:51525/actuator/entity/";

	/**
	 * ID of the heater. Default: Living_Lab
	 */
	@Option
	private String heaterID = "Heater_Living";

	/**
	 * Used to control the heater.
	 */
	private HeaterControl control;

	/**
	 * Creates new TemperatureActorBlock.
	 * 
	 * @param blockID
	 *            ID of newTemperatureActorBlock
	 */
	public TemperatureActorBlock(final String blockID) {
		super(blockID);
	}

	/**
	 * Returns Type of this FunctionBlock.
	 * 
	 * @return type of this FunctionBlock
	 */
	@Override
	public String getType() {
		return "actorTemperature";
	}

	/**
	 * Initializes TemperatureActorBlock.
	 */
	@Override
	public void init() {
		if (url == null || heaterID == null) {
			return;
		}
		url = url.concat(heaterID).concat("/function/set");
		try {
			control = new HeaterControl(url);
		} catch (MalformedURLException e) {
		}
	}

	/**
	 * This method is used to turn the heating off or on, depending on the input.
	 */
	@Override
	protected void update() {
		if (control == null || temperature == null) {
			return;
		}
		try {
			control.updateHeater(temperature);
		} catch (SensorException e) {
		}
	}

}
