package edu.teco.dnd.deploy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.FunctionBlockID;
import edu.teco.dnd.blocks.InputDescription;
import edu.teco.dnd.deploy.Distribution.BlockTarget;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.graphiti.model.InputModel;
import edu.teco.dnd.graphiti.model.OptionModel;
import edu.teco.dnd.graphiti.model.OutputModel;
import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.module.ModuleInfo;
import edu.teco.dnd.module.messages.joinStartApp.JoinApplicationAck;
import edu.teco.dnd.module.messages.joinStartApp.JoinApplicationMessage;
import edu.teco.dnd.module.messages.joinStartApp.StartApplicationAck;
import edu.teco.dnd.module.messages.joinStartApp.StartApplicationMessage;
import edu.teco.dnd.module.messages.killApp.KillAppMessage;
import edu.teco.dnd.module.messages.loadStartBlock.BlockAck;
import edu.teco.dnd.module.messages.loadStartBlock.BlockMessage;
import edu.teco.dnd.module.messages.loadStartBlock.BlockNak;
import edu.teco.dnd.module.messages.loadStartBlock.LoadClassAck;
import edu.teco.dnd.module.messages.loadStartBlock.LoadClassMessage;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.util.ClassFile;
import edu.teco.dnd.util.DefaultFutureNotifier;
import edu.teco.dnd.util.Dependencies;
import edu.teco.dnd.util.FileCache;
import edu.teco.dnd.util.FinishedFutureNotifier;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;
import edu.teco.dnd.util.JoinedFutureNotifier;
import edu.teco.dnd.util.MapUtil;

/**
 * This class provides functionality to deploy applications to modules. If any step doing this fails, the (partially
 * deployed) Application is killed so that a consistent state is reached again.
 */
public class Deploy {
	private static final Logger LOGGER = LogManager.getLogger(Deploy.class);

	private final Set<DeployListener> listeners = new HashSet<DeployListener>();
	private final ReadWriteLock listenerLock = new ReentrantReadWriteLock();

	private final ConnectionManager connectionManager;

	private final Map<FunctionBlockModel, BlockTarget> distribution;
	private final String appName;

	/**
	 * The ID of the application.
	 */
	private final ApplicationID applicationID;

	/**
	 * Used to resolve dependencies.
	 */
	private final Dependencies dependencies;
	private Map<ModuleInfo, Set<ClassFile>> neededFilesPerModule;
	private final FileCache fileCache = new FileCache();

	private final DeployFutureNotifier deployFutureNotifier = new DeployFutureNotifier();
	private final AtomicInteger unfinishedModules = new AtomicInteger();
	private final Map<ModuleInfo, Set<FunctionBlockModel>> moduleMap;
	private final AtomicBoolean hasBeenStarted = new AtomicBoolean(false);

	/**
	 * Creates a new Deploy object.
	 * 
	 * @param connectionManager
	 *            the ConnectionManager to use
	 * @param distribution
	 *            the Distribution that should be deployed
	 * @param name
	 *            the name of the Application
	 * @param dependencies
	 *            used to resolve Dependencies
	 * @param applicationID
	 *            the ID of the Application
	 */
	public Deploy(final ConnectionManager connectionManager, final Map<FunctionBlockModel, BlockTarget> distribution,
			final String name, final Dependencies dependencies, final ApplicationID applicationID) {
		LOGGER.entry(connectionManager, distribution, name, dependencies, applicationID);
		this.connectionManager = connectionManager;
		this.distribution = distribution;
		this.appName = name;
		this.dependencies = dependencies;
		this.applicationID = applicationID;
		this.moduleMap = MapUtil.invertMap(getModuleMapping());
		LOGGER.exit();
	}

	/**
	 * Creates a new Deploy object with a random ID.
	 * 
	 * @param connectionManager
	 *            the ConnectionManager to use
	 * @param distribution
	 *            the Distribution that should be deployed
	 * @param name
	 *            the name of the Application
	 * @param dependencies
	 *            used to resolve Dependencies
	 */
	public Deploy(final ConnectionManager connectionManager, final Map<FunctionBlockModel, BlockTarget> distribution,
			final String name, final Dependencies dependencies) {
		this(connectionManager, distribution, name, dependencies, new ApplicationID());
	}

