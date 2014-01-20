package edu.teco.dnd.server;

import java.util.Map;
import java.util.UUID;

import edu.teco.dnd.module.ModuleInfo;

/**
 * Provides information on running Modules (UUIDs and ModuleInfo objects) and on Server State. In general: All information
 * needed by the views and editors, can be extended if additional information needed.
 * 
 * @author jung
 * 
 */
public interface ModuleManagerListener {

	/**
	 * Informs that a module has been connected.
	 * 
	 * @param id
	 *            UUID of the module.
	 */
	void moduleOnline(UUID id);

	/**
	 * Informs that a module has been disconnected.
	 * 
	 * @param id
	 *            UUID of the ModuleInfo.
	 * @param module
	 *            ModuleInfo that has been removed. Might be null if module was not resolved before.
	 */
	void moduleOffline(UUID id, ModuleInfo module);

	/**
	 * Informs that UUID has been resolved to a module.
	 * 
	 * @param id
	 *            UUID of the module
	 * @param module
	 *            the ModuleInfo itself
	 */
	void moduleResolved(UUID id, ModuleInfo module);

	/**
	 * Informs that server is Online and provides HashMap of Modules that are already running.
	 * 
	 * @return
	 */
	void serverOnline(Map<UUID, ModuleInfo> modules);

	/**
	 * Informs that server is offline.
	 */
	void serverOffline();

}
