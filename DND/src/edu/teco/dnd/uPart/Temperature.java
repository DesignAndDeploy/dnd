package edu.teco.dnd.uPart;

/**
 * This Class is later on used to create a JSON String to control the Heating. It only contains a temperature.
 */
public class Temperature {

	/**
	 * the temperature the heater shall achieve.
	 */
	@SuppressWarnings("unused")
	private String temp;

	/**
	 * Initializes a new Temperature.
	 * 
	 * @param temp
	 *            value of temperature
	 */
	public Temperature(final Integer temp) {
		this.temp = temp.toString();
	}

	/**
	 * sets the temperature.
	 * 
	 * @param temp
	 *            value of temperature
	 */
	public void setTemp(final Integer temp) {
		this.temp = temp.toString();
	}

}
