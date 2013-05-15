package edu.teco.dnd.uPart.test;

import java.net.MalformedURLException;

import edu.teco.dnd.uPart.HeaterControl;
import edu.teco.dnd.uPart.SensorException;

import org.eclipse.jface.dialogs.InputDialog;

/**
 * This class is for testing the HeaterControl.
 */
public final class HeaterControlTest {

	/**
	 * empty Constructor.
	 */
	private HeaterControlTest() {

	}

	/**
	 * Initializes a heater control.
	 * 
	 * @param args
	 *            args
	 */
	public static void main(final String[] args) {
		InputDialog id = new InputDialog(null, "Temperatur", "Bitte gewünschte Temperatur eingeben", "22",
				null);
		id.open();
		Integer temp = Integer.parseInt(id.getValue());
		id = new InputDialog(null, "Heizung", "Bitte gewünschte Heizung eingeben",
				"http://cumulus.teco.edu:51525/actuator/entity/Heater_214/function/set", null);
		id.open();
		try {
			HeaterControl control = new HeaterControl(id.getValue());
			control.updateHeater(temp);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SensorException e) {
			e.printStackTrace();
		}

		System.out.println("Temp: " + temp);
	}

}
