package edu.teco.dnd.module;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.config.BlockTypeHolder;
import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.module.messages.joinStartApp.StartApplicationMessage;
import edu.teco.dnd.module.messages.joinStartApp.StartApplicationMessageHandler;
import edu.teco.dnd.module.messages.killApp.KillAppMessage;
import edu.teco.dnd.module.messages.killApp.KillAppMessageHandler;
import edu.teco.dnd.module.messages.loadStartBlock.BlockMessage;
import edu.teco.dnd.module.messages.loadStartBlock.BlockMessageHandler;
import edu.teco.dnd.module.messages.loadStartBlock.LoadClassMessage;
import edu.teco.dnd.module.messages.loadStartBlock.LoadClassMessageHandler;
import edu.teco.dnd.module.messages.values.ValueMessage;
import edu.teco.dnd.module.messages.values.ValueMessageHandler;
import edu.teco.dnd.module.messages.values.WhoHasBlockMessage;
import edu.teco.dnd.module.messages.values.WhoHasFuncBlockHandler;
import edu.teco.dnd.network.ConnectionManager;

public class ModuleApplicationManager {
	private static final Logger LOGGER = LogManager.getLogger(ModuleApplicationManager.class);

	public UUID localeModuleId;
	public ConfigReader moduleConfig;
	private Map<UUID, Application> runningApps = new HashMap<UUID, Application>();
	public final int maxAllowedThreadsPerApp;
	private final ConnectionManager connMan;
	private final Set<FunctionBlock> scheduledToStart = new HashSet<FunctionBlock>();
	private final ReadWriteLock isShuttingDown = new ReentrantReadWriteLock();

	public ModuleApplicationManager(ConfigReader moduleConfig, ConnectionManager connMan) {
		this.localeModuleId = moduleConfig.getUuid();
		this.maxAllowedThreadsPerApp = moduleConfig.getMaxThreadsPerApp();
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
	public void joinApplication(final UUID appId, UUID deployingAgentId, String name) {
		LOGGER.info("joining app {} ({}), as requested by {}", name, appId, deployingAgentId);
		isShuttingDown.readLock().lock();
		try {
			if (runningApps.containsKey(appId)) {
				LOGGER.info("trying to rejoin app that was already joined before.");
				return;
			}

			final ApplicationClassLoader classLoader = new ApplicationClassLoader(connMan, appId);

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

			connMan.addHandler(appId, LoadClassMessage.class, new LoadClassMessageHandler(this, newApp), pool);
			connMan.addHandler(appId, BlockMessage.class, new BlockMessageHandler(this), pool);
			connMan.addHandler(appId, StartApplicationMessage.class, new StartApplicationMessageHandler(this), pool);
			connMan.addHandler(appId, KillAppMessage.class, new KillAppMessageHandler(this), pool);
			connMan.addHandler(appId, ValueMessage.class, new ValueMessageHandler(newApp), pool);
			connMan.addHandler(appId, WhoHasBlockMessage.class, new WhoHasFuncBlockHandler(newApp, localeModuleId));
		} finally {
			isShuttingDown.readLock().unlock();
		}

	}

	/**
	 * Schedules a FunctionBlock to be started, when StartApp() is called.
	 * 
	 * @param insecureBlock
	 *            the FunctionBlock
	 */
	public boolean scheduleBlock(UUID appId, FunctionBlock insecureBlock) {
		FunctionBlock block;
		if (insecureBlock == null) {
			LOGGER.warn("send block message with NULL block.");
			return false;
		}
		try {
			block = new FunctionBlockSecurityDecorator(insecureBlock);
		} catch (UserSuppliedCodeException e) {
			LOGGER.warn("Not scheduling block {} ({}), because of error in code.", insecureBlock.getBlockName(),
					insecureBlock.getID());
			return false;
		}
		isShuttingDown.readLock().lock();
		String blockType = block.getType();

		try {
			BlockTypeHolder blockAllowed = moduleConfig.getAllowedBlocks().get(blockType);
			if (blockAllowed == null) {
				LOGGER.info("Block {} not allowed in App {}({})", blockType, runningApps.get(appId), appId);
				return false;
			}

			if (!blockAllowed.tryDecrease()) {
				LOGGER.info("Blockamount of {} exceeded. Not scheduling!", blockType);
				return false;
			}
			scheduledToStart.add(block);
			LOGGER.info("succesfully scheduled block {}, in App {}({})", block, runningApps.get(appId), appId);
			return true;
		} finally {
			isShuttingDown.readLock().unlock();
		}
	}

	public void startApp(UUID appId) {
		for (FunctionBlock func : scheduledToStart) {
			Application app = runningApps.get(appId);
			if (app == null) {
				LOGGER.warn("Tried to start non existing app: {}", appId);
				throw new IllegalArgumentException("tried to start app that does not exist.");
			} else {
				runningApps.get(appId).startBlock(func);
			}

		}
	}

	/**
	 * called if a block should be started.
	 * 
	 * @param appId
	 *            the app this is directed to.
	 * @param block
	 *            the block to start.
	 * @return true iff starting was successful.
	 */
	public boolean startBlock(UUID appId, FunctionBlock block) {
		String blockType;
		blockType = block.getType();

		BlockTypeHolder blockAllowed = moduleConfig.getAllowedBlocks().get(blockType);
		if (blockAllowed == null) {
			// do not log the block directly, as that would call block.toString which is untrusted code
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Block {} not allowed in App {}({})", block.getID(), runningApps.get(appId), appId);
			}
			return false;
		}

		if (blockAllowed.tryDecrease()) {
			LOGGER.info("Blockamount of {} exceeded. Not starting!", blockType);
			return false;
		}

		return true;
	}

	/**
	 * triggers a shutdown of all Applications because the module is being shutdown.
	 */
	public void shutdownModule() {
		isShuttingDown.writeLock().lock();
		try {
			ModuleMain.shutdownNetwork();
			for (UUID appId : runningApps.keySet()) {
				stopApplication(appId);
			}
		} finally {
			isShuttingDown.writeLock().unlock();
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

			BlockTypeHolder holder = moduleConfig.getAllowedBlocks().get(block.getType());
			if (holder != null) {
				holder.increase();
			} else {
				LOGGER.warn("Block returned bogous blocktype on shutdown. Can not free resources.");
			}
		}
	}

	/**
	 * @return the runningApps
	 */
	public Map<UUID, Application> getRunningApps() {
		return runningApps;
	}

	/**
	 * @return the class loader of an app. null if none.
	 */
	public ClassLoader getAppClassLoader(UUID appId) {
		Application app = runningApps.get(appId);
		return (app == null) ? null : app.getClassLoader();
	}
}