	/**
	 * Adds a listener to this object. The listener will be informed about the deployment process.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addListener(final DeployListener listener) {
		LOGGER.entry(listener);
		listenerLock.writeLock().lock();
		try {
			listeners.add(listener);
		} finally {
			listenerLock.writeLock().unlock();
		}
		LOGGER.exit();
	}

	/**
	 * Removes a listener from this object.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeListener(final DeployListener listener) {
		LOGGER.entry(listener);
		listenerLock.writeLock().lock();
		try {
			listeners.add(listener);
		} finally {
			listenerLock.writeLock().unlock();
		}
		LOGGER.exit();
	}

	/**
	 * Returns the name of the Application.
	 * 
	 * @return the name of the Application
	 */
	public String getName() {
		return this.appName;
	}

	/**
	 * Returns the ID of the Application.
	 * 
	 * @return the ID of the Application
	 */
	public ApplicationID getApplicationID() {
		return this.applicationID;
	}

	/**
	 * Returns the number of Modules that will be used by the Application.
	 * 
	 * @return the number of Modules that will be used by the Application
	 */
	public int getModuleCount() {
		return this.moduleMap.size();
	}

	/**
	 * Returns all Modules used by the application.
	 * 
	 * @return all Modules used by the application
	 */
	public Set<ModuleInfo> getModules() {
		return Collections.unmodifiableSet(this.moduleMap.keySet());
	}

	/**
	 * Returns the IDs of the Modules used by the application.
	 * 
	 * @return the IDs of the Modules used by the application
	 */
	public Collection<ModuleID> getModuleIDs() {
		final Collection<ModuleID> moduleIDs = new ArrayList<ModuleID>(moduleMap.size());
		for (final ModuleInfo module : moduleMap.keySet()) {
			moduleIDs.add(module.getID());
		}
		return moduleIDs;
	}

	/**
	 * Returns a FutureNotifier that represents the state of the deployment process: When the FutureNotifier is finished
	 * the deployment process is finished and the {@link FutureNotifier#isSuccess()} method indicates whether or not the
	 * deployment was successful.
	 * 
	 * @return a FutureNotifier representing the state of the deployment process
	 */
	public FutureNotifier<Void> getDeployFutureNotifier() {
		return deployFutureNotifier;
	}

	/**
	 * Starts the deployment process. If it is called multiple times only the first time will do anything.
	 * 
	 * @return true if this method was called for the first time, false if called again later
	 */
	public boolean deploy() {
		LOGGER.entry();

		if (!hasBeenStarted.compareAndSet(false, true)) {
			LOGGER.warn("tried to start deployment of {} twice");
			LOGGER.exit(false);
			return false;
		}

		unfinishedModules.set(moduleMap.size());

		final Map<FunctionBlockModel, Set<ClassFile>> neededFiles = getNeededFiles();
		neededFilesPerModule = MapUtil.transitiveMapSet(moduleMap, neededFiles);

		for (final ModuleInfo module : moduleMap.keySet()) {
			sendJoin(module.getID()).addListener(new FutureListener<FutureNotifier<Response>>() {
				@Override
				public void operationComplete(final FutureNotifier<Response> future) {
					handleJoinFinished(future, module);
				}
			});
		}
		LOGGER.exit(true);
		return true;
	}

	/**
	 * Creates a mapping from FunctionBlock to ModuleInfo using {@link #distribution}.
	 * 
	 * @return a mapping from FunctionBlock to corresponding ModuleInfo
	 */
	private Map<FunctionBlockModel, ModuleInfo> getModuleMapping() {
		final Map<FunctionBlockModel, ModuleInfo> moduleMapping = new HashMap<FunctionBlockModel, ModuleInfo>();
		for (final Entry<FunctionBlockModel, BlockTarget> entry : distribution.entrySet()) {
			moduleMapping.put(entry.getKey(), entry.getValue().getModule());
		}
		return moduleMapping;
	}

