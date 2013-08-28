package edu.teco.dnd.module;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.blocks.ValueDestination;
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

				final Application newApp = createApplication(appId, name);
				runningApps.put(appId, newApp);

				registerMessageHandlers(newApp);
			}
		} finally {
			isShuttingDown.readLock().unlock();
		}
	}

	private Application createApplication(final UUID appId, final String name) {
		final ApplicationClassLoader classLoader = createApplicationClassLoader(appId);
		final ScheduledThreadPoolExecutor executor = createApplicationExecutor(appId);
		return new Application(appId, name, executor, connMan, classLoader);
	}

	private ApplicationClassLoader createApplicationClassLoader(final UUID appId) {
		return new ApplicationClassLoader(connMan, appId);
	}

	private ScheduledThreadPoolExecutor createApplicationExecutor(final UUID appId) {
		return new ScheduledThreadPoolExecutor(maxAllowedThreadsPerApp, createApplicationThreadFactory(appId));
	}

	private ThreadFactory createApplicationThreadFactory(final UUID appId) {
		final ApplicationClassLoader classLoader = createApplicationClassLoader(appId);
		return new ThreadFactory() {
			private final ThreadFactory internalFactory = new IndexedThreadFactory("app-" + appId.toString() + "-");

			@Override
			public Thread newThread(Runnable r) {
				final Thread appThread = internalFactory.newThread(r);
				appThread.setContextClassLoader(classLoader); // prevent circumvention of our classLoader.
				return appThread;
			}
		};
	}

	private void registerMessageHandlers(final Application application) {
		final UUID appId = application.getOwnAppId();
		final Executor executor = application.getThreadPool();
		connMan.addHandler(appId, LoadClassMessage.class, new LoadClassMessageHandler(application), executor);
		connMan.addHandler(appId, BlockMessage.class, new BlockMessageHandler(this), executor);
		connMan.addHandler(appId, StartApplicationMessage.class, new StartApplicationMessageHandler(this), executor);
		connMan.addHandler(appId, KillAppMessage.class, new KillAppMessageHandler(this), executor);
		connMan.addHandler(appId, ValueMessage.class, new ValueMessageHandler(application), executor);
		connMan.addHandler(appId, WhoHasBlockMessage.class, new WhoHasFuncBlockHandler(application, localeModuleId));
	}

	/**
	 * Schedules a FunctionBlock to be started, when StartApp() is called.
	 * 
	 * @param blockClass
	 *            the class of the FunctionBlock
	 * @param blockUUID
	 *            the UUID of the FunctionBlock
	 * @param options
	 *            the options for the FunctionBlock
	 */
	public boolean scheduleBlock(UUID appId, final String blockClass, final UUID blockUUID,
			final Map<String, String> options, final Map<String, Collection<ValueDestination>> outputs, int scheduleToId) {
		isShuttingDown.readLock().lock();
		try {
			final Application app = runningApps.get(appId);
			Class<?> cls;
			try {
				cls = app.loadClass(blockClass);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
			if (!FunctionBlock.class.isAssignableFrom(cls)) {
				return false;
			}
			@SuppressWarnings("unchecked")
			final FunctionBlockSecurityDecorator block =
					FunctionBlockSecurityDecorator.getDecorator((Class<? extends FunctionBlock>) cls);
			if (block == null) {
				return false;
			}
			try {
				block.doInit(blockUUID);
			} catch (final IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			}
			final String blockType = block.getBlockType();
			BlockTypeHolder blockAllowed = null;
			blockAllowed = moduleConfig.getAllowedBlocksById().get(scheduleToId);
			if (blockAllowed == null) {
				LOGGER.info("Id {} does not exist in App {}({})", scheduleToId, app, appId);
				throw new IllegalArgumentException("Id does not exist in App");
			}

			if (!blockType.matches(blockAllowed.getType())) {
				LOGGER.warn("given scheduleId ({}:{}) and Blocktype {} incompatible", scheduleToId,
						blockAllowed.getType(), blockType);
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
				spotOccupiedByBlock.put(block.getBlockUUID(), blockAllowed.getIdNumber());

				app.scheduledToStart.add(block);
			}
			final Map<String, Output<? extends Serializable>> blockOutputs = block.getOutputs();
			System.out.println(blockOutputs);
			for (final Entry<String, Collection<ValueDestination>> output : outputs.entrySet()) {
				System.out.println(output.getKey());
				System.out.println(output.getValue());
				final Output<? extends Serializable> out = blockOutputs.get(output.getKey());
				if (out == null) {
					if (LOGGER.isWarnEnabled()) {
						LOGGER.warn("did not find Output {}", output.getKey());
					}
					continue;
				}
				out.setTarget(app.new ApplicationOutputTarget(output.getValue()));
			}
			spotOccupiedByBlock.put(block.getBlockUUID(), blockAllowed.getIdNumber());
			app.scheduledToStart.add(block);
			LOGGER.info("succesfully scheduled block {}, in App {}({})", block.getBlockType(), app, appId);
			return true;
		} finally {
			isShuttingDown.readLock().unlock();
		}

	}

	// FIXME: Apps can be started after shutdownModule has been called
	public void startApp(UUID appId) {
		Application app = runningApps.get(appId);
		if (app == null) {
			LOGGER.warn("Tried to start non existing app: {}", appId);
			throw new IllegalArgumentException("tried to start app that does not exist.");
		}

		isShuttingDown.readLock().lock();
		try {
			app.start();
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
		// TODO deregister Message handlers
		LOGGER.entry(appId);
		Collection<FunctionBlockSecurityDecorator> blocksKilled;
		synchronized (runningApps) {
			Application app = runningApps.get(appId);
			if (app == null) {
				return;
			}

			app.shutdown();

			blocksKilled = app.getAllBlocks();

			runningApps.remove(appId);
		}
		removeBlocks(blocksKilled);
	}

	private void removeBlocks(final Collection<FunctionBlockSecurityDecorator> blocks) {
		for (final FunctionBlockSecurityDecorator block : blocks) {
			removeBlock(block);
		}
	}

	private void removeBlock(final FunctionBlockSecurityDecorator block) {
		final UUID blockUUID = block.getBlockUUID();
		BlockTypeHolder holder = moduleConfig.getAllowedBlocksById().get(spotOccupiedByBlock.get(blockUUID));
		if (holder != null) {
			holder.increase();
		} else {
			LOGGER.warn("Block returned bogous blocktype on shutdown. Can not free resources.");
		}
		spotOccupiedByBlock.remove(blockUUID);
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
