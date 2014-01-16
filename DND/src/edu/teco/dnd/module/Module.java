package edu.teco.dnd.module;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.ModuleBlockManager.BlockTypeHolderFullException;
import edu.teco.dnd.module.ModuleBlockManager.NoSuchBlockTypeHolderException;
import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.module.messages.infoReq.ApplicationBlockID;
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

/**
 * Provides a high level view of a Module.
 */
public class Module {
	private static final Logger LOGGER = LogManager.getLogger(Module.class);

	private final ConfigReader moduleConfig;
	private final Map<UUID, Application> runningApps = new ConcurrentHashMap<UUID, Application>();
	private final ConnectionManager connMan;
	private final Runnable moduleShutdownHook;
	private boolean isShuttingDown = false;
	private final ReadWriteLock shutdownLock = new ReentrantReadWriteLock();
	private final ModuleBlockManager moduleBlockManager;

	/**
	 * 
	 * @param config
	 *            Configuration this module has been given.
	 * @param connMan
	 *            the Manager for connections to other modules.
	 * @param shutdownHook
	 *            A runnable that will be executed upon receipt of a KillMeassage before the applications are killed.
	 *            Can be null if it is not needed.
	 */
	public Module(ConfigReader config, ConnectionManager connMan, Runnable shutdownHook) {
		this.moduleShutdownHook = shutdownHook;
		this.moduleConfig = config;
		this.connMan = connMan;
		this.moduleBlockManager = new ModuleBlockManager(this.moduleConfig.getAllowedBlocksById());
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

		shutdownLock.readLock().lock();
		try {
			if (isShuttingDown) {
				return;
			}
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
			shutdownLock.readLock().unlock();
		}
	}

	/**
	 * create a new Application with given UUID and name.
	 * 
	 * @param appId
	 *            the uuid of the application to create
	 * @param name
	 *            Human readable name of the application
	 * @return A reference to the new application object
	 */
	private Application createApplication(final UUID appId, final String name) {
		final ApplicationClassLoader classLoader = new ApplicationClassLoader(connMan, appId);
		final ScheduledThreadPoolExecutor executor =
				new ScheduledThreadPoolExecutor(moduleConfig.getMaxThreadsPerApp(), createApplicationThreadFactory(
						appId, classLoader));
		return new Application(appId, name, executor, connMan, classLoader, moduleBlockManager);
	}

	/**
	 * create a new Thread factory.
	 * 
	 * @param appId
	 *            the application UUID, used for naming of threads.
	 * @param classLoader
	 *            classLoader set as contextClassLoader of the created threads. It is STRONGLY advised this be the same
	 *            as the classLoader used to load the Application class.
	 * @return a thread Factory that can be used for applications.
	 */
	private ThreadFactory createApplicationThreadFactory(final UUID appId, final ApplicationClassLoader classLoader) {
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

	/**
	 * registers the Message handlers for a new application.
	 * 
	 * @param application
	 *            the application the message handlers are to be registered for.
	 */
	private void registerMessageHandlers(final Application application) {
		final UUID appId = application.getOwnAppId();
		final Executor executor = application.getThreadPool();
		connMan.addHandler(appId, LoadClassMessage.class, new LoadClassMessageHandler(application), executor);
		connMan.addHandler(appId, BlockMessage.class, new BlockMessageHandler(this), executor);
		connMan.addHandler(appId, StartApplicationMessage.class, new StartApplicationMessageHandler(this), executor);
		connMan.addHandler(appId, KillAppMessage.class, new KillAppMessageHandler(this), executor);
		connMan.addHandler(appId, ValueMessage.class, new ValueMessageHandler(application), executor);
		connMan.addHandler(appId, WhoHasBlockMessage.class,
				new WhoHasFuncBlockHandler(application, moduleConfig.getUuid()));
	}

	/**
	 * Schedules a FunctionBlock to be started, when StartApp() is called.
	 * 
	 * @param appId
	 *            the UUID of the application this block is to be scheduled on.
	 * @param blockDescription
	 *            the information about the block that schould be scheduled.
	 * @throws UserSuppliedCodeException
	 *             if the BlockDescriptor contains a block with invalid code.
	 * @throws ClassNotFoundException
	 *             if the Class described in blockDescription can not be loaded by the application class loader.
	 * @throws IllegalArgumentException
	 *             if the application does not exist or scheduleBlock threw one.
	 * @throws NoSuchBlockTypeHolderException
	 * @throws BlockTypeHolderFullException
	 */
	public void scheduleBlock(UUID appId, final BlockDescription blockDescription) throws ClassNotFoundException,
			UserSuppliedCodeException, IllegalArgumentException, BlockTypeHolderFullException,
			NoSuchBlockTypeHolderException {
		shutdownLock.readLock().lock();
		try {
			if (isShuttingDown) {
				return;
			}
			final Application app = runningApps.get(appId);
			if (app == null) {
				throw new IllegalArgumentException("tried to schedule block " + blockDescription.blockUUID
						+ " for non-existant Application " + appId);
			}
			app.scheduleBlock(blockDescription);
		} finally {
			shutdownLock.readLock().unlock();
		}

	}

	/**
	 * called when an app that was scheduled to start before is started. Then does what it says.
	 * 
	 * @param appId
	 *            the UUID of the app to be started.
	 */
	public void startApp(UUID appId) {
		shutdownLock.readLock().lock();
		try {
			if (isShuttingDown) {
				return;
			}
			Application app = runningApps.get(appId);
			if (app == null) {
				LOGGER.warn("Tried to start non existing app: {}", appId);
				throw new IllegalArgumentException("tried to start app that does not exist.");
			}
			app.start();
		} finally {
			shutdownLock.readLock().unlock();
		}

	}

	/**
	 * triggers a shutdown of all Applications because the module is being shutdown.
	 */
	public void shutdownModule() {
		shutdownLock.writeLock().lock();
		try {
			if (isShuttingDown) {
				return;
			}
			isShuttingDown = true;
			if (moduleShutdownHook != null) {
				moduleShutdownHook.run();
			}
			for (UUID appId : runningApps.keySet()) {
				stopApplication(appId);
			}
		} finally {
			shutdownLock.writeLock().unlock();
		}
	}

	/**
	 * called to request stopping of a given application.
	 * 
	 * @param appId
	 *            the id of the app to be stopped
	 */
	public void stopApplication(UUID appId) {
		// TODO deregister Message handlers
		LOGGER.entry(appId);
		synchronized (runningApps) {
			Application app = runningApps.get(appId);
			if (app == null) {
				LOGGER.warn("tried to stop nonexistant application with ID {}", appId);
				LOGGER.exit();
				return;
			}

			app.shutdown();

			for (final FunctionBlockSecurityDecorator block : app.getAllBlocks()) {
				moduleBlockManager.removeBlock(new ApplicationBlockID(block.getBlockUUID(), appId));
			}

			runningApps.remove(appId);
		}
		LOGGER.exit();
	}

	/**
	 * @return the runningApps
	 */
	public Map<UUID, Application> getRunningApps() {
		return runningApps;
	}

	public Application getApplication(final UUID applicationID) {
		return runningApps.get(applicationID);
	}

	/**
	 * @param appId
	 *            UUID of the application to get the classloader of.
	 * @return the class loader of an app. null if none.
	 */
	public ClassLoader getAppClassLoader(UUID appId) {
		Application app = runningApps.get(appId);
		return (app == null) ? null : app.getClassLoader();
	}
}
