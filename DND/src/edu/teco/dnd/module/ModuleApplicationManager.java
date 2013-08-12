package edu.teco.dnd.module;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
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
	private final Map<UUID, FunctionBlock> scheduledToStart = new HashMap<UUID, FunctionBlock>();
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
	 * @param blockClass the class of the FunctionBlock
	 * @param blockUUID the UUID of the FunctionBlock
	 * @param options the options for the FunctionBlock
	 */
	public boolean scheduleBlock(UUID appId, final String blockClass, final UUID blockUUID, final Map<String, String> options) {
		isShuttingDown.readLock().lock();
		try {
			String blockType = "";
			// TODO: implement block types
//			try {
//				blockType = BlockRunner.getBlockType(block);
//			} catch (UserSuppliedCodeException e) {
//				e.printStackTrace();
//				return false; // not scheduling bad block.
//			}
			final Application application = runningApps.get(appId);
			Class<?> cls;
			try {
				cls = application.getClassLoader().loadClass(blockClass);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			FunctionBlock block;
			try {
				block = (FunctionBlock) cls.getConstructor().newInstance();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			} catch (SecurityException e) {
				e.printStackTrace();
				return false;
			} catch (InstantiationException e) {
				e.printStackTrace();
				return false;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return false;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return false;
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				return false;
			}
			BlockTypeHolder blockAllowed = moduleConfig.getAllowedBlocks().get(blockType);
			if (blockAllowed == null) {
				LOGGER.info("Block {} not allowed in App {}({})", blockType, runningApps.get(appId), appId);
				return false;
			}
			if (!blockAllowed.tryDecrease()) {
				LOGGER.info("Blockamount of {} exceeded. Not scheduling!", blockType);
				return false;
			}
			scheduledToStart.put(blockUUID, block);
			LOGGER.info("succesfully scheduled block {}, in App {}({})", block, runningApps.get(appId), appId);
			return true;
		} finally {
			isShuttingDown.readLock().unlock();
		}
	}

	public void startApp(UUID appId) {
		// TODO: add synchronization
		// TODO: clear list of scheduled blocks
		// TODO: only start the blocks that are scheduled for this application
		for (Entry<UUID, FunctionBlock> block : scheduledToStart.entrySet()) {
			Application app = runningApps.get(appId);
			if (app == null) {
				LOGGER.warn("Tried to start non existing app: {}", appId);
				throw new IllegalArgumentException("tried to start app that does not exist.");
			} else {
				runningApps.get(appId).startBlock(block.getKey(), block.getValue());
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
	public boolean startBlock(UUID appId, final String blockClass, final UUID blockUUID, final Map<String, String> options) {
		String blockType = "";
		// TODO: implement block types
		// TODO: maybe merge some code with scheduleBlock, there seems to be a lot of redundancy
//		try {
//			blockType = BlockRunner.getBlockType(block);
//		} catch (UserSuppliedCodeException e) {
//			e.printStackTrace();
//			return false; // not scheduling bad block.
//		}
		BlockTypeHolder blockAllowed = moduleConfig.getAllowedBlocks().get(blockType);
		if (blockAllowed == null) {
			// do not log the block directly, as that would call block.toString which is untrusted code
			if (LOGGER.isInfoEnabled()) {
				// TODO: FunctionBlocks no longer store their own id. Add them back to the class or store them somewhere else
//				LOGGER.info("Block {} not allowed in App {}({})", block.getID(), runningApps.get(appId), appId);
				LOGGER.info("Block {} not allowed in App {}({})", blockUUID, runningApps.get(appId), appId);
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
			String blockType = "";
			// TODO: implement block types
//			try {
//				blockType = BlockRunner.getBlockType(block);
//			} catch (UserSuppliedCodeException e) {
//				e.printStackTrace();
//				blockType = "";
//				// FIXME: If block return a different value during start and shutdown, they can change the maximum
//				// amount of blocks allowed to run of the latter type.
//			}
			BlockTypeHolder holder = moduleConfig.getAllowedBlocks().get(blockType);
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
