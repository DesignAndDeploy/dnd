package edu.teco.dnd.module;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.FunctionBlockID;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.InputDescription;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.blocks.OutputTarget;
import edu.teco.dnd.module.ModuleBlockManager.BlockTypeHolderFullException;
import edu.teco.dnd.module.ModuleBlockManager.NoSuchBlockTypeHolderException;
import edu.teco.dnd.module.config.BlockTypeHolder;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.util.HashStorage;
import edu.teco.dnd.util.ValueWithHash;

/**
 * Represents an Application running on a {@link Module}. This will only contain the {@link FunctionBlock}s that are
 * running on the local Module.
 */
public class Application {
	private static final Logger LOGGER = LogManager.getLogger(Application.class);

	/** Time all shutdown hooks of an application have to run before being killed. */
	public static final int TIME_BEFORE_ATTEMPTED_SHUTDOWNHOOK_KILL = 2000;
	/** Additional time granted, for shutdownhooks after kill attempt, before thread is forcefully stopped. */
	public static final int ADDITIONAL_TIME_BEFORE_FORCEFULL_KILL = 500;

	/**
	 * Current state of the application. Can only advance to the next state: the Application starts in CREATED, then
	 * goes to RUNNING and eventually transitions to STOPPED.
	 */
	private enum State {
		CREATED, RUNNING, STOPPED
	}

	private final ApplicationID applicationID;
	private final String name;
	private final ScheduledThreadPoolExecutor scheduledThreadPool;
	private final ConnectionManager connectionManager;
	private final ModuleBlockManager moduleBlockManager;
	private final HashStorage<byte[]> byteCodeStorage;

	private State currentState = State.CREATED;
	private final ReadWriteLock currentStateLock = new ReentrantReadWriteLock();

	private final Set<FunctionBlockSecurityDecorator> scheduledToStart = new HashSet<FunctionBlockSecurityDecorator>();
	private final Map<FunctionBlockSecurityDecorator, Map<String, String>> blockOptions =
			new HashMap<FunctionBlockSecurityDecorator, Map<String, String>>();

	/**
	 * A Map from FunctionBlock UUID to matching ValueSender. Not used for local FunctionBlocks.
	 */
	private final ConcurrentMap<FunctionBlockID, ValueSender> valueSenders =
			new ConcurrentHashMap<FunctionBlockID, ValueSender>();

	private final ApplicationClassLoader classLoader = new ApplicationClassLoader();
	/** mapping of active blocks to their ID, used e.g. to pass values to inputs. */
	private final ConcurrentMap<FunctionBlockID, FunctionBlockSecurityDecorator> functionBlocksById =
			new ConcurrentHashMap<FunctionBlockID, FunctionBlockSecurityDecorator>();

	/**
	 * Initializes a new Application. Normally this should not be called directly, use
	 * {@link Module#joinApplication(ApplicationID, String)} instead.
	 * 
	 * @param applicationID
	 *            ID of this application
	 * @param name
	 *            human readable name of this application
	 * @param connectionManager
	 *            ConnectionManager used to send values to FunctionBlocks running on remote Modules
	 * @param threadFactory
	 *            will be used to create Threads that will {@link FunctionBlock#update() update} the
	 *            {@link FunctionBlock}s belonging to this Application
	 * @param maxThreads
	 *            the maximum number of Threads that will be used
	 * @param moduleBlockManager
	 *            this BlockManager will be checked to see if a FunctionBlock can be executed locally
	 * @param byteCodeStorage
	 *            a HashStorage that is used to globaly store the byte code. This can be used to cache byte code if
	 *            multiple Applications execute the same FunctionBlock class.
	 */
	public Application(final ApplicationID applicationID, final String name, final ConnectionManager connectionManager,
			final ThreadFactory threadFactory, final int maxThreads, final ModuleBlockManager moduleBlockManager,
			final HashStorage<byte[]> byteCodeStorage) {
		this.applicationID = applicationID;
		this.name = name;
		this.byteCodeStorage = byteCodeStorage;
		this.scheduledThreadPool =
				new ScheduledThreadPoolExecutor(maxThreads, new ContextClassLoaderThreadFactory(threadFactory));
		this.connectionManager = connectionManager;
		this.moduleBlockManager = moduleBlockManager;
	}

