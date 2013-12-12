package edu.teco.dnd.network.tcp;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import edu.teco.dnd.network.tcp.ResponseFutureManager.ResponseFutureNotifier;

public class TimeoutResponseInvalidator {
	private final ScheduledExecutorService executorService;
	private final long defaultDelay;
	private final TimeUnit defaultUnit;

	public TimeoutResponseInvalidator(final ScheduledExecutorService executorService, final long defaultDelay,
			final TimeUnit defaultUnit) {
		this.executorService = executorService;
		this.defaultDelay = defaultDelay;
		this.defaultUnit = defaultUnit;
	}

	public void addTimeout(final ResponseFutureNotifier responseFutureNotifier) {
		addTimeout(responseFutureNotifier, defaultDelay, defaultUnit);
	}

	public void addTimeout(final ResponseFutureNotifier responseFutureNotifier, final long delay, final TimeUnit unit) {
		executorService.schedule(new Invalidator(responseFutureNotifier), delay, unit);
	}

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
