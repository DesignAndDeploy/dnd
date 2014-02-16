package edu.teco.dnd.module;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.Level;
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
import edu.teco.dnd.util.HashStorage;
import edu.teco.dnd.util.IndexedThreadFactory;
import edu.teco.dnd.util.MessageDigestHashAlgorithm;

/**
 * Provides a high level view of a Module.
 */
public class Module {
	private static final Logger LOGGER = LogManager.getLogger(Module.class);

	/**
	 * This algorithm will be used to store byte code globally.
	 */
	public static final String BYTE_CODE_HASH_ALGORITHM = "SHA-256";

	private final ConfigReader moduleConfig;
	private final ConnectionManager connMan;

	private final HashStorage<byte[]> byteCodeStorage;
	private final Map<ApplicationID, Application> runningApps = new HashMap<ApplicationID, Application>();
	private final ModuleBlockManager moduleBlockManager;

	private boolean isShuttingDown = false;
	private final ReadWriteLock shutdownLock = new ReentrantReadWriteLock();
	private final Runnable moduleShutdownHook;

	/**
	 * 
	 * @param config
	 *            Configuration this module has been given.
	 * @param connMan
	 *            the Manager for connections to other modules.
	 * @param shutdownHook
	 *            A runnable that will be executed upon receipt of a KillMeassage before the applications are killed.
	 *            Can be null if it is not needed.
	 * @throws NoSuchAlgorithmException
	 *             if the algorithm that is used for the byte code HashStorage. See {@link #BYTE_CODE_HASH_ALGORITHM}.
	 */
	public Module(ConfigReader config, ConnectionManager connMan, Runnable shutdownHook)
			throws NoSuchAlgorithmException {
		this.byteCodeStorage = new HashStorage<byte[]>(new MessageDigestHashAlgorithm(BYTE_CODE_HASH_ALGORITHM));
		this.moduleShutdownHook = shutdownHook;
		this.moduleConfig = config;
		this.connMan = connMan;
		this.moduleBlockManager = new ModuleBlockManager(this.moduleConfig.getAllowedBlocksById());
	}

	/**
	 * called when a new application is supposed to be started.
	 * 
	 * @param applicationID
	 *            the Id of the app to be started.
	 * @param deployingAgentId
	 *            the agent requesting the start of this application.
	 * @param name
	 *            (human readable) name of the application
	 * @throws IllegalArgumentException
	 */
	public void createNewApplication(final ApplicationID applicationID, String name) {
		LOGGER.info("joining app {} ({})", name, applicationID);

		shutdownLock.readLock().lock();
		try {
			if (isShuttingDown) {
				return;
			}
			synchronized (runningApps) {
				if (runningApps.containsKey(applicationID)) {
					throw LOGGER.throwing(Level.INFO, new IllegalArgumentException("Application with " + applicationID
							+ " already exists"));
				}

				final Application newApplication = instantiateApplication(applicationID, name);
				runningApps.put(applicationID, newApplication);

				registerMessageHandlers(newApplication);
			}
		} finally {
			shutdownLock.readLock().unlock();
		}
	}

	/**
	 * create a new Application with given ID and name.
	 * 
	 * @param applicationID
	 *            the ID of the application to create
	 * @param name
	 *            Human readable name of the application
	 * @return A reference to the new application object
	 */
	private Application instantiateApplication(final ApplicationID applicationID, final String name) {
		final IndexedThreadFactory threadFactory = new IndexedThreadFactory("app-" + applicationID.getUUID() + "-");
		return new Application(applicationID, name, connMan, threadFactory, moduleConfig.getMaxThreadsPerApp(),
				moduleBlockManager, byteCodeStorage);
	}

	/**
	 * registers the Message handlers for a new application.
	 * 
	 * @param application
	 *            the application the message handlers are to be registered for.
	 */
	private void registerMessageHandlers(final Application application) {
		final ApplicationID applicationID = application.getApplicationID();
		final Executor executor = application.getThreadPool();
		connMan.addHandler(applicationID, LoadClassMessage.class, new LoadClassMessageHandler(application), executor);
		connMan.addHandler(applicationID, BlockMessage.class, new BlockMessageHandler(this), executor);
		connMan.addHandler(applicationID, StartApplicationMessage.class, new StartApplicationMessageHandler(this),
				executor);
		connMan.addHandler(applicationID, KillAppMessage.class, new KillAppMessageHandler(this), executor);
		connMan.addHandler(applicationID, ValueMessage.class, new ValueMessageHandler(application), executor);
		connMan.addHandler(applicationID, WhoHasBlockMessage.class, new WhoHasFuncBlockHandler(application,
				moduleConfig.getModuleID()));
	}

	/**
	 * Schedules a FunctionBlock to be started, when StartApp() is called.
	 * 
	 * @param applicationID
	 *            the ID of the application this block is to be scheduled on.
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
	public void scheduleBlock(ApplicationID applicationID, final BlockDescription blockDescription)
			throws ClassNotFoundException, UserSuppliedCodeException, IllegalArgumentException,
			BlockTypeHolderFullException, NoSuchBlockTypeHolderException {
		shutdownLock.readLock().lock();
		try {
			if (isShuttingDown) {
				return;
			}
			Application app;
			synchronized (runningApps) {
				app = runningApps.get(applicationID);
			}
			if (app == null) {
				throw new IllegalArgumentException("tried to schedule block " + blockDescription.getBlockID()
						+ " for non-existant Application " + applicationID);
			}
			app.scheduleBlock(blockDescription);
		} finally {
			shutdownLock.readLock().unlock();
		}

	}

	/**
	 * called when an app that was scheduled to start before is started. Then does what it says.
	 * 
	 * @param applicationID
	 *            the ID of the app to be started.
	 */
	public void startApp(ApplicationID applicationID) {
		shutdownLock.readLock().lock();
		try {
			if (isShuttingDown) {
				return;
			}
			Application app;
			synchronized (runningApps) {
				app = runningApps.get(applicationID);
			}
			if (app == null) {
				LOGGER.warn("Tried to start non existing app: {}", applicationID);
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
			synchronized (runningApps) {
				for (ApplicationID applicationID : runningApps.keySet()) {
					stopApplication(applicationID);
				}
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
	public void stopApplication(ApplicationID appId) {
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

			for (final FunctionBlockSecurityDecorator block : app.getFunctionBlocksById().values()) {
				moduleBlockManager.removeBlock(new ApplicationBlockID(block.getBlockID(), appId));
			}

			runningApps.remove(appId);
		}
		LOGGER.exit();
	}

	/**
	 * @return the runningApps
	 */
	public Map<ApplicationID, Application> getRunningApps() {
		final Map<ApplicationID, Application> result = new HashMap<ApplicationID, Application>();
		synchronized (runningApps) {
			result.putAll(runningApps);
		}
		return result;
	}

	public Application getApplication(final ApplicationID applicationID) {
		synchronized (runningApps) {
			return runningApps.get(applicationID);
		}
	}
}
