package edu.teco.dnd.uPart;

/**
 * This class represents a UPart.
 */
public class UPart {

	/**
	 * contains the measured data.
	 */
	private UPartData data;

	/**
	 * idk what.
	 */
	@SuppressWarnings("unused")
	private String updated;

	/**
	 * String describing the UPart.
	 */
	@SuppressWarnings("unused")
	private String description;

	/**
	 * registration number??.
	 */
	@SuppressWarnings("unused")
	private String registered;

	/**
	 * longitude of UPart.
	 */
	@SuppressWarnings("unused")
	private String longitude;

	/**
	 * idk what.
	 */
	@SuppressWarnings("unused")
	private int permission;

	/**
	 * latitude of UPart.
	 */
	@SuppressWarnings("unused")
	private String latitude;

	/**
	 * returns the temperature measured by the UPart.
	 * 
	 * @return temperature measured by UPart
	 */
	protected Integer getTemperature() {
		return data.getTemperature();
	}

	/**
	 * returns the brightness measured by the UPart.
	 * 
	 * @return brightness measured by UPart
	 */
	protected Integer getLight() {
		return data.getLight();
	}

}
