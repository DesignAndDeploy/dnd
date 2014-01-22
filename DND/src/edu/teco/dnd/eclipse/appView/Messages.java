package edu.teco.dnd.eclipse.appView;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "edu.teco.dnd.eclipse.appView.messages"; //$NON-NLS-1$
	public static String ApplicationView_UPDATE;
	public static String ApplicationView_KILL_APPLICATION;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
