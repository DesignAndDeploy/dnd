package edu.teco.dnd.uPart;

/**
 * contains the Data that can be measured at an outlet.
 */
public class OutletData {

	/**
	 * consumed energy.
	 */
	private Data energy;

	/**
	 * returns measured energy.
	 * 
	 * @return measured energy.
	 */
	protected Integer getEnergy() {
		return energy.getValue();
	}
}