	/**
	 * Returns a mapping that contains all ClassFiles needed for each block in {@link #distribution}.
	 * 
	 * @return a mapping that contains all ClassFiles needed for each block
	 */
	private Map<FunctionBlockModel, Set<ClassFile>> getNeededFiles() {
		final Map<FunctionBlockModel, Set<ClassFile>> neededFiles = new HashMap<FunctionBlockModel, Set<ClassFile>>();
		for (final FunctionBlockModel block : distribution.keySet()) {
			neededFiles.put(block, new HashSet<ClassFile>(dependencies.getDependencies(block.getBlockClass())));
		}
		return neededFiles;
	}

	/**
	 * Sends a {@link JoinApplicationMessage} to the Module with the given ID.
	 * 
	 * @param moduleID
	 *            the ID of the Module the message should be sent to
	 * @return a FutureNotifier that will return the Response of the ModuleInfo
	 */
	private FutureNotifier<Response> sendJoin(final ModuleID moduleID) {
		LOGGER.entry(moduleID);
		final FutureNotifier<Response> futureNotifier =
				connectionManager.sendMessage(moduleID, new JoinApplicationMessage(appName, applicationID));
		LOGGER.exit(futureNotifier);
		return futureNotifier;
	}

	/**
	 * This method is called when a ModuleInfo's Response to a {@link JoinApplicationMessage} is received. If a positive
	 * Response is received {@link #sendClasses(ModuleInfo)} is called, otherwise {@link #deployFutureNotifier} is
	 * marked as failed.
	 * 
	 * @param future
	 *            the FutureNotifier for the Response
	 * @param module
	 *            the ModuleInfo the Response is from
	 */
	private void handleJoinFinished(final FutureNotifier<Response> future, final ModuleInfo module) {
		LOGGER.entry(future, module);
		if (deployFutureNotifier.isDone() && !deployFutureNotifier.isSuccess()) {
			LOGGER.debug("deployFutureNotifier failed, aborting");
			return;
		}

		if (future.isSuccess()) {
			if (!(future.getNow() instanceof JoinApplicationAck)) {
				deployFutureNotifier.setFailure0(null);
				LOGGER.exit();
				return;
			}

			informJoined(module.getID());

			LOGGER.debug("sending classes to {}", module);
			sendClasses(module).addListener(new FutureListener<FutureNotifier<Collection<Response>>>() {
				@Override
				public void operationComplete(final FutureNotifier<Collection<Response>> future) {
					handleClassSendingFinished(future, module);
				}
			});
		} else {
			deployFutureNotifier.setFailure0(future.cause());
		}
		LOGGER.exit();
	}

	/**
	 * Calls {@link DeployListener#moduleJoined(ApplicationID, ModuleID)} on all listeners.
	 * 
	 * @param moduleID
	 *            the ID of the Module that joined
	 */
	private void informJoined(final ModuleID moduleID) {
		listenerLock.readLock().lock();
		try {
			for (final DeployListener listener : listeners) {
				listener.moduleJoined(applicationID, moduleID);
			}
		} finally {
			listenerLock.readLock().unlock();
		}
	}

	/**
	 * Sends all classes needed by the given ModuleInfo to the ModuleInfo.
	 * 
	 * @param module
	 *            the that should be handled
	 * @return a FutureNotifier that will return a Collection of the Responses to all {@link LoadClassMessage}s
	 */
	private FutureNotifier<Collection<Response>> sendClasses(final ModuleInfo module) {
		final ModuleID moduleID = module.getID();
		final Collection<FutureNotifier<? extends Response>> futureNotifiers =
				new ArrayList<FutureNotifier<? extends Response>>();
		for (final ClassFile classFile : neededFilesPerModule.get(module)) {
			futureNotifiers.add(sendClass(moduleID, classFile));
		}
		return new JoinedFutureNotifier<Response>(futureNotifiers);
	}

