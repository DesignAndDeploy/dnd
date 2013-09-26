package edu.teco.dnd.eclipse.appView;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "edu.teco.dnd.eclipse.appView.messages"; //$NON-NLS-1$
	public static String AppView_APP_KILLED;
	public static String AppView_APP_SELECTED;
	public static String AppView_APPLICATION;
	public static String AppView_APPLICATIONS;
	public static String AppView_CANCEL_ERROR;
	public static String AppView_COLON;
	public static String AppView_EMPTYSTRING;
	public static String AppView_MODULE;
	public static String AppView_MODULE_NOT_RESOLVED;
	public static String AppView_MODULES;
	public static String AppView_ON_MODULE;
	public static String AppView_SORT_BY_APPS;
	public static String AppView_SORT_BY_MODS;
	public static String AppView_SORT_WARNING;
	public static String AppView_WARN_RESTART;
	public static String AppView_WARNING;
	public static String AppViewGraphics_CURRENTLY_RUNNING_BLOCKS;
	public static String AppViewGraphics_DO_SELECT;
	public static String AppViewGraphics_FUNCTION_BLOCK;
	public static String AppViewGraphics_KILL_APPLICATION;
	public static String AppViewGraphics_TYPE;
	public static String AppViewGraphics_UPDATE;
	public static String AppViewGraphics_UUID;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
