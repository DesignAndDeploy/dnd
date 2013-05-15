package edu.teco.dnd.eclipse.view;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "edu.teco.dnd.eclipse.view.messages"; //$NON-NLS-1$
	public static String DeployEditor_CouldNotDistribute_Inform;
	public static String DeployEditor_CreatingDistribution_Inform;
	public static String DeployEditor_Deploy_Btn;
	public static String DeployEditor_Deployed_Inform;
	public static String DeployEditor_DeployError_Inform;
	public static String DeployEditor_DeploymentFailed;
	public static String DeployEditor_DeployOpenDiagram_ToolTip;
	public static String DeployEditor_DiagrammCorrupt;
	public static String DeployEditor_EmptyDiagram;
	public static String DeployEditor_ErrorReadingFile_Inform;
	public static String DeployEditor_IdOfBlock_columnHeader;
	public static String DeployEditor_DiscoverStarter;
	public static String DeployEditor_LoadApp_Inform;
	public static String DeployEditor_LoadingBlocks;
	public static String DeployEditor_LocationOfModule_ColumnHeader;
	public static String DeployEditor_MaxModuleStategy_Anouncement;
	public static String DeployEditor_MinModulesStrategy_Anouncement;
	public static String DeployEditor_NameOfModule_ColumnHeader;
	public static String DeployEditor_NoModules_Inform;
	public static String DeployEditor_DiscoverButton;
	public static String DeployEditor_ReloadDistr_Tooltip;
	public static String DeployEditor_TypeOfBlock_ColumnHeader;
	public static String DeployEditor_UnsupportedFile_Inform;

	public static String DiscoveryView_AppId_ColumnHead;
	public static String DiscoveryView_AvailableModules_TabHead;
	public static String DiscoveryView_ColumnName_ColumnHead;
	public static String DiscoveryView_CurrentlyUsed_Label;
	public static String DiscoveryView_Discover_BTN;
	public static String DiscoveryView_ErrorOnDiscover_Label;
	public static String DiscoveryView_IdOfAgentModule_ColumnHead;
	public static String DiscoveryView_IdOfApp_ColumnHead;
	public static String DiscoveryView_Location_ColumnHead;
	public static String DiscoveryView_LocationOfApp_ColumnHead;
	public static String DiscoveryView_LocationOfModule_ColumnHead;
	public static String DiscoveryView_ModuleAppRunsOn_ColumnHead;
	public static String DiscoveryView_Name_ColumnHead;
	public static String DiscoveryView_NameOfApp_ColumnHeader;
	public static String DiscoveryView_RestartEclipse_Info;
	public static String DiscoveryView_RunningApp_Label;
	public static String DiscoveryView_SearchAllModules_ToolTip;
	public static String DeployEditor_CouldNotDeploy;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
