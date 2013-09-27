package edu.teco.dnd.graphiti.model.impl;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "edu.teco.dnd.graphiti.model.impl.messages"; //$NON-NLS-1$
	public static String BLOCK_DEFAULT_BLOCKNAME;
	public static String BLOCK_DEFAULT_POSITION;
	public static String BLOCK_TOSTRING_BLOCKCLASS;
	public static String BLOCK_TOSTRING_BLOCKNAME;
	public static String BLOCK_TOSTRING_ID;
	public static String BLOCK_TOSTRING_POSITION;
	public static String BLOCK_TOSTRING_TYPE;
	public static String Graphiti_impl_OUTPUT_TOSTRING_NAME;
	public static String Graphiti_impl_OUTPUT_TOSTRING_TYPE;
	public static String INPUT_TOSTRING_NAME;
	public static String INPUT_TOSTRING_TYPE;
	public static String OPTION_TOSTRING_NAME;
	public static String OPTION_TOSTRING_TYPE;
	public static String OPTION_TOSTRING_VALUE;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
