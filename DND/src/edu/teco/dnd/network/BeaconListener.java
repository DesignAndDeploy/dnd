package edu.teco.dnd.network;

/**
 * A listener that will be informed if a beacon is received. Used by {@link UDPMulticastBeacon}.
 * 
 * @author Philipp Adolf
 */
public interface BeaconListener {
	/**
	 * This method is called if a beacon was found.
	 * 
	 * @param beacon the beacon that was found
	 */
	public void beaconFound(final BeaconMessage beacon);
}
