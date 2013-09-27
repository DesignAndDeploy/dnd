package edu.teco.dnd.graphiti;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "edu.teco.dnd.graphiti.messages"; //$NON-NLS-1$
	public static String Graphiti_addBlock_FONTNAME;
	public static String Graphiti_addBlock_NAME;
	public static String Graphiti_addBlock_POSITION;
	public static String Graphiti_BRACE_LEFT;
	public static String Graphiti_BRACE_RIGHT;
	public static String Graphiti_CREATE_CONNECTION;
	public static String Graphiti_CREATE_CONNECTION_DESCRIPTION;
	public static String Graphiti_createBlock_CREATE_DESCRIPTION;
	public static String Graphiti_DEBUG;
	public static String Graphiti_DEBUG_DESCRIPTION;
	public static String Graphiti_EMPTYSTRING;
	public static String Graphiti_NOT_A_REGEX;
	public static String Graphiti_PALETTE_BLOCKS;
	public static String Graphiti_PALETTE_CONNECTIONS;
	public static String Graphiti_PALETTE_OTHER;
	public static String Graphiti_SPACE;
	public static String Graphiti_UPDATE_CUSTOM;
	public static String Graphiti_UPDATE_CUSTOM_DESCRIPTION;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
