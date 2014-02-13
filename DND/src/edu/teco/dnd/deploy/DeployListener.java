package edu.teco.dnd.deploy;

import java.util.UUID;

import edu.teco.dnd.module.ApplicationID;

/**
 * A listener for {@link Deploy} that is updated when the status of the deployment changes.
 * 
 * @author Philipp Adolf
 */
public interface DeployListener {
	/**
	 * This method is called as soon as a ModuleInfo joined the application successfully.
	 * 
	 * @param applicationID
	 *            the ID of the Application
	 * @param moduleUUID
	 *            the UUID of the ModuleInfo
	 */
	void moduleJoined(ApplicationID applicationID, UUID moduleUUID);

	/**
	 * This method is called when a ModuleInfo has loaded all classes it needs successfully.
	 * 
	 * @param applicationID
	 *            the ID of the Application
	 * @param moduleUUID
	 *            the UUID of the ModuleInfo
	 */
	void moduleLoadedClasses(ApplicationID applicationID, UUID moduleUUID);

	/**
	 * This method is called when a ModuleInfo has loaded its blocks successfully.
	 * 
	 * @param applicationID
	 *            the ID of the Application
	 * @param moduleUUID
	 *            the UUID of the ModuleInfo
	 */
	void moduleLoadedBlocks(ApplicationID applicationID, UUID moduleUUID);

	/**
	 * This method is called when a ModuleInfo has started the application successfully.
	 * 
	 * @param applicationID
	 *            the ID of the Application
	 * @param moduleUUID
	 *            the UUID of the ModuleInfo
	 */
	void moduleStarted(ApplicationID applicationID, UUID moduleUUID);

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
