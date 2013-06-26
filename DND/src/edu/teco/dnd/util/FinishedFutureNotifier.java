package edu.teco.dnd.util;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FinishedFutureNotifier<V> extends AbstractFutureNotifier<V> {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(FinishedFutureNotifier.class);
	
	private final V result;
	
	private final Throwable cause;
	
	public FinishedFutureNotifier(V result) {
		this.result = result;
		this.cause = null;
	}
	
	public FinishedFutureNotifier(Throwable cause) {
		this.result = null;
		this.cause = cause;
	}
	
	@Override
	public boolean isSuccess() {
		return cause != null;
	}

	@Override
	public Throwable cause() {
		return cause;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addListener(final FutureListener<? extends FutureNotifier<? super V>> listener) {
		try {
			((FutureListener) listener).operationComplete(this);
		} catch (final Exception e) {
			LOGGER.warn("listener {} threw exception {}", listener, e);
		}
	}

	@Override
	public void removeListener(FutureListener<? extends FutureNotifier<? super V>> listener) {
	}

	@Override
	public V getNow() {
		return result;
	}

	@Override
	public void await() throws InterruptedException {
	}

	@Override
	public boolean await(final long timeout, final TimeUnit unit) throws InterruptedException {
		return true;
	}

	@Override
	public boolean cancel(final boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return true;
	}
}
