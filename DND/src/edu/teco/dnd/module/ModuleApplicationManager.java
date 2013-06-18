package edu.teco.dnd.module;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.config.BlockTypeHolder;
import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.module.messages.infoReq.AppInfoReqMsgHandler;
import edu.teco.dnd.module.messages.infoReq.AppInfoRequestMessage;
import edu.teco.dnd.module.messages.killApp.KillAppMessage;
import edu.teco.dnd.module.messages.killApp.KillAppMessageHandler;
import edu.teco.dnd.module.messages.loadStartClass.AppLoadClassMessage;
import edu.teco.dnd.module.messages.loadStartClass.AppLoadClassMessageHandler;
import edu.teco.dnd.module.messages.loadStartClass.AppStartClassMessage;
import edu.teco.dnd.module.messages.loadStartClass.AppStartClassMessageHandler;
import edu.teco.dnd.network.ConnectionManager;

public class ModuleApplicationManager {
	private static final Logger LOGGER = LogManager.getLogger(ModuleApplicationManager.class);

	public UUID localeModuleId;
	public ConfigReader moduleConfig;
	private Map<UUID, Application> runningApps = new HashMap<UUID, Application>();
	public int maxAllowedThreads;
	private final Map<UUID, ScheduledThreadPoolExecutor> scheduledAppPools = new HashMap<UUID, ScheduledThreadPoolExecutor>();
	private final ConnectionManager connMan;

	public ModuleApplicationManager(int maxAllowedThreads, int minThreadsPerApp, UUID localeModuleId,
			ConfigReader moduleConfig, ConnectionManager connMan) {
		this.localeModuleId = localeModuleId;
		this.maxAllowedThreads = maxAllowedThreads;
		this.moduleConfig = moduleConfig;
		this.connMan = connMan;
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
	 */
	public void startApplication(UUID appId, UUID deployingAgentId, String name) {
		LOGGER.info("starting app {} ({}), as requested by {}", name, appId, deployingAgentId);
		// TODO calculate proper size.
		ScheduledThreadPoolExecutor pool = scheduledAppPools.get(appId);
		if (pool == null) {
			pool = new ScheduledThreadPoolExecutor(maxAllowedThreads);
			scheduledAppPools.put(appId, pool);
		}

		Application newApp = new Application(appId, deployingAgentId, name, pool);

		runningApps.put(appId, newApp);
		connMan.addHandler(appId, AppLoadClassMessage.class, new AppLoadClassMessageHandler(this, newApp), pool);
		connMan.addHandler(appId, AppStartClassMessage.class, new AppStartClassMessageHandler(this, newApp), pool);
		connMan.addHandler(appId, AppInfoRequestMessage.class, new AppInfoReqMsgHandler(newApp), pool);
		connMan.addHandler(appId, KillAppMessage.class, new KillAppMessageHandler(this),pool);

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
	 * called, when a value for a given local functionblock.input was received. Passes the value on to the input.
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
	public void receiveValue(UUID appId, String funcBlockId, String input, Serializable value) {
		try {
			runningApps.get(appId).receiveValue(funcBlockId, input, value);
		} catch (IllegalAccessException e) {
			LOGGER.catching(e);
			LOGGER.info("Can not receive value {} @ input {}.{} in App {}({})", value, funcBlockId, input,
					runningApps.get(appId), appId);

		}
	}

	/**
	 * called to request stopping of a given application
	 * 
	 * @param appId
	 *            the id of the app to be stopped
	 * @return true iff successful
	 */
	public void stopApplication(UUID appId) {
		LOGGER.entry(appId);
		Application app = runningApps.get(appId);
		if (app == null) {
			return;
		}

		Collection<FunctionBlock> blocksKilled = app.getAllBlocks();

		app.shutdown();
		runningApps.remove(appId);
		for (FunctionBlock block : blocksKilled) {
			moduleConfig.getAllowedBlocks().get(block.getType()).increase();
		}
	}

	/**
	 * @return the runningApps
	 */
	public Map<UUID, Application> getRunningApps() {
		return runningApps;
	}

}
