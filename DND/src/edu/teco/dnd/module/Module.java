package edu.teco.dnd.module;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.ModuleBlockManager.BlockTypeHolderFullException;
import edu.teco.dnd.module.ModuleBlockManager.NoSuchBlockTypeHolderException;
import edu.teco.dnd.module.config.ModuleConfig;
import edu.teco.dnd.module.config.BlockTypeHolder;
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
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Message;
import edu.teco.dnd.util.HashStorage;
import edu.teco.dnd.util.IndexedThreadFactory;
import edu.teco.dnd.util.MessageDigestHashAlgorithm;

/**
 * An entity that can execute {@link FunctionBlock}s belonging to an {@link Application}.
 */
public class Module {
	private static final Logger LOGGER = LogManager.getLogger(Module.class);

	/**
	 * This algorithm will be used to store byte code globally.
	 */
	public static final String BYTE_CODE_HASH_ALGORITHM = "SHA-256";

	private final ModuleConfig moduleConfig;
	private final ConnectionManager connectionManager;

	private final HashStorage<byte[]> byteCodeStorage;
	private final Map<ApplicationID, Application> runningApps = new HashMap<ApplicationID, Application>();
	private final ModuleBlockManager moduleBlockManager;

	private boolean isShuttingDown = false;
	private final ReadWriteLock shutdownLock = new ReentrantReadWriteLock();
	private final Runnable moduleShutdownHook;

	/**
	 * Initializes a new Module. This includes creating a {@link HashStorage} for storing byte code and creating a
	 * {@link ModuleBlockManager}.
	 * 
	 * @param config
	 *            a configuration for this Module
	 * @param connectionManager
	 *            a ConnectionManager that will be used to send {@link Message}s to other Modules
	 * @param shutdownHook
	 *            will be executed when the Module is shut down. Can be <code>null</code>.
	 * @throws NoSuchAlgorithmException
	 *             if the algorithm that is used for the byte code HashStorage. See {@link #BYTE_CODE_HASH_ALGORITHM}.
	 * @see #BYTE_CODE_HASH_ALGORITHM
	 */
	public Module(ModuleConfig config, ConnectionManager connectionManager, Runnable shutdownHook)
			throws NoSuchAlgorithmException {
		this.byteCodeStorage = new HashStorage<byte[]>(new MessageDigestHashAlgorithm(BYTE_CODE_HASH_ALGORITHM));
		this.moduleShutdownHook = shutdownHook;
		this.moduleConfig = config;
		this.connectionManager = connectionManager;
		this.moduleBlockManager = new ModuleBlockManager(this.moduleConfig.getBlockRoot());
	}

	/**
	 * Creates a new Application. If the ApplicationID is already in use an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param applicationID
	 *            an ID for the new Application. Must not be in use or an IllegalArgumentException will be thrown
	 * @param name
	 *            a name for the Application. Not used directly, only displayed for the user’s convenience
	 * @throws IllegalArgumentException
	 *             if <code>applicationID</code> is already in use
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
	 * Instantiates {@link Application}. A new {@link ThreadFactory} is created that so that the ApplicationID is part
	 * of the Application’s Thread’s names.
	 * 
	 * @param applicationID
	 *            the ID of the new Application
	 * @param name
	 *            the name of the Application
	 */
	private Application instantiateApplication(final ApplicationID applicationID, final String name) {
		final IndexedThreadFactory threadFactory = new IndexedThreadFactory("app-" + applicationID.getUUID() + "-");
		return new Application(applicationID, name, connectionManager, threadFactory,
				moduleConfig.getMaxThreadsPerApp(), moduleBlockManager, byteCodeStorage);
	}

