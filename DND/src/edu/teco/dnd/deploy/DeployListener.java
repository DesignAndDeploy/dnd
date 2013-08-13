package edu.teco.dnd.deploy;

import java.util.UUID;

/**
 * A listener for {@link Deploy} that is updated when the status of the deployment changes.
 * 
 * @author Philipp Adolf
 */
public interface DeployListener {
	/**
	 * This method is called as soon as a Module joined the application successfully.
	 * 
	 * @param appId
	 *            the UUID of the Application
	 * @param moduleUUID
	 *            the UUID of the Module
	 */
	void moduleJoined(UUID appId, UUID moduleUUID);

	/**
	 * This method is called when a Module has loaded all classes it needs successfully.
	 * 
	 * @param appId
	 *            the UUID of the Application
	 * @param moduleUUID
	 *            the UUID of the Module
	 */
	void moduleLoadedClasses(UUID appId, UUID moduleUUID);

	/**
	 * This method is called when a Module has loaded its blocks successfully.
	 * 
	 * @param appId
	 *            the UUID of the Application
	 * @param moduleUUID
	 *            the UUID of the Module
	 */
	void moduleLoadedBlocks(UUID appId, UUID moduleUUID);

	/**
	 * This method is called when a Module has started the application successfully.
	 * 
	 * @param appId
	 *            the UUID of the Application
	 * @param moduleUUID
	 *            the UUID of the Module
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
