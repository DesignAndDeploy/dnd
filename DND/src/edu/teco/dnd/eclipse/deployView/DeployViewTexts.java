package edu.teco.dnd.eclipse.deployView;

/**
 * Contains all longer texts displayed in the DeployView.
 * 
 * @author jung
 * 
 */
public class DeployViewTexts {

	public static final String COLUMN1_TOOLTIP = "Deploy function block on this module, if possible. No module selected means no constraint for deployment";

	public static final String COLUMN2_TOOLTIP = "Deploy function block at this place, if possible. No place selected means no constraint for deployment";

	public static final String COLUMN3_TOOLTIP = "Module assigned to the function block by the deployment algorithm.";

	public static final String COLUMN4_TOOLTIP = "Place the function block will be deployed to.";

	public static final String UPDATEMODULES_TOOLTIP = "Updates Information on currently available modules and running applications.";

	public static final String UPDATEBLOCKS_TOOLTIP = "Reloads the function blocks from the dataflowgraph.";

	public static final String CREATE_TOOLTIP = "Create a deployment concerning your constraints. To actually deploy the function blocks, press the Deploy button.";

	public static final String DEPLOY_TOOLTIP = "Deploy your application according to the displayed deployment.";

	public static final String RENAMEBLOCK_TOOLTIP = "Rename the selected function block.";

	public static final String SELECTMODULE_TOOLTIP = "Select a module for this function block to run on.";
	
	public static final String SELECTMODULEOFFLINE_TOOLTIP = "You have to start the server to be able to select among running modules";

	public static final String SELECTPLACE_TOOLTIP = "Enter a place for this function block to run on.";

	public static final String WARN_CONSTRAINTS = "You entered both a place and a module. Keep in mind that if the module doesn't match the place, no possible distribution will be found. If you still want to save both the module and the place for this function blockModel, press OK. If you want to abort, close this window.";

	public static final String NO_DEPLOYMENT_YET = "No deployment created yet";
}
