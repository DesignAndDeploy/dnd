package edu.teco.dnd.eclipse.deployView;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "edu.teco.dnd.eclipse.deployView.messages"; //$NON-NLS-1$
	public static String DEPLOY_COLUMN1_TOOLTIP;
	public static String DEPLOY_COLUMN2_TOOLTIP;
	public static String DEPLOY_COLUMN3_TOOLTIP;
	public static String DEPLOY_COLUMN4_TOOLTIP;
	public static String DEPLOY_CONSTRAINTS_BLOCK_REMOVED;
	public static String DEPLOY_CONSTRAINTS_INFORM;
	public static String DEPLOY_CONSTRAINTS_NEW;
	public static String DEPLOY_CONSTRAINTS_TOOLTIP;
	public static String DEPLOY_CONSTRAINTS_WARN;
	public static String DEPLOY_CREATEDISTRIBUTION_TOOLTIP;
	public static String DEPLOY_DEPLOYDISTRIBUTION_TOOLTIP;
	public static String DEPLOY_NO_BLOCKS;
	public static String DEPLOY_NO_DIST_YET;
	public static String DEPLOY_NO_MODULES;
	public static String DEPLOY_NO_VALID_DISTRIBUTION;
	public static String DEPLOY_RENAMEBLOCK_TOOLTIP;
	public static String DEPLOY_SELECTMODULE_TOOLTIP;
	public static String DEPLOY_SELECTMODULEOFFLINE_TOOLTIP;
	public static String DEPLOY_SELECTPLACE_TOOLTIP;
	public static String DEPLOY_UPDATEBLOCKS_TOOLTIP;
	public static String DEPLOY_UPDATEMODULES_TOOLTIP;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
