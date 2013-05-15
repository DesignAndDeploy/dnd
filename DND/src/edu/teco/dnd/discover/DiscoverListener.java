package edu.teco.dnd.discover;

import java.util.Map;

import lime.AgentID;

import edu.teco.dnd.module.ApplicationAgent;
import edu.teco.dnd.module.CommunicationAgent;
import edu.teco.dnd.module.Module;

/**
 * A listener for {@link Discover}.
 */
public interface DiscoverListener {
	/**
	 * This method is called if modules were discovered in the module space.
	 * 
	 * @param modules
	 *            a map containing all modules identified by the AgentID of their {@link CommunicationAgent}.
	 * @see Discover#startModuleDiscovery()
	 */
	void modulesDiscovered(Map<AgentID, Module> modules);

	/**
	 * This method is called if modules belonging to an application were discovered.
	 * 
	 * @param appID
	 *            the ID of the application
	 * @param modules
	 *            the IDs of the modules mapped by the AgentID of their {@link ApplicationAgent}
	 * @see Discover#startApplicationScan(int)
	 */
	void applicationModulesDiscovered(int appID, Map<AgentID, Long> modules);

	/**
	 * This method is called if applications were discovered in the module space.
	 * 
	 * @param applications
	 *            the IDs and names of the applications
	 * @see Discover#startApplicationDiscovery()
	 */
	void applicationsDiscovered(Map<Integer, String> applications);
}
