package edu.teco.dnd.deploy;

import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.module.ModuleID;

/**
 * <p>
 * The general order of events is:
 * </p>
 * 
 * <ol>
 * <li>A module joins ({@link #moduleJoined(UUID, UUID)})</li>
 * <li>The module loads the necessary classes ({@link #moduleLoadedClasses(UUID, UUID)})</li>
 * <li>The module loads the blocks ({@link #moduleLoadedBlocks(UUID, UUID)})</li>
 * <li>The module starts ({@link #moduleStarted(UUID, UUID)})</li>
 * </ol>
 * 
 * <p>
 * If at any point an error occurs, {@link #deployFailed(UUID, Throwable)} is called. Not that the order is only set for
 * a single Module, multiple Modules may be at different stages at the same time. However, no Module will be started
 * unless all Modules have received all blocks.
 * </p>
 * 
 * @author Philipp Adolf
 */
public interface DeployListener {
	/**
	 * This method is called as soon as a Module joined the application successfully.
	 * 
	 * @param applicationID
	 *            the ID of the Application
	 * @param moduleID
	 *            the ID of the Module
	 */
	void moduleJoined(ApplicationID applicationID, ModuleID moduleID);

	/**
	 * This method is called when a Module has loaded all classes it needs successfully.
	 * 
	 * @param applicationID
	 *            the ID of the Application
	 * @param moduleID
	 *            the ID of the Module
	 */
	void moduleLoadedClasses(ApplicationID applicationID, ModuleID moduleID);

	/**
	 * This method is called when a Module has loaded its blocks successfully.
	 * 
	 * @param applicationID
	 *            the ID of the Application
	 * @param moduleID
	 *            the ID of the Module
	 */
	void moduleLoadedBlocks(ApplicationID applicationID, ModuleID moduleID);

	/**
	 * This method is called when a Module has started the application successfully.
	 * 
	 * @param applicationID
	 *            the ID of the Application
	 * @param moduleID
	 *            the ID of the Module
	 */
	void moduleStarted(ApplicationID applicationID, ModuleID moduleID);

	/**
	 * This method is called if the deployment fails. Other methods of the listener may still be called afterwards due
	 * to race conditions; these calls should be ignored.
	 * 
	 * @param applicationID
	 *            the ID of the Application that failed
	 * @param cause
	 *            the cause of the failure if know or null otherwise
	 */
	void deployFailed(ApplicationID applicationID, Throwable cause);
}
