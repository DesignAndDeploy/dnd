package edu.teco.dnd.module;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.teco.dnd.blocks.AssignmentException;
import edu.teco.dnd.blocks.FunctionBlock;

/**
 * A thread that updates a single FunctionBlock.
 */
public class Updater implements Runnable {
	/** Time to wait if no timer is used. */
	public static final long DEFAULT_WAIT = 1000L;

	/** Lock used for {@link #needsUpdate}. */
	private final Lock lock = new ReentrantLock();

	/** Is used to signal that an input has changed. */
	private final Condition needsUpdate = lock.newCondition();

	private final Map<UUID, Set<FunctionBlock>> runnableBlocks; // TODO change set to something sorted, to make
																// concurrent fair iteration without too many locks
																// possible

	private final Map<UUID, Set<FunctionBlock>> blocksNeedingInit;
	private final Set<UUID> applist;

	/** Whether to keep running or stop updating. */
	private final AtomicBoolean keepRunning = new AtomicBoolean(true);

	public Updater(Set<UUID> applist, Map<UUID, Set<FunctionBlock>> runnableBlocks,
			Map<UUID, Set<FunctionBlock>> blocksNeedingInit) {
		this.runnableBlocks = runnableBlocks;
		this.blocksNeedingInit = blocksNeedingInit;
		this.applist = applist;
	}

	/**
	 * Updates the FunctionBlock.
	 */
	@Override
	public void run() {

		while (keepRunning.get()) {
			for (UUID appId : applist) { // TODO: fair scheduling of apps

				Set<FunctionBlock> needInitList = blocksNeedingInit.get(appId);
				for (FunctionBlock block : needInitList) { // completely unfair scheduler. Every block of the app is
															// initialized before a single one is started.
					Boolean reallyRemoved;
					synchronized (needInitList) {
						reallyRemoved = needInitList.remove(block);
					}
					if (reallyRemoved) {
						block.init();
						block.resetTimer();
					} // else some other updater did the init() at the same time.

				}

				for (FunctionBlock block : runnableBlocks.get(appId)) { // TODO be fair regarding later blocks in the
																		// list.
					if (block.isDirty()) {
						try {
							block.doUpdate();
						} catch (AssignmentException e) {
							e.printStackTrace();
						}
					}

				}
			}
			// TODO sleep till next update needed?
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// ignore
			}

		}

	}

	/**
	 * Signals the Updater that it should stop updating.
	 */
	public void stopRunning() {
		keepRunning.set(false);
		// TODO something to tell a single block to go away.
	}

	/**
	 * Notifies the updater that a ConnectionTarget has a new value.
	 */
	public void notifyChanged(UUID appId, FunctionBlock block) {
		// TODO
		lock.lock();
		needsUpdate.signal();
		lock.unlock();
	}
}
