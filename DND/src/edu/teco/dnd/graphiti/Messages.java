package edu.teco.dnd.graphiti;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "edu.teco.dnd.graphiti.messages"; //$NON-NLS-1$
	public static String DNDCreateBlockFeature_CreatesFunBlockOfTpe_Info;
	public static String DNDCreateDataConnectionFeature_CreatConnection_Short_Info;
	public static String DNDCreateDataConnectionFeature_CreateConnection_Long_Info;
	public static String DNDEditIntegerOptionFeature_NotANumber_Info;
	public static String DNDEditPositionFeature_NotARegex_Info;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
