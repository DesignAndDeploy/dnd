package edu.teco.dnd.uPart;

/**
 * this class represents the measured data.
 */
public class Data {

	/**
	 * unit the value is measured in.
	 */
	private String unit;

	/**
	 * last time the UPart got updated.
	 */
	private String updated;

	/**
	 * measured value.
	 */
	private Integer value;

	/**
	 * converts measured data to a String.
	 * 
	 * @return measured data, converted to a String
	 */
	@Override
	public String toString() {
		return ("unit: " + unit + "updated: " + updated + "value: " + Double.toString(value));
	}

	/**
	 * returns the measured value.
	 * 
	 * @return measured value
	 */
	protected Integer getValue() {
		return value;
	}
}