	/**
	 * Sends a single class to a Module. Used {@link #fileCache} to cache the class file contents.
	 * 
	 * @param moduleID
	 *            the ID of the Module the class should be sent to
	 * @param classFile
	 *            the class to send
	 * @return a FutureNotifier that will return the Response of the Module
	 */
	private FutureNotifier<Response> sendClass(final ModuleID moduleID, final ClassFile classFile) {
		byte[] classData;
		try {
			classData = fileCache.getFileData(classFile.getFile());
		} catch (final IOException e) {
			return new FinishedFutureNotifier<Response>(e);
		}
		LOGGER.trace("sending class {} to module {}", classFile, moduleID);
		return connectionManager.sendMessage(moduleID, new LoadClassMessage(classFile.getClassName(), classData,
				applicationID));
	}

	/**
	 * This method is called when a FutureNotifier for Responses to {@link LoadClassMessage}s has finished. If a
	 * positive Reponse is received {@link #sendBlocks(ModuleInfo)} is called, otherwise {@link #deployFutureNotifier}
	 * is marked as failed.
	 * 
	 * @param future
	 *            the FutureNotifier that returns the Responses
	 * @param module
	 *            the ModuleInfo the Responses are from
	 */
	private void handleClassSendingFinished(final FutureNotifier<Collection<Response>> future, final ModuleInfo module) {
		LOGGER.entry(future, module);
		if (deployFutureNotifier.isDone() && !deployFutureNotifier.isSuccess()) {
			LOGGER.debug("deployFutureNotifier failed, aborting");
			return;
		}

		if (future.isSuccess()) {
			for (final Response response : future.getNow()) {
				if (!(response instanceof LoadClassAck)) {
					deployFutureNotifier.setFailure0(null);
					LOGGER.exit();
					return;
				}
			}

			informClassesLoaded(module.getID());

			LOGGER.debug("sending blocks to {}", module);
			sendBlocks(module).addListener(new FutureListener<FutureNotifier<Collection<Response>>>() {
				@Override
				public void operationComplete(final FutureNotifier<Collection<Response>> future) {
					handleBlockSendingFinished(future, module);
				}
			});
		} else {
			deployFutureNotifier.setFailure0(future.cause());
		}
		LOGGER.exit();
	}

	/**
	 * Calls {@link DeployListener#moduleLoadedClasses(ApplicationID, ModuleID)} on all listeners.
	 * 
	 * @param moduleID
	 *            the ID of the Module that loaded the classes
	 */
	private void informClassesLoaded(final ModuleID moduleID) {
		listenerLock.readLock().lock();
		try {
			for (final DeployListener listener : listeners) {
				listener.moduleLoadedClasses(applicationID, moduleID);
			}
		} finally {
			listenerLock.readLock().unlock();
		}
	}

	/**
	 * Sends all Blocks that are assigned to the given ModuleInfo to the ModuleInfo.
	 * 
	 * @param module
	 *            the ModuleInfo to handle
	 * @return a FutureNotifier returning a Collection of all Responses to the {@link BlockMessage}s
	 */
	private FutureNotifier<Collection<Response>> sendBlocks(final ModuleInfo module) {
		final ModuleID moduleID = module.getID();
		final Collection<FutureNotifier<? extends Response>> futureNotifiers =
				new ArrayList<FutureNotifier<? extends Response>>();
		for (final FunctionBlockModel block : moduleMap.get(module)) {
			LOGGER.debug("sending block {}", block);
			futureNotifiers.add(sendBlock(moduleID, block));
		}
		return new JoinedFutureNotifier<Response>(futureNotifiers);
	}