	public boolean isRunning() {
		currentStateLock.readLock().lock();
		try {
			return currentState == State.RUNNING;
		} finally {
			currentStateLock.readLock().unlock();
		}
	}

	public boolean hasShutDown() {
		currentStateLock.readLock().lock();
		try {
			return currentState == State.STOPPED;
		} finally {
			currentStateLock.readLock().unlock();
		}
	}

	/**
	 * This is called to send a value to a remote {@link FunctionBlock} that is either running here or on a remote
	 * Module. The {@link FunctionBlock} must belong to the same Application (although it does not have to belong to
	 * this object as this is only for FunctionBlocks running locally).
	 * 
	 * @param inputDescription
	 *            ID of the FunctionBlock and the name of the {@link Input} the value should be sent to
	 * @param value
	 *            the value to send
	 */
	private void sendValue(final InputDescription inputDescription, final Serializable value) {
		if (inputDescription == null) {
			throw new IllegalArgumentException("inputDescription must not be null");
		}
		if (inputDescription.getBlock() == null) {
			throw new IllegalArgumentException("FunctionBlockID must not be null");
		}
		if (inputDescription.getInput() == null) {
			throw new IllegalArgumentException("Input must not be null");
		}
		// sending null is allowed, as some FunctionBlocks may make use of it

		if (hasFunctionBlockWithID(inputDescription.getBlock())) { // block is local
			try {
				receiveValue(inputDescription, value);
			} catch (final NonExistentFunctionblockException e) {
				LOGGER.catching(e);
			} catch (final NonExistentInputException e) {
				LOGGER.catching(e);
			}
		} else {
			getValueSender(inputDescription.getBlock()).sendValue(inputDescription.getInput(), value);
		}
	}

	/**
	 * Returns a ValueSender for the given target FunctionBlock. If no ValueSender for that FunctionBlock exists yet a
	 * new one is created. When called with the same FunctionBlockID it will always return the same ValueSender, even if
	 * called concurrently in different Threads.
	 * 
	 * @param blockID
	 *            the ID of the FunctionBlock for which a ValueSender should be returned
	 * @return the ValueSender for the given FunctionBlock
	 */
	// FIXME: Need a way to clean up old value senders
	private ValueSender getValueSender(final FunctionBlockID blockID) {
		ValueSender valueSender = valueSenders.get(blockID);
		if (valueSender == null) {
			valueSender = new ValueSender(applicationID, blockID, connectionManager);
			// if between the get and this call another Thread put a ValueSender into the map, this call will return the
			// ValueSender the other Thread put into the Map. We'll use that one instead of our new one so that only one
			// ValueSender exists per target
			ValueSender oldValueSender = valueSenders.putIfAbsent(blockID, valueSender);
			if (oldValueSender != null) {
				valueSender = oldValueSender;
			}
		}
		return valueSender;
	}

	/**
	 * Loads a class into the ClassLoader used by the Application. After this method is called FunctionBlocks of the
	 * given class or depending on this class can be {@link #scheduleBlock(BlockDescription) added} (if all other
	 * dependencies are also loaded). Also stores the byte code in the {@link HashStorage} passed to
	 * {@link #Application(ApplicationID, String, ConnectionManager, ThreadFactory, int, ModuleBlockManager, HashStorage)}
	 * if it is missing.
	 * 
	 * @param className
	 *            name of the class to load
	 * @param byteCode
	 *            byte code of the class to be loaded
	 */
	public void loadClass(String className, byte[] byteCode) {
		if (className == null || byteCode == null) {
			throw new IllegalArgumentException("className and byteCode must not be null.");
		}

		final ValueWithHash<byte[]> unifiedByteCode = byteCodeStorage.putIfAbsent(byteCode);
		classLoader.injectClass(className, unifiedByteCode.getValue());
	}

