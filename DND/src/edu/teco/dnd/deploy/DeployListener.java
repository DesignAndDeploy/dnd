package edu.teco.dnd.deploy;

/**
 * Interface that all listeners can use to be notified about the progress of a deployment.
 */
public interface DeployListener {
	/**
	 * Update function, notifying about changes. Certain values could be skipped by the caller, meaning if
	 * current is 1 it's not necessarily followed next time by 2. (This way faster parts of a program can be
	 * adjusted to slower parts)
	 * 
	 * @param classesLoaded
	 *            the number of block classes that have been loaded
	 * @param blocksStarted
	 *            the number of blocks that have been started
	 */
	void updateDeployStatus(int classesLoaded, int blocksStarted);

	/**
	 * This function is called if an error occurs during deployment.
	 * 
	 * @param message
	 *            a message describing the error
	 */
	void deployError(String message);
}
