package edu.teco.dnd.uPart;

/**
 * This class represents a measuring outlet.
 */
public class Outlet {

	/**
	 * contains the measured data.
	 */
	private OutletData data;

	/**
	 * tags of the UPart.
	 */
	@SuppressWarnings("unused")
	private String[] tags;

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
	 * returns Energy measured at Outlet.
	 * 
	 * @return Energy measured at Outlet
	 */
	protected Integer getEnergy() {
		return data.getEnergy();
	}
}
