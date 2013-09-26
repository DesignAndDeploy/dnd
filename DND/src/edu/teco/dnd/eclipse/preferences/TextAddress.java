package edu.teco.dnd.eclipse.preferences;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.swt.widgets.Text;

/**
 * This class can be used to check if a Text Field contains a valid internet address.
 * 
 * @author jung
 * 
 */
public class TextAddress extends TextCheck {

	/**
	 * Creates a new AddressText that can be used to check the content of the Text Field
	 * 
	 * @param text
	 *            Text Field to be checked later
	 */
	public TextAddress(Text text) {
		super(text, Messages.TextCheck_INVALID_IP_ADDRESS);
	}

	@Override
	public boolean check() {
		try {
			InetAddress.getByName(this.getText());
		} catch (UnknownHostException e1) {
			return false;
		}
		return true;
	}

}
