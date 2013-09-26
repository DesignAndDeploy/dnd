package edu.teco.dnd.eclipse.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "edu.teco.dnd.eclipse.preferences.messages"; //$NON-NLS-1$
	public static String Preferences_GENERAL_SETTINGS;
	public static String PreferencesNetwork_5;
	public static String PreferencesNetwork_ADDRESS_ANNOUNCE;
	public static String PreferencesNetwork_ADDRESS_FOR_ECLIPSE;
	public static String PreferencesNetwork_ADDRESS_FOR_MULTICASTS;
	public static String PreferencesNetwork_BEACON_INTERVAL;
	public static String PreferencesNetwork_DESCRIPTION;
	public static String PreferencesNetwork_NETWORK_FOR_MULTICASTS;
	public static String PreferencesNetwork_NETWORK_INTERFACE;
	public static String PreferencesNetwork_PORT_ANNOUNCE;
	public static String PreferencesNetwork_PORT_FOR_ECLIPSE;
	public static String PreferencesNetwork_PORT_FOR_MULTICASTS;
	public static String TextCheck_INVALID_IP_ADDRESS;
	public static String TextCheck_INVALID_NETWORK_INTERFACE;
	public static String TextCheck_INVALID_PORT_NUMBER;
	public static String TextCheck_WARNING;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
