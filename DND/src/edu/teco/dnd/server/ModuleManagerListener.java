package edu.teco.dnd.server;

import edu.teco.dnd.module.ModuleInfo;

/**
 * These listeners are informed by a {@link ModuleManager} once new Modules are discovered, their information is fetched
 * and when they disconnect.
 * 
 * @author Philipp Adolf
 */
public interface ModuleManagerListener {
	/**
	 * This method is called once a new Module is connected.
	 * 
	 * @param moduleInfo
	 *            the information known about the Module. This will only be the ID most of the time, the other entries
	 *            may be null
	 */
	void moduleAdded(ModuleInfo moduleInfo);

	/**
	 * This method is called when a Module disconnects.
	 * 
	 * @param moduleInfo
	 *            the information that was known about the Module when it disconnect. May only be the ID.
	 */
	void moduleRemoved(ModuleInfo moduleInfo);

	/**
	 * This method is called when new information about a Module is known.
	 * 
	 * @param moduleInfo
	 *            the information known about the Module. Only the ID is guaranteed to be set.
	 */
	void moduleUpdated(ModuleInfo moduleInfo);
}