	/**
	 * Adds a {@link FunctionBlock} to this Application. If the Application {@link #isRunning() is Running} the
	 * FunctionBlock will immediately executed. If not, it will be stored until {@link #start()} is called.
	 * 
	 * @param blockDescription
	 *            the block to add
	 * @throws ClassNotFoundException
	 *             if the class given is not known by the ClassLoader of this Application
	 * @throws UserSuppliedCodeException
	 *             if the FunctionBlock throws any {@link Throwable}
	 * @throws NoSuchBlockTypeHolderException
	 *             if the {@link BlockTypeHolder} specified in <code>blockDescription</code> does not exist
	 * @throws BlockTypeHolderFullException
	 *             if the BlockTypeHolder specified in <code>blockDescription</code> is already full
	 * @throws IllegalArgumentException
	 *             if the class name specified in <code>blockDescription</code> is not a FunctionBlock
	 * @see #loadClass(String, byte[])
	 */
	public void scheduleBlock(final BlockDescription blockDescription) throws ClassNotFoundException,
			UserSuppliedCodeException, BlockTypeHolderFullException, NoSuchBlockTypeHolderException {
		LOGGER.entry(blockDescription);
		currentStateLock.readLock().lock();
		try {
			if (hasShutDown()) {
				throw LOGGER.throwing(new IllegalStateException(this + " has already been stopped"));
			}

			final FunctionBlockSecurityDecorator securityDecorator =
					createFunctionBlockSecurityDecorator(blockDescription.getBlockClassName());
			LOGGER.trace("calling doInit on securityDecorator {}", securityDecorator);
			securityDecorator.initInternal(blockDescription.getBlockID(), blockDescription.getBlockName());

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("adding {} to ID {}", securityDecorator, blockDescription.getBlockTypeHolderID());
			}
			moduleBlockManager.addToBlockTypeHolders(applicationID, securityDecorator,
					blockDescription.getBlockTypeHolderID());

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("initializing outputs {} on {}", blockDescription.getOutputs(), securityDecorator);
			}
			initializeOutputs(securityDecorator, blockDescription.getOutputs());

