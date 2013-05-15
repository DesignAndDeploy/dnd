package edu.teco.dnd.util;

import java.util.concurrent.LinkedBlockingQueue;

import lime.StationaryAgent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Used to dispatch jobs which can not be directly executed (as in e.g. strong reactions).
 */
public abstract class RunnerAgent extends StationaryAgent {

	/**
	 * Used for logging.
	 */
	private static final Logger LOGGER = LogManager.getLogger(RunnerAgent.class);

	/**
	 * Used to mark the end.
	 */
	private class EndMarker implements Runnable {
		@Override
		public void run() {
			assert false;
		}
	};

	/**
	 * Used for serializing.
	 */
	private static final long serialVersionUID = 1613445876684882484L;

	/**
	 * Stores the jobs that should be run.
	 */
	private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

	/**
	 * Initializes a new RunnerAgent.
	 */
	public RunnerAgent() {
		super();
	}

	/**
	 * Retrieves jobs from the queue and executes them. Stops if null is encountered.
	 */
	@Override
	public final void run() {
		doRun();
		boolean running = true;
		while (running) {
			Runnable runnable;
			try {
				runnable = queue.take();
				LOGGER.debug("Now running runnable {}", runnable);
			} catch (InterruptedException e) {
				continue;
			}
			if (runnable instanceof EndMarker) {
				running = false;
			} else {
				runnable.run();
			}
		}
	}

	/**
	 * Is called by run before processing of jobs starts.
	 */
	public abstract void doRun();

	/**
	 * adds a runnable to be run at a later time.
	 * 
	 * @param runnable
	 *            the runnable, <br>
	 *            when null is inserted RunnerAgent terminates after finishing the jobs scheduled before the
	 *            null.
	 */
	protected final synchronized void addRunnable(final Runnable runnable) {
		queue.offer((runnable != null) ? runnable : new EndMarker());
		LOGGER.info("adding runnable and exiting");
	}

	/**
	 * adds a runnable to the queue and guaratees that it will be the last one executed, (RunnerAgent will
	 * exit after executing it.).
	 * 
	 * @param runnable
	 *            the runnable to add
	 */
	protected final void addAndEnd(final Runnable runnable) {
		if (runnable == null) {
			throw new NullPointerException();
		}

		synchronized (this) {
			queue.offer(runnable);
			queue.offer(new EndMarker());
		}
	}
}
