package edu.teco.dnd.uPart.test;

import java.net.MalformedURLException;

import edu.teco.dnd.uPart.OutletControl;

import org.eclipse.jface.dialogs.InputDialog;
import org.junit.Before;

/**
 * This test tests the activating of an outlet / the beamer.
 */
public final class OutletDeactivatorTest {

	/**
	 * Empty Constructor.
	 */
	private OutletDeactivatorTest() {

	}

	/**
	 * initializes ID and outletControl.
	 * 
	 * @param args
	 *            args
	 * @throws MalformedURLException
	 *             if url of the outlet is malformed.
	 */
	@Before
	public static void main(final String[] args) throws MalformedURLException {
		InputDialog id = new InputDialog(null, "Outlet", "Bitte gewünschte Steckdose eingeben",
				"http://cumulus.teco.edu:5000/plugwise/000D6F0000768B58", null);
		id.open();
		String outletID = id.getValue();
		OutletControl control = new OutletControl(outletID);
		control.deactivateOutlet();
		System.out.println("Outlet deactivated");
	}
}
