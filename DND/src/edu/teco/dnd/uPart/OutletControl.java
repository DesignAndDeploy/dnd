package edu.teco.dnd.uPart;

import java.io.IOException;
import java.net.URL;

/**
 * This class is used for activating and deactivating an outlet.
 */
public class OutletControl {

	/**
	 * URL of the outlet to control.
	 */
	private String urlString;

	/**
	 * Initializes a new OutletControl.
	 * 
	 * @param url
	 *            URL of the outlet to control
	 */
	public OutletControl(final String url) {
		this.urlString = url;
	}

	/**
	 * Activates an outlet.
	 */
	public void activateOutlet() {
		try {
			URL url = new URL(urlString.concat("/on"));
			url.openStream();
		} catch (IOException e) {
		}
	}

	/**
	 * Deactivates an outlet.
	 */
	public void deactivateOutlet() {
		try {
			URL url = new URL(urlString.concat("/off"));
			url.openStream();
		} catch (IOException e) {
		}
	}

}
