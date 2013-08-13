package edu.teco.dnd.eclipse;

import java.net.NetworkInterface;
import java.net.SocketException;

import org.eclipse.swt.widgets.Text;

/**
 * This class can be used to check if a Text Field contains a valid network interface.
 * 
 * @author jung
 * 
 */
public class TextNetwork extends TextCheck {
	/**
	 * Creates a new NetworkText that can be used to check the content of the Text Field
	 * 
	 * @param text
	 *            Text Field to be checked later
	 */
	public TextNetwork(Text text) {
		super(text, "Invalid Network Interface");
	}

	@Override
	public boolean check() {
		NetworkInterface network;
		try {
			network = NetworkInterface.getByName(getText());
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return (network != null);
	}

}
