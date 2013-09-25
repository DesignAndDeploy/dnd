package edu.teco.dnd.eclipse.preferences;

import org.eclipse.swt.widgets.Text;

/**
 * This class can be used to check if a Text Field contains a valid port number.
 * 
 * @author jung
 * 
 */
public class TextPort extends TextCheck {

	/**
	 * Biggest valid Port number. Change if only system and user ports should be used.
	 * 
	 * System Ports: 0-1023
	 * 
	 * User Ports: 1024 - 49151
	 * 
	 * Dynamic Ports: 49152 - 65535
	 */
	public static final int maxPortNumber = 65535;

	/**
	 * Creates a new PortText that can be used to check the content of the Text Field
	 * 
	 * @param text
	 *            Text Field to be checked later
	 */
	public TextPort(Text port) {
		super(port, "Invalid Port Number");
	}

	@Override
	public boolean check() {
		int number = -1;
		try {
			number = Integer.parseInt(this.getText());
		} catch (NumberFormatException e) {
			return false;
		}
		if (number < 0) {
			return false;
		}
		return (number <= maxPortNumber);
	}

}