	/**
	 * Sends a single {@link FunctionBlock} to the Module with the given ID.
	 * 
	 * @param moduleID
	 *            the ID of the ModuleInfo
	 * @param block
	 *            the FunctionBlock to send
	 * @return a FutureNotifier that will return the Response of the Module
	 */
	private FutureNotifier<Response> sendBlock(final ModuleID moduleID, final FunctionBlockModel block) {
		final Map<String, String> options = new HashMap<String, String>();
		for (final OptionModel option : block.getOptions()) {
			options.put(option.getName(), option.getValue());
		}
		final Map<String, Collection<InputDescription>> outputs = new HashMap<String, Collection<InputDescription>>();
		for (final OutputModel output : block.getOutputs()) {
			final Collection<InputDescription> destinations = new ArrayList<InputDescription>();
			for (final InputModel input : output.getInputs()) {
				destinations.add(new InputDescription(new FunctionBlockID(input.getFunctionBlock().getID()), input
						.getName()));
			}
			if (!destinations.isEmpty()) {
				outputs.put(output.getName(), destinations);
			}
		}
		final BlockMessage blockMsg =
				new BlockMessage(applicationID, block.getBlockClass(), block.getBlockName(), new FunctionBlockID(
						block.getID()), options, outputs, distribution.get(block).getBlockTypeHolder().getID());
		return connectionManager.sendMessage(moduleID, blockMsg);
	}

	/**
	 * This method is called when all {@link FunctionBlock} that are assigned to a Module have been sent. If a positive
	 * Response was received {@link #unfinishedModules} is decremented. If it is zero afterwards
	 * {@link #sendStartApplication()} is called. If a negative Response is received {@link #deployFutureNotifier} is
	 * marked as failed.
	 * 
	 * @param future
	 *            the FutureNotifier returning the Responses
	 * @param module
	 *            the ModuleInfo the Responses are from
	 */
	private void handleBlockSendingFinished(final FutureNotifier<Collection<Response>> future, final ModuleInfo module) {
		LOGGER.entry(future, module);
		if (deployFutureNotifier.isDone() && !deployFutureNotifier.isSuccess()) {
			LOGGER.exit();
			return;
		}

		if (future.isSuccess()) {
			for (final Response response : future.getNow()) {
				if (!(response instanceof BlockAck)) {
					if (response instanceof BlockNak) {
						deployFutureNotifier.setFailure0(new BlockNotAcceptedException(((BlockNak) response)
								.getErrorMessage()));
					} else {
						deployFutureNotifier.setFailure0(null);
					}
					LOGGER.exit();
					return;
				}
			}

			informBlocksLoaded(module.getID());

			LOGGER.debug("module {} finished successfully", module);
			if (unfinishedModules.decrementAndGet() <= 0) {
				LOGGER.debug("last module finished, sending StartApplication");
				sendStartApplication().addListener(new FutureListener<FutureNotifier<Collection<Response>>>() {
					@Override
					public void operationComplete(FutureNotifier<Collection<Response>> future) throws Exception {
						handleStartApplicationFinished(future);
					}
				});
			}
		} else {
			deployFutureNotifier.setFailure0(future.cause());
		}
		LOGGER.exit();
	}

	/**
	 * Calls {@link DeployListener#moduleLoadedBlocks(ApplicationID, ModuleID)} on all listeners.
	 * 
	 * @param moduleID
	 *            the ID of the Module that loaded its blocks
	 */
	private void informBlocksLoaded(final ModuleID moduleID) {
		listenerLock.readLock().lock();
		try {
			for (final DeployListener listener : listeners) {
				listener.moduleLoadedBlocks(applicationID, moduleID);
			}
		} finally {
			listenerLock.readLock().unlock();
		}
	}

