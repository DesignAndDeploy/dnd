package edu.teco.dnd.module;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.teco.dnd.blocks.AssignmentException;
import edu.teco.dnd.blocks.FunctionBlock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Takes care of updating a number of FunctionBlocks.
 */
public class Scheduler {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(Scheduler.class);

	/**
	 * A thread that updates a single FunctionBlock.
	 */
	private static class Updater extends Thread {
		/**
		 * Time to wait if no timer is used.
		 */
		public static final long DEFAULT_WAIT = 1000L;

		/**
		 * Lock used for {@link #needsUpdate}.
		 */
		private final Lock lock = new ReentrantLock();

		/**
		 * Is used to signal that an input has changed.
		 */
		private final Condition needsUpdate = lock.newCondition();

		/**
		 * The FunctionBlock this Updater updates.
		 */
		private final FunctionBlock functionBlock;

		/**
		 * Whether to keep running or stop updating.
		 */
		private final AtomicBoolean keepRunning = new AtomicBoolean(true);

		/**
		 * Initializes a new Updater.
		 * 
		 * @param functionBlock
		 *            the FunctionBlock to update
		 */
		public Updater(final FunctionBlock functionBlock) {
			this.functionBlock = functionBlock;

		}

		/**
		 * Updates the FunctionBlock.
		 */
		@Override
		public void run() {
			functionBlock.init();
			functionBlock.resetTimer();
			while (keepRunning.get()) {
				if (functionBlock.isDirty()) {
					try {
						functionBlock.doUpdate();
					} catch (AssignmentException e) {
						e.printStackTrace();
					}
				}
				lock.lock();
				long timeToTick = functionBlock.getTimeToNextTick();
				if (timeToTick < 0) {
					timeToTick = DEFAULT_WAIT;
				}
				try {
					needsUpdate.await(timeToTick, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					LOGGER.catching(e);
				}
				lock.unlock();
			}
		}

		/**
		 * Signals the Updater that it should stop updating.
		 */
		public void stopRunning() {
			keepRunning.set(false);
		}

		/**
		 * Notifies the updater that a ConnectionTarget has a new value.
		 */
		public void notifyChanged() {
			lock.lock();
			needsUpdate.signal();
			lock.unlock();
		}
	}

	/**
	 * Maps from FunctionBlock ID to Updater.
	 */
	private final Map<String, Updater> updaters = new HashMap<String, Updater>();

	/**
	 * Adds a FunctionBlock to update.
	 * 
	 * @param functionBlock
	 *            the FunctionBlock to update
	 */
	public final void addFunctionBlock(final FunctionBlock functionBlock) {
		if (functionBlock == null) {
			throw new IllegalArgumentException("functionBlock must not be null");
		}
		if (!updaters.containsKey(functionBlock.getID())) {
			Updater updater = new Updater(functionBlock);
			updaters.put(functionBlock.getID(), updater);
			updater.start();  //TODO use threadpool to limit the amount of threads.
			LOGGER.info("Started updater for {}", functionBlock.getID());
		}
	}

	/**
	 * Notifies the Scheduler that a ConnectionTarget of a FunctionBlock has changed.
	 * 
	 * @param id
	 *            the ID of the block
	 */
	public final void notifyChanged(final String id) {
		Updater updater = updaters.get(id);
		if (updater != null) {
			updater.notifyChanged();
		}
	}

	/**
	 * Stops updating FunctionBlocks.
	 */
	public final void stopRunning() {
		LOGGER.info("stopping all updaters");
		for (Updater updater : updaters.values()) {
			updater.stopRunning();
		}
	}

	/**
	 * Waits for all threads used by the Scheduler to finish.
	 * 
	 * @throws InterruptedException
	 *             if the current thread gets interrupted
	 */
	public final void joinAll() throws InterruptedException {
		LOGGER.info("joining all updaters started");
		for (Updater updater : updaters.values()) {
			updater.join();
		}
	}
}
