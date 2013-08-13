package edu.teco.dnd.uPart;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

/**
 * This class is used to control the new and totally awesome beamer in teco.
 */
public class BeamerControl {

	/**
	 * Initializes a new BeamerControl for the default beamer in teco.
	 */
	public BeamerControl() {
	}

	/**
	 * Activates the beamer.
	 */
	public void activateBeamer() {
		if (!getBeamerState()) {
			accessBeamer();
		}
	}

	/**
	 * Deactivates the beamer.
	 */
	public void deactivateBeamer() {
		if (getBeamerState()) {
			accessBeamer();
			accessBeamer();
		}
	}

	/**
	 * Returns the state (On = true, off = false) of the beamer.
	 * 
	 * @return true if beamer is activated, false if it's turned off.
	 */
	public boolean getBeamerState() {
		try {
			InetAddress ipaddress = InetAddress.getByName("epsonbeamer.teco.edu");

			URL url = new URL("http://epsonbeamer.teco.edu/cgi-bin/webconf.exe?page=1");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Referer", "http://" + ipaddress.getHostAddress() + "/cgi-bin/webconf.exe?page=1");
			conn.setRequestMethod("GET");

			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line, result = "";

			while ((line = rd.readLine()) != null) {
				result += line;
			}

			return (result.indexOf("The projector is currently on standby") <= -1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Used to access beamer for turning it off / on.
	 */
	private void accessBeamer() {
		try {
			InetAddress ipaddress = InetAddress.getByName("epsonbeamer.teco.edu");

			URL url = new URL("http://epsonbeamer.teco.edu/cgi-bin/sender.exe?KEY=3B");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Referer", "http://" + ipaddress.getHostAddress() + "/cgi-bin/webconf.exe?page=1");
			conn.setRequestMethod("GET");

			InputStreamReader rd = new InputStreamReader(conn.getInputStream());
			while (rd.read() != -1) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
