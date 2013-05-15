package edu.teco.dnd.uPart.test;

import edu.teco.dnd.uPart.BeamerControl;

/**
 * Used to deactivate beamer.
 */
public final class BeamerDeactivatorTest {

	/**
	 * empty constructor to satisfy checkstyle.
	 */
	private BeamerDeactivatorTest() {

	}

	/**
	 * activates beamer.
	 * 
	 * @param args
	 *            args
	 */
	public static void main(final String[] args) {
		BeamerControl control = new BeamerControl();
		control.deactivateBeamer();
	}

}
