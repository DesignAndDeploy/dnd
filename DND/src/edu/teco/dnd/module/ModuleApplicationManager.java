package edu.teco.dnd.module;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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
import edu.teco.dnd.util.IndexedThreadFactory;

public class ModuleApplicationManager {
	private static final Logger LOGGER = LogManager.getLogger(ModuleApplicationManager.class);

	public final UUID localeModuleId;
	public final ConfigReader moduleConfig;
	public final int maxAllowedThreadsPerApp;
	private final Map<UUID, Application> runningApps = new ConcurrentHashMap<UUID, Application>();
	private final ConnectionManager connMan;
	private final Runnable moduleShutdownHook;
	private final ReadWriteLock isShuttingDown = new ReentrantReadWriteLock();
	private final Map<UUID, Integer> spotOccupiedByBlock = new ConcurrentHashMap<UUID, Integer>();

	public ModuleApplicationManager(ConfigReader moduleConfig, ConnectionManager connMan, Runnable modShutdownHook) {
		this.moduleShutdownHook = modShutdownHook;
		this.moduleConfig = moduleConfig;
		this.localeModuleId = moduleConfig.getUuid();
		this.maxAllowedThreadsPerApp = moduleConfig.getMaxThreadsPerApp();
		this.connMan = connMan;
	}

	public ModuleApplicationManager(ConfigReader moduleConfig, ConnectionManager connMan) {
		this(moduleConfig, connMan, null);
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
			synchronized (runningApps) {
				if (runningApps.containsKey(appId)) {
					LOGGER.info("trying to rejoin app that was already joined before.");
					return;
				}

				final ApplicationClassLoader classLoader = new ApplicationClassLoader(connMan, appId);

				final ThreadFactory fact = new ThreadFactory() {
					private final ThreadFactory internalFactory = new IndexedThreadFactory("app-" + appId.toString()
							+ "-");

					@Override
					public Thread newThread(Runnable r) {
						final Thread appThread = internalFactory.newThread(r);
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
			}
		} finally {
			isShuttingDown.readLock().unlock();
		}

	}

	/**
	 * Schedules a FunctionBlock to be started, when StartApp() is called.
	 * 
	 * @param insecureBlock
	 *            the FunctionBlock without a security wrapper.
	 * @param scheduleToId
	 *            the ID of the free blockPosition in the allowed block config. If not supplied (< 0) a random one is
	 *            taken. Be aware that not giving this ID might lead to an otherwise schedulable combination of blocks
	 *            being disallowed as resources might be depleted in an unfavorable order.
	 * @throws UserSuppliedCodeException
	 */
	public void scheduleBlock(UUID appId, FunctionBlock insecureBlock, int scheduleToId)
			throws UserSuppliedCodeException {
		FunctionBlock block;
		if (insecureBlock == null) {
			LOGGER.warn("send block message with NULL block.");
			throw new NullPointerException("send block message with NULL block.");
		}
		try {
			block = new FunctionBlockSecurityDecorator(insecureBlock);
		} catch (UserSuppliedCodeException e) {
			LOGGER.warn("Not scheduling block {} ({}), because of error in block code.", insecureBlock.getBlockName(),
					insecureBlock.getID());
			throw new UserSuppliedCodeException("Not scheduling block, because of error in block-code.");
		}

		Application app = runningApps.get(appId);
		if (app == null) {
			LOGGER.warn("No application {} running on this module.", appId);
			throw new IllegalArgumentException("App must be started before blocks can be scheduled on it.");
		}
		String blockType = block.getType();
		isShuttingDown.readLock().lock();
		try {
			BlockTypeHolder blockAllowed;
			if (scheduleToId < 0) {
				blockAllowed = moduleConfig.getAllowedBlocks().get(blockType);
				if (blockAllowed == null) {
					LOGGER.info("Block {} not allowed in App {}({})", blockType, app, appId);
					throw new IllegalArgumentException("Block not allowed in App");
				}
			} else {
				blockAllowed = moduleConfig.getAllowedBlocksById().get(scheduleToId);
				if (blockAllowed == null) {
					LOGGER.info("Id {} does not exist in App {}({})", scheduleToId, app, appId);
					throw new IllegalArgumentException("Id does not exist in App");
				}
			}

			if (!blockAllowed.type.equals(blockType)) {
				LOGGER.warn("given scheduleId ({}:{}) and Blocktype {} incompatible", scheduleToId, blockAllowed.type,
						blockType);
				throw new IllegalArgumentException("given scheduleId and Blocktype incompatible");
			}

			synchronized (app.scheduledToStart) {
				if (app.isRunning == true) {
					LOGGER.info("tried to schedule Block to already running application.");
					throw new IllegalArgumentException("tried to schedule Block to already running application.");
				}
				if (!blockAllowed.tryDecrease()) {
					LOGGER.info("Blockamount of {} exceeded. Not scheduling! (ID was:{})", blockType, scheduleToId);
					throw new IllegalArgumentException("Blockamount exceeded. Not scheduling!");
				}
				spotOccupiedByBlock.put(block.getID(), blockAllowed.getIdNumber());
				app.scheduledToStart.add(block);
			}
			LOGGER.info("succesfully scheduled block {}, in App {}({})", block, app, appId);

		} finally {
			isShuttingDown.readLock().unlock();
		}

	}

	public void startApp(UUID appId) {
		Application app = runningApps.get(appId);
		if (app == null) {
			LOGGER.warn("Tried to start non existing app: {}", appId);
			throw new IllegalArgumentException("tried to start app that does not exist.");
		}

		isShuttingDown.readLock().lock();
		try {
			synchronized (app.scheduledToStart) {
				if (app.isRunning == true) {
					LOGGER.warn("tried to double start Application.");
					throw new IllegalArgumentException("tried to double start Application.");
				}
				app.isRunning = true;

				for (FunctionBlock func : app.scheduledToStart) {
					app.startBlock(func);
				}
			}
		} finally {
			isShuttingDown.readLock().unlock();
		}

	}

	/**
	 * triggers a shutdown of all Applications because the module is being shutdown.
	 */
	public void shutdownModule() {
		isShuttingDown.writeLock().lock();
		try {
			if (moduleShutdownHook != null) {
				moduleShutdownHook.run();
			}
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
		Collection<FunctionBlock> blocksKilled;
		synchronized (runningApps) {

			Application app = runningApps.get(appId);
			if (app == null) {
				return;
			}

			blocksKilled = app.getAllBlocks();

			app.shutdown();
			runningApps.remove(appId);

		}
		for (FunctionBlock block : blocksKilled) {

			BlockTypeHolder holder = moduleConfig.getAllowedBlocksById().get(spotOccupiedByBlock.get(block.getID()));
			if (holder != null) {
				holder.increase();
			} else {
				LOGGER.warn("Block returned bogous blocktype on shutdown. Can not free resources.");
			}
			spotOccupiedByBlock.remove(block.getID());
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
