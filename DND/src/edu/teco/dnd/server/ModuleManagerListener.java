package edu.teco.dnd.server;

import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.module.ModuleInfo;

/**
 * These listeners are informed by a {@link ModuleManager} once new {@link Module}s are discovered, once their
 * information is fetched and once they disconnect.
 */
public interface ModuleManagerListener {
	/**
	 * This method is called once a new {@link Module} is connected.
	 * 
	 * @param moduleInfo
	 *            the information known about the Module. This will only be the {@link ModuleID} most of the time, the
	 *            other entries may be <code>null</code>
	 */
	void moduleAdded(ModuleInfo moduleInfo);

	/**
	 * This method is called when a {@link Module} disconnects.
	 * 
	 * @param moduleInfo
	 *            the information that was known about the Module when it disconnect. May only be the {@link ModuleID}.
	 */
	void moduleRemoved(ModuleInfo moduleInfo);

	/**
	 * This method is called when new information about a {@link Module} is known.
	 * 
	 * @param moduleInfo
	 *            the information known about the Module. Only the {@link ModuleID} is guaranteed to be set.
	 */
	void moduleUpdated(ModuleInfo moduleInfo);
}