			if (isRunning()) {
				startBlock(securityDecorator, blockDescription.getOptions());
			} else {
				synchronized (scheduledToStart) {
					LOGGER.trace("adding {} to scheduledToStart");
					scheduledToStart.add(securityDecorator);
					LOGGER.trace("saving block options");
					blockOptions.put(securityDecorator, blockDescription.getOptions());
				}
			}
		} finally {
			currentStateLock.readLock().unlock();
		}
	}

	/**
	 * Instantiates a {@link FunctionBlock} and wraps it in a {@link FunctionBlockSecurityDecorator}.
	 * 
	 * @param className
	 *            the name of the class to instantiate
	 * @return a new FunctionBlockSecurityDecorator wrapping a FunctionBlock of the given class
	 * @throws ClassNotFoundException
	 *             if {@link #classLoader} cannot find the class
	 * @throws UserSuppliedCodeException
	 *             if the FunctionBlock throws any {@link Throwable}
	 * @throws IllegalArgumentException
	 *             if the given class is not a FunctionBlock
	 */
	@SuppressWarnings("unchecked")
	private FunctionBlockSecurityDecorator createFunctionBlockSecurityDecorator(final String className)
			throws ClassNotFoundException, UserSuppliedCodeException, IllegalArgumentException {
		Class<?> cls = null;
		cls = classLoader.loadClass(className);
		if (!FunctionBlock.class.isAssignableFrom(cls)) {
			throw new IllegalArgumentException("class " + className + " is not a FunctionBlock");
		}
		return new FunctionBlockSecurityDecorator((Class<? extends FunctionBlock>) cls);
	}

	/**
	 * Initializes the {@link Output}s of {@link FunctionBlock} so that they can be used to send values to the
	 * appropriate {@link Input}s.
	 * 
	 * @param securityDecorator
	 *            the SecurityDecorator holding the FunctionBlock that should be initialized
	 * @param outputs
	 *            a mapping from the name of an Output to the Inputs it should send to
	 */
	private void initializeOutputs(final FunctionBlockSecurityDecorator securityDecorator,
			final Map<String, Set<InputDescription>> outputs) {
		final Map<String, Output<? extends Serializable>> blockOutputs = securityDecorator.getOutputs();
		for (final Entry<String, Set<InputDescription>> output : outputs.entrySet()) {
			if (!blockOutputs.containsKey(output.getKey())) {
				continue;
			}
			final Output<? extends Serializable> blockOutput = blockOutputs.get(output.getKey());
			blockOutput.setTarget(new ApplicationOutputTarget(output.getValue()));
		}
	}

	/**
	 * Starts the Application. This will call {@link FunctionBlock#init(Map)} on all {@link FunctionBlock}s that were
	 * {@link #scheduleBlock(BlockDescription) added} to the Application. It will also schedule an updater to run
	 * periodically if this is requested by the FunctionBlock (see {@link FunctionBlock#getUpdateInterval()}.
	 * 
	 * @throws IllegalStateException
	 *             if the Application has already been started
	 */
	public void start() {
		currentStateLock.readLock().lock();
		try {
			if (currentState != State.CREATED) {
				throw LOGGER.throwing(new IllegalStateException("Tried to start " + this + " while it was in State "
						+ currentState));
			}
		} finally {
			currentStateLock.readLock().unlock();
		}

		currentStateLock.writeLock().lock();
		try {
			if (currentState != State.CREATED) {
				throw LOGGER.throwing(new IllegalStateException("Tried to start " + this + " while it was in State "
						+ currentState));
			}

			currentState = State.RUNNING;

			synchronized (scheduledToStart) {
				for (final FunctionBlockSecurityDecorator func : scheduledToStart) {
					startBlock(func, blockOptions.get(func));
				}

				scheduledToStart.clear();
				blockOptions.clear();
			}
		} finally {
			currentStateLock.writeLock().unlock();
		}
	}

	/**
	 * Starts a single {@link FunctionBlock}. Will call {@link FunctionBlock#init(Map)} with the given options. Also
	 * installs a Runnable that will periodically {@link FunctionBlock#update() update} the FunctionBlock if requested
	 * (see {@link FunctionBlock#getUpdateInterval()}. If not, it will call update() once. init() is guaranteed to be
	 * executed before the method returns, update() however is not.
	 */
	private void startBlock(final FunctionBlockSecurityDecorator block, final Map<String, String> options) {
		currentStateLock.readLock().lock();
		try {
			if (hasShutDown()) {
				throw LOGGER.throwing(new IllegalStateException(this + " has already been shut down"));
			}

			final Runnable initRunnable = new Runnable() {
				@Override
				public void run() {
					try {
						block.init(options);
					} catch (UserSuppliedCodeException e) {
						// TODO: handle malevolent block. Stop it, maybe?
					}
				}
			};
			final Runnable updater = new Runnable() {
				@Override
				public void run() {
					try {
						block.update();
					} catch (UserSuppliedCodeException e) {
						// TODO: handle malevolent block. Stop it, maybe?
					}
				}
			};

			final Future<?> initFuture = scheduledThreadPool.submit(initRunnable);
			while (!initFuture.isDone()) {
				try {
					initFuture.get();
				} catch (final InterruptedException e) {
					LOGGER.debug("got interrupted waiting for init future of {}", block);
				} catch (final ExecutionException e) {
					LOGGER.catching(e);
					return;
				}
			}

			// FIXME: if two blocks share the UUID, blocks get lost
			functionBlocksById.put(block.getBlockID(), block);

			long period = block.getUpdateInterval();
			try {
				if (period < 0) {
					scheduledThreadPool.schedule(updater, 0, TimeUnit.SECONDS);
				} else {
					scheduledThreadPool.scheduleAtFixedRate(updater, period, period, TimeUnit.MILLISECONDS);
				}
			} catch (RejectedExecutionException e) {
				LOGGER.catching(e);
			}
		} finally {
			currentStateLock.readLock().unlock();
		}
	}

	/**
	 * Receives a value for a {@link FunctionBlock} running locally.
	 * 
	 * @param inputDescription
	 *            the {@link FunctionBlockID} and {@link Input} name the value is for
	 * @param value
	 *            the value to give to the {@link Input}
	 * @throws IllegalStateException
	 *             if this Application is not running
	 * @throws NonExistentFunctionblockException
	 *             if the FunctionBlock is not being executed by this Application on this {@link Module}
	 * @throws NonExistentInputException
	 *             if the FunctionBlock does not have an Input called <code>inputName</code>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void receiveValue(final InputDescription inputDescription, Serializable value)
			throws NonExistentFunctionblockException, NonExistentInputException {
		currentStateLock.readLock().lock();
		try {
			if (isRunning()) {
				throw new IllegalStateException(this + " is not running");
			}

			final FunctionBlockSecurityDecorator block = functionBlocksById.get(inputDescription.getBlock());
			if (block == null) {
				throw LOGGER.throwing(new NonExistentFunctionblockException(inputDescription.getBlock().toString()));
			}

			final Input input = block.getInputs().get(inputDescription.getInput());
			if (input == null) {
				throw LOGGER.throwing(new NonExistentInputException("FunctionBlock " + inputDescription.getBlock()
						+ " does not have an input called " + inputDescription.getInput()));
			}
			input.setValue(value);

			final Runnable updater = new Runnable() {
				@Override
				public void run() {
					try {
						block.update();
					} catch (UserSuppliedCodeException e) {
						// TODO: handle malevolent block. Stop it, maybe?
					}
				}
			};

			try {
				scheduledThreadPool.schedule(updater, 0, TimeUnit.SECONDS);
			} catch (RejectedExecutionException e) {
				LOGGER.catching(e);
			}
		} finally {
			currentStateLock.readLock().unlock();
		}
	}

	/**
	 * Shuts down this Application. This involves calling {@link FunctionBlock#shutdown()} on all {@link FunctionBlock}
	 * s.
	 * 
	 * @throws IllegalStateException
	 *             if the Application is not currently {@link #isRunning() running}
	 */
	@SuppressWarnings("deprecation")
	public void shutdown() {
		currentStateLock.readLock().lock();
		try {
			if (currentState != State.RUNNING) {
				throw LOGGER.throwing(new IllegalStateException(this + " is not currently running"));
			}
		} finally {
			currentStateLock.readLock().unlock();
		}

		currentStateLock.writeLock().lock();
		try {
			if (currentState != State.RUNNING) {
				throw LOGGER.throwing(new IllegalStateException(this + " is not currently running"));
			}

			scheduledThreadPool.shutdown();

			final Thread shutdownThread = new Thread() {
				@Override
				public void run() {
					for (final FunctionBlockSecurityDecorator block : functionBlocksById.values()) {
						if (Thread.interrupted()) {
							LOGGER.warn("shutdownThread got interrupted, not shutting down remaining FunctionBlocks");
							break;
						}
						try {
							block.shutdown();
						} catch (UserSuppliedCodeException e) {
							LOGGER.catching(e);
						}
					}
				}
			};
			shutdownThread.start();

			sleepUninterrupted(TIME_BEFORE_ATTEMPTED_SHUTDOWNHOOK_KILL);
			if (!shutdownThread.isAlive()) {
				LOGGER.debug("shutdownThread finished in time");
				return;
			}
			LOGGER.info("shutdownThread is taking too long. Interrupting it.");
			shutdownThread.interrupt();

			sleepUninterrupted(ADDITIONAL_TIME_BEFORE_FORCEFULL_KILL);
			if (!shutdownThread.isAlive()) {
				LOGGER.debug("shutdownThread finished in time after interrupting");
				return;
			}
			LOGGER.warn("Shutdown thread hanging. Killing it.");
			shutdownThread.stop();
			// It's deprecated and dangerous to stop a thread like this, because it forcefully releases all locks,
			// yet there is no alternative to it if the victim is refusing to cooperate.
		} finally {
			currentStateLock.writeLock().unlock();
		}
	}

	public ApplicationID getApplicationID() {
		return applicationID;
	}

	public String getName() {
		return name;
	}

	/**
	 * This ClassLoader is used by this Application to load {@link FunctionBlock}s.
	 * 
	 * @return the ClassLoader used by the Application
	 * @see #loadClass(String, byte[])
	 */
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	/**
	 * This ThreadPool is used by this Application to run the {@link FunctionBlock} code.
	 * 
	 * @return the ThreadPool used to run FunctionBlock code
	 */
	public ScheduledThreadPoolExecutor getThreadPool() {
		return scheduledThreadPool;
	}

	public Map<FunctionBlockID, FunctionBlockSecurityDecorator> getFunctionBlocksById() {
		return new HashMap<FunctionBlockID, FunctionBlockSecurityDecorator>(functionBlocksById);
	}

	public boolean hasFunctionBlockWithID(FunctionBlockID blockId) {
		return functionBlocksById.containsKey(blockId);
	}

	/**
	 * Behaves like a Thread.sleep(millisToSleep), with the exception, that all InterruptedExceptions are disregarded
	 * (=have no influence on sleep time and are dropped)
	 * 
	 * @param millisToSleep
	 *            time to sleep in milli seconds.
	 */
	private void sleepUninterrupted(long millisToSleep) {
		long sleepTill = System.currentTimeMillis() + millisToSleep;
		long timeLeftToSleep = millisToSleep;
		while (timeLeftToSleep > 0) {
			try {
				Thread.sleep(timeLeftToSleep);
				break;
			} catch (InterruptedException e) {
				timeLeftToSleep = sleepTill - System.currentTimeMillis();
			}
		}
	}

	/**
	 * An OutputTarget that calls {@link Application#sendValue(InputDescription, Serializable)} with a given list of
	 * target {@link InputDescription}s.
	 */
	private class ApplicationOutputTarget implements OutputTarget<Serializable> {
		private final Set<InputDescription> destinations;

		/**
		 * Initializes a new ApplicationOutputTarget.
		 * 
		 * @param destinations
		 *            any values received will be forwared to these Inputs (via
		 *            {@link Application#sendValue(InputDescription, Serializable)})
		 */
		public ApplicationOutputTarget(final Collection<InputDescription> destinations) {
			this.destinations = new HashSet<InputDescription>(destinations);
		}

		@Override
		public void setValue(Serializable value) {
			for (final InputDescription destination : destinations) {
				sendValue(destination, value);
			}
		}
	}

	/**
	 * A wrapper for ThreadFactory that sets the ContextClassLoader to {@link Application#classLoader}.
	 */
	private class ContextClassLoaderThreadFactory implements ThreadFactory {
		private final ThreadFactory internalFactory;

		private ContextClassLoaderThreadFactory(final ThreadFactory internalFactory) {
			this.internalFactory = internalFactory;
		}

		@Override
		public Thread newThread(final Runnable r) {
			final Thread thread = internalFactory.newThread(r);
			thread.setContextClassLoader(classLoader);
			return thread;
		}
	}
}
