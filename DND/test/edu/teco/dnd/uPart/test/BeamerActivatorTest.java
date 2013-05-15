package edu.teco.dnd.uPart.test;

import edu.teco.dnd.uPart.BeamerControl;

/**
 * Used to activate beamer.
 */
public final class BeamerActivatorTest {

	/**
	 * empty constructor to satisfy checkstyle.
	 */
	private BeamerActivatorTest() {
	}

	/**
	 * activates beamer.
	 * 
	 * @param args
	 *            args
	 */
	public static void main(final String[] args) {
		BeamerControl control = new BeamerControl();
		control.activateBeamer();
	}

}