	/**
	 * Registers the {@link MessageHandler}s that will be used by the Application. This is done in Module as some of the
	 * MessageHandlers call methods in Module instead of Application directly so that a shutdown can be properly
	 * synchronized.
	 * 
	 * @param application
	 *            the Application the MessageHandlers should be registered for
	 */
	private void registerMessageHandlers(final Application application) {
		final ApplicationID applicationID = application.getApplicationID();
		final Executor executor = application.getThreadPool();
		connectionManager.addHandler(applicationID, LoadClassMessage.class, new LoadClassMessageHandler(application),
				executor);
		connectionManager.addHandler(applicationID, BlockMessage.class, new BlockMessageHandler(this), executor);
		connectionManager.addHandler(applicationID, StartApplicationMessage.class, new StartApplicationMessageHandler(
				this), executor);
		connectionManager.addHandler(applicationID, KillAppMessage.class, new KillAppMessageHandler(this), executor);
		connectionManager.addHandler(applicationID, ValueMessage.class, new ValueMessageHandler(application), executor);
		connectionManager.addHandler(applicationID, WhoHasBlockMessage.class, new WhoHasFuncBlockHandler(application,
				moduleConfig.getModuleID()));
	}

	/**
	 * Adds a {@link FunctionBlock} to an {@link Application}. If the Application is already running the FunctionBlock
	 * will be initialized and executed immediately, if not it will be stored until {@link #startApp(ApplicationID)} is
	 * called for the Application.
	 * 
	 * @param applicationID
	 *            the ID of the Application the FunctionBlock should be added to
	 * @param blockDescription
	 *            a description of the Block that should be added
	 * @throws ClassNotFoundException
	 *             if the class given in <code>blockDescription</code> could not be found
	 * @throws UserSuppliedCodeException
	 *             if the FunctionBlock throws any {@link Throwable}
	 * @throws IllegalArgumentException
	 *             if there is no Application with the given ID or the <code>blockDescription</code> is invalid
	 * @throws BlockTypeHolderFullException
	 *             if the FunctionBlock could not be added because the {@link BlockTypeHolder} given in the
	 *             <code>blockDescription</code> has no free space
	 * @throws NoSuchBlockTypeHolderException
	 *             if the {@link BlockTypeHolder} given in the <code>blockDescription</code> does not exist
	 * @see Application#scheduleBlock(BlockDescription)
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
	 * Starts an Application. The Application has to be created with
	 * {@link #createNewApplication(ApplicationID, String)} beforehand and must not have been started already.
	 * 
	 * @param applicationID
	 *            the ID of the link Application to start
	 * @throws IllegalArgumentException
	 *             if there is no Application with given ID or if the Application has already been started
	 * @see Application#start()
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
	 * Shuts down the Module. This will {@link #stopApplication(ApplicationID) stop} all Applications and prevent the
	 * Module from accepting any new Applications.
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
	 * Stops a single Application. If the {@link Application} is running it is shutdown, all used
	 * {@link BlockTypeHolder}s are freed and the {@link ApplicationID} is freed so a new Application with that ID can
	 * be {@link #startApp(ApplicationID) started}.
	 * 
	 * @param applicationID
	 *            the ID of the Application that should be stopped
	 */
	public void stopApplication(ApplicationID applicationID) {
		// TODO deregister Message handlers
		LOGGER.entry(applicationID);
		synchronized (runningApps) {
			Application app = runningApps.get(applicationID);
			if (app == null) {
				throw LOGGER.throwing(new IllegalArgumentException("Tried to stop non-existant Application "
						+ applicationID));
			}

			try {
				app.shutdown();
			} catch (final IllegalStateException e) {
				LOGGER.catching(Level.DEBUG, e);
				// Application had not been started, ignoring
			}

			for (final FunctionBlockSecurityDecorator block : app.getFunctionBlocksById().values()) {
				moduleBlockManager.removeBlock(new ApplicationBlockID(block.getBlockID(), applicationID));
			}

			runningApps.remove(applicationID);
		}
		LOGGER.exit();
	}

	/**
	 * Returns all Applications. This includes all created Applications (running and those that have not been started
	 * yet).
	 */
	public Map<ApplicationID, Application> getApplications() {
		final Map<ApplicationID, Application> result = new HashMap<ApplicationID, Application>();
		synchronized (runningApps) {
			result.putAll(runningApps);
		}
		return result;
	}

	/**
	 * Returns the {@link Application} with the given ID.
	 * 
	 * @param applicationID
	 *            the ID to look up
	 * @return the matching Application or null if no Application with this ID is found
	 */
	public Application getApplication(final ApplicationID applicationID) {
		synchronized (runningApps) {
			return runningApps.get(applicationID);
		}
	}
}
