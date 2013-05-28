package edu.teco.dnd.module;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.config.BlockTypeHolder;
import edu.teco.dnd.module.config.ConfigReader;

public class ModuleApplicationManager {
	private static final Logger LOGGER = LogManager.getLogger(ModuleApplicationManager.class);
	
	public  UUID localeModuleId;
	public  ConfigReader moduleConfig;
	private Map<UUID, Application> runningApps = new HashMap<UUID, Application>();
	public int maxAllowedThreads; 
	public final Scheduler scheduler;
	
	

	public ModuleApplicationManager(int maxAllowedThreads, UUID localeModuleId, ConfigReader moduleConfig) {
		this.localeModuleId = localeModuleId;
		this.maxAllowedThreads = maxAllowedThreads;
		this.moduleConfig = moduleConfig;
		
		
		this.scheduler = new Scheduler(maxAllowedThreads);
	}

	/**
	 * called from this module, when a value is supposed to be send to another
	 * block (potentially on another Module).
	 * 
	 * @param funcBlock
	 *            the receiving functionBlock.
	 * @param input
	 *            the input on the given block to receive the message.
	 * @param val
	 *            the value to be send.
	 * @return true iff setting was successful.
	 */
	public boolean sendValue(String funcBlock, String input, Serializable val) {
		// TODO tell networking, that we want to send this value :)
		return false;
	}

	/**
	 * called when a new application is supposed to be started.
	 * 
	 * @param appId
	 *            the Id of the app to be started.
	 * @param deployingAgentId
	 *            the agent requesting the start of this application.
	 * @param name
	 *            (human readable) name of the application
	 * 
	 * @return true iff successful.
	 */
	public boolean startApplication(UUID appId, UUID deployingAgentId, String name) {
		LOGGER.info("starting app {} ({}), as requested by {}", name, appId, deployingAgentId);
		// TODO scheduling/thread/forking
		// TODO tell network part that we have a new app?
		runningApps.put(appId, new Application(appId, deployingAgentId, name, scheduler));

		return false;
	}

	/**
	 * called to signal necessity to load a class.
	 * 
	 * @param appId
	 *            the Id of the app to which this message is directed.
	 * @param classnames
	 *            names of the classes to be loaded
	 * @param mainclassname
	 *            name of the main class, which triggered the loading.
	 * @return true iff successful.
	 */
	public boolean loadClass(UUID appId, Set<String> classnames, String mainclassname) {
		// TODO is this actually needed?
		if (!runningApps.get(appId).loadClass(classnames, mainclassname)) {
			LOGGER.warn("Can not loadClass {} - ({}) in App {} ({})", classnames, mainclassname,
					runningApps.get(appId), appId);
			return false;
		}
		return true;
	}

	/**
	 * called if a block should be started.
	 * 
	 * @param appId
	 *            the app this is directed to.
	 * @param func
	 *            the block to start.
	 * @return true iff starting was successful.
	 */
	public boolean startBlock(UUID appId, FunctionBlock func) {
		BlockTypeHolder blockAllowed = moduleConfig.getAllowedBlocks().get(func.getType());
		if (blockAllowed == null) {
			LOGGER.info("Block {} not allowed in App {}({})", func, runningApps.get(appId), appId);
			return false;
		}

		if (blockAllowed.tryDecrease()) {
			LOGGER.info("Blockamount of {} exceeded. Not starting!", func.getType());
			return false;
		}
		if (!runningApps.get(appId).startBlock(func)) {
			LOGGER.warn("Can not start block {} in App {}({})", func, runningApps.get(appId), appId);
			return false;
		}
		return true;
	}

	/**
	 * called, when a value for a given local functionblock.input was received.
	 * Passes the value on to the input.
	 * 
	 * @param appId
	 *            the id of the app the block belongs to.
	 * @param funcBlockId
	 *            the id of the functionBlock receiving the value.
	 * @param input
	 *            the input of the function block to receive the value.
	 * @param value
	 *            the value to be handed to the functionBlock
	 * @return true iff the action succeeded.
	 */
	public boolean receiveValue(UUID appId, String funcBlockId, String input, Serializable value) {
		if (!runningApps.get(appId).receiveValue(funcBlockId, input, value)) {
			LOGGER.info("Can not receive value {} @ input {}.{} in App {}({})", value, funcBlockId, input,
					runningApps.get(appId), appId);
			return false;
		}
		return true;
	}

	/**
	 * called to request stopping of a given application
	 * 
	 * @param appId
	 *            the id of the app to be stopped
	 * @return true iff successful
	 */
	public boolean stopApplication(UUID appId) {
		LOGGER.entry(appId);
		Application app = runningApps.get(appId);
		if (app == null) {
			return false;
		}

		Collection<FunctionBlock> blocksKilled = app.getAllBlocks();

		if (!app.shutdown()) {
			LOGGER.warn("Can not stop App {}({})", runningApps.get(appId), appId);
			return false;
		}
		runningApps.remove(appId);
		for (FunctionBlock block : blocksKilled) {
			moduleConfig.getAllowedBlocks().get(block.getType()).increase();
		}

		// TODO tell internet, that the app is stopped?
		return true;
	}

}
