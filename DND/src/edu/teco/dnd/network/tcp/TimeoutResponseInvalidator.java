package edu.teco.dnd.network.tcp;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import edu.teco.dnd.network.tcp.ResponseFutureManager.ResponseFutureNotifier;

/**
 * Invalidates {@link ResponseFutureNotifier}s after a given timeout.
 */
public class TimeoutResponseInvalidator {
	private final ScheduledExecutorService executorService;
	private final long defaultTimeout;
	private final TimeUnit defaultUnit;

	/**
	 * Initializes a new TimeoutResponseValidator.
	 * 
	 * @param executorService
	 *            this service will be used to invalidate the futures after the timeout
	 * @param defaultTimeout
	 *            a default timeout for {@link #addTimeout(ResponseFutureNotifier)}
	 * @param defaultUnit
	 *            the unit of <code>defaultTimeout</code>
	 */
	public TimeoutResponseInvalidator(final ScheduledExecutorService executorService, final long defaultTimeout,
			final TimeUnit defaultUnit) {
		this.executorService = executorService;
		this.defaultTimeout = defaultTimeout;
		this.defaultUnit = defaultUnit;
	}

	/**
	 * Adds a default timeout for a future.
	 * 
	 * @param responseFutureNotifier
	 *            the future the timeout should be added for
	 * @see #TimeoutResponseInvalidator(ScheduledExecutorService, long, TimeUnit)
	 */
	public void addTimeout(final ResponseFutureNotifier responseFutureNotifier) {
		addTimeout(responseFutureNotifier, defaultTimeout, defaultUnit);
	}

	/**
	 * Adds a timeout for a future.
	 * 
	 * @param responseFutureNotifier
	 *            the future the timeout should be added for
	 * @param timeout
	 *            the timeout
	 * @param unit
	 *            the unit for <code>timeout</code>
	 */
	public void addTimeout(final ResponseFutureNotifier responseFutureNotifier, final long timeout, final TimeUnit unit) {
		executorService.schedule(new Invalidator(responseFutureNotifier), timeout, unit);
	}

	/**
	 * This code will be executed by the executor service given to
	 * {@link TimeoutResponseInvalidator#TimeoutResponseInvalidator(ScheduledExecutorService, long, TimeUnit)} after the
	 * timeout. It will unconditionally fail the future, however, if the future is already done this is a no-op.
	 */
	private static class Invalidator implements Runnable {
		private final ResponseFutureNotifier responseFutureNotifier;

		private Invalidator(final ResponseFutureNotifier responseFutureNotifier) {
			this.responseFutureNotifier = responseFutureNotifier;
		}

		@Override
		public void run() {
			responseFutureNotifier.setFailure0(new TimeoutException());
		}
	}
}
