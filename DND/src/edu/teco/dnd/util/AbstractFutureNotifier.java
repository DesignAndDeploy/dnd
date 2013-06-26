package edu.teco.dnd.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractFutureNotifier<V> implements FutureNotifier<V> {
	@Override
	public V get() throws InterruptedException, ExecutionException {
		await();
		
		final Throwable cause = cause();
		if (cause != null) {
			throw new ExecutionException(cause);
		}
		
		return getNow();
	}
	
	@Override
	public V get(final long timeout, final TimeUnit unit)
			throws InterruptedException, TimeoutException, ExecutionException {
		if (await(timeout, unit)) {
			final Throwable cause = cause();
			if (cause != null) {
				throw new ExecutionException(cause);
			}
			
			return getNow();
		}
		
		throw new TimeoutException();
	}
}