	/**
	 * Sends a {@link StartApplicationMessage} to all Modules and adds a listener to each future to call
	 * {@link #informModuleStarted(ModuleID)} if the Module started the application successfully.
	 * 
	 * @return a FutureNotifier returning the Responses of all Modules
	 */
	private FutureNotifier<Collection<Response>> sendStartApplication() {
		final Collection<FutureNotifier<? extends Response>> futureNotifiers =
				new ArrayList<FutureNotifier<? extends Response>>();
		for (final ModuleInfo module : moduleMap.keySet()) {
			final ModuleID moduleID = module.getID();
			final FutureNotifier<Response> futureNotifier =
					connectionManager.sendMessage(moduleID, new StartApplicationMessage(applicationID));
			futureNotifier.addListener(new FutureListener<FutureNotifier<Response>>() {
				@Override
				public void operationComplete(final FutureNotifier<Response> future) {
					if (future.isSuccess() && future.getNow() instanceof StartApplicationAck) {
						informModuleStarted(moduleID);
					}
				}
			});
			futureNotifiers.add(futureNotifier);
		}
		return new JoinedFutureNotifier<Response>(futureNotifiers);
	}

	/**
	 * This method is called when the FutureNotifier for the Responses to the {@link StartApplicationMessage} is
	 * finished. If a positive Response is received, {@link #deployFutureNotifier} is marked as finished successfully,
	 * otherwise it is marked as failed.
	 * 
	 * @param future
	 *            the FutureNotifier that returns the Responses
	 */
	private void handleStartApplicationFinished(final FutureNotifier<Collection<Response>> future) {
		LOGGER.entry();
		if (deployFutureNotifier.isDone() && !deployFutureNotifier.isSuccess()) {
			LOGGER.exit();
			return;
		}

		if (future.isSuccess()) {
			for (final Response response : future.getNow()) {
				if (!(response instanceof StartApplicationAck)) {
					deployFutureNotifier.setFailure0(null);
					LOGGER.exit();
					return;
				}
			}

			LOGGER.debug("all modules started succesfully");
			deployFutureNotifier.setSuccess0();
		} else {
			deployFutureNotifier.setFailure0(future.cause());
		}
		LOGGER.exit();
	}

	/**
	 * Calls {@link DeployListener#moduleStarted(ApplicationID, ModuleID)} on all listeners.
	 * 
	 * @param moduleID
	 *            the ID of the ModuleInfo that started
	 */
	private void informModuleStarted(final ModuleID moduleID) {
		listenerLock.readLock().lock();
		try {
			for (final DeployListener listener : listeners) {
				listener.moduleStarted(applicationID, moduleID);
			}
		} finally {
			listenerLock.readLock().unlock();
		}
	}

	/**
	 * Calls {@link DeployListener#deployFailed(Throwable)} on all listeners.
	 * 
	 * @param cause
	 *            the cause for the failure
	 */
	private void informDeployFailed(final Throwable cause) {
		listenerLock.readLock().lock();
		try {
			for (final DeployListener listener : listeners) {
				listener.deployFailed(applicationID, cause);
			}
		} finally {
			listenerLock.readLock().unlock();
		}
	}

	private void killApplication() {
		LOGGER.info("killing failed application");
		for (final ModuleInfo module : moduleMap.keySet()) {
			final KillAppMessage killAppMsg = new KillAppMessage(applicationID);
			connectionManager.sendMessage(module.getID(), killAppMsg);
		}
	}

	/**
	 * This class is used to provide a FutureNotifier for the deployment process.
	 * 
	 * @author Philipp Adolf
	 */
	private class DeployFutureNotifier extends DefaultFutureNotifier<Void> {
		/**
		 * Sets this FutureNotifier to be finished successfully (if it hasn't been set as finished already).
		 */
		protected void setSuccess0() {
			LOGGER.trace("setting success");
			setSuccess(null);
		}

		/**
		 * Sets this FutureNotifier to be failed (if it hasn't been set as finished already). It also sends a
		 * {@link KillAppMessage} to all Modules.
		 * 
		 * @param cause
		 *            the cause for the failure
		 */
		protected boolean setFailure0(final Throwable cause) {
			LOGGER.warn("setting failure, cause: {}", cause);
			final boolean setFailureSucceeded = setFailure(cause);
			if (setFailureSucceeded) {
				killApplication();
				informDeployFailed(cause);
			}
			return setFailureSucceeded;
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			if (!mayInterruptIfRunning) {
				return false;
			}
			return setFailure0(new CancellationException());
		}
	}
}
