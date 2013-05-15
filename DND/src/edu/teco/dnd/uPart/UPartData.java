package edu.teco.dnd.uPart;

/**
 * Contains Data the UPart can measure.
 */
public class UPartData {

	/**
	 * measured brightness.
	 */
	private Data light;

	/**
	 * measured idk what, not important for our use.
	 */
	private Data move;

	/**
	 * measured temperature.
	 */
	private Data temperature;

	/**
	 * converts all Data to a String @ return all Data converted to a String.
	 * 
	 * @return all Data converted to a String
	 */
	@Override
	public String toString() {
		return (light.toString() + move.toString() + temperature.toString());
	}

	/**
	 * returns measured temperature.
	 * 
	 * @return measured temperature
	 */
	protected Integer getTemperature() {
		return temperature.getValue();
	}

	/**
	 * returns the measured brightness.
	 * 
	 * @return measured brightness
	 */
	protected Integer getLight() {
		return light.getValue();
	}
}
