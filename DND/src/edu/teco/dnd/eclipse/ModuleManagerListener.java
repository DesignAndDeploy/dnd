package edu.teco.dnd.eclipse;

import java.util.Map;
import java.util.UUID;

import edu.teco.dnd.module.Module;

/**
 * Provides information on running Modules (UUIDs and Module objects) and on Server State. In general: All information
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
	 *            UUID of the Module.
	 */
	void moduleOffline(UUID id);

	/**
	 * Informs that UUID has been resolved to a module.
	 * 
	 * @param id
	 *            UUID of the module
	 * @param module
	 *            the Module itself
	 */
	void moduleResolved(UUID id, Module module);

	/**
	 * Informs that server is Online and provides HashMap of Modules that are already running.
	 * 
	 * @return
	 */
	void serverOnline(Map<UUID, Module> modules);

	/**
	 * Informs that server is offline.
	 */
	void serverOffline();

}
