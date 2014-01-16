package edu.teco.dnd.deploy;

import java.util.UUID;

/**
 * A listener for {@link Deploy} that is updated when the status of the deployment changes.
 * 
 * @author Philipp Adolf
 */
public interface DeployListener {
	/**
	 * This method is called as soon as a ModuleInfo joined the application successfully.
	 * 
	 * @param appId
	 *            the UUID of the Application
	 * @param moduleUUID
	 *            the UUID of the ModuleInfo
	 */
	void moduleJoined(UUID appId, UUID moduleUUID);

	/**
	 * This method is called when a ModuleInfo has loaded all classes it needs successfully.
	 * 
	 * @param appId
	 *            the UUID of the Application
	 * @param moduleUUID
	 *            the UUID of the ModuleInfo
	 */
	void moduleLoadedClasses(UUID appId, UUID moduleUUID);

	/**
	 * This method is called when a ModuleInfo has loaded its blocks successfully.
	 * 
	 * @param appId
	 *            the UUID of the Application
	 * @param moduleUUID
	 *            the UUID of the ModuleInfo
	 */
	void moduleLoadedBlocks(UUID appId, UUID moduleUUID);

	/**
	 * This method is called when a ModuleInfo has started the application successfully.
	 * 
	 * @param appId
	 *            the UUID of the Application
	 * @param moduleUUID
	 *            the UUID of the ModuleInfo
	 */
	void moduleStarted(UUID appId, UUID moduleUUID);

	/**
	 * This method is called if the deployment fails. Other methods of the listener may still be called afterwards due
	 * to race conditions; these calls should be ignored.
	 * 
	 * @param appId
	 *            the UUID of the Application that failed
	 * @param cause
	 *            the cause of the failure if know or null otherwise
	 */
	void deployFailed(UUID appId, Throwable cause);
}
