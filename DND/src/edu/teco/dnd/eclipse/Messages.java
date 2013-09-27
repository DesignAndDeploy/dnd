package edu.teco.dnd.eclipse;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "edu.teco.dnd.eclipse.messages"; //$NON-NLS-1$
	public static String ModuleView_CURRENTLY_AVAILABLE_MODULES;
	public static String ModuleView_LOCATION;
	public static String ModuleView_MODULE_ID;
	public static String ModuleView_MODULE_NAME;
	public static String ModuleView_SERVER_DOWN;
	public static String ModuleView_SERVER_RUNNING;
	public static String ModuleView_START_SERVER;
	public static String ModuleView_START_SERVER_TOOLTIP;
	public static String ModuleView_STARTING_SERVER;
	public static String ModuleView_STOP_SERVER;
	public static String ModuleView_STOPPING_SERVER;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}