package edu.teco.dnd.module;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

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
import edu.teco.dnd.module.messages.values.AppValueMessage;
import edu.teco.dnd.module.messages.values.AppValueMessageHandler;
import edu.teco.dnd.module.messages.values.AppWhoHasFuncBlockHandler;
import edu.teco.dnd.module.messages.values.AppWhoHasFuncBlockMessage;
import edu.teco.dnd.network.ConnectionManager;

public class ModuleApplicationManager {
	private static final Logger LOGGER = LogManager.getLogger(ModuleApplicationManager.class);

	public UUID localeModuleId;
	public ConfigReader moduleConfig;
	private Map<UUID, Application> runningApps = new HashMap<UUID, Application>();
	public final int maxAllowedThreadsPerApp;
	private final ConnectionManager connMan;

	public ModuleApplicationManager(int maxAllowedThreadsPerApp, UUID localeModuleId, ConfigReader moduleConfig,
			ConnectionManager connMan) {
		this.localeModuleId = localeModuleId;
		this.maxAllowedThreadsPerApp = maxAllowedThreadsPerApp;
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
	public void startApplication(final UUID appId, UUID deployingAgentId, String name) {
		LOGGER.info("starting app {} ({}), as requested by {}", name, appId, deployingAgentId);
		if (runningApps.containsKey(appId)) {
			LOGGER.info("trying to restart app that is already running.");
			return;
		}

		final ApplicationClassLoader classLoader = new ApplicationClassLoader();

		ThreadFactory fact = new ThreadFactory() {
			private int counter = 0;
			private UUID threadAppId = appId;

			@Override
			public Thread newThread(Runnable r) {
				Thread appThread = new Thread(r, "thread: app - " + threadAppId + " - " + counter);
				appThread.setContextClassLoader(classLoader); // prevent circumvention of our classLoader.
				return appThread;
			}
		};

		ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(maxAllowedThreadsPerApp, fact);
		Application newApp = new Application(appId, name, pool, connMan, classLoader);
		runningApps.put(appId, newApp);

		connMan.addHandler(appId, AppLoadClassMessage.class, new AppLoadClassMessageHandler(this, newApp), pool);
		connMan.addHandler(appId, AppStartClassMessage.class, new AppStartClassMessageHandler(this), pool);
		connMan.addHandler(appId, AppInfoRequestMessage.class, new AppInfoReqMsgHandler(newApp), pool);
		connMan.addHandler(appId, KillAppMessage.class, new KillAppMessageHandler(this), pool);
		connMan.addHandler(appId, AppValueMessage.class, new AppValueMessageHandler(newApp), pool);
		connMan.addHandler(appId, AppWhoHasFuncBlockMessage.class,
				new AppWhoHasFuncBlockHandler(newApp, localeModuleId));

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
