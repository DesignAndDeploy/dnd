package edu.teco.dnd.network;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.ModuleID;

/**
 * A thread-safe implementation of {@link ConnectionListener} that will forward any events to a Set of
 * ConnectionListeners.
 */
public class DelegatingConnectionListener implements ConnectionListener {
	private static final Logger LOGGER = LogManager.getLogger(DelegatingConnectionListener.class);

	private final AtomicReference<Set<ConnectionListener>> listeners = new AtomicReference<Set<ConnectionListener>>(
			Collections.<ConnectionListener> emptySet());

	/**
	 * Adds a listener atomically. After this method returns <code>listener</code> will receive all events this
	 * DelegatingConnectionListener receives.
	 * 
	 * @param listener
	 *            the ConnectionListener to add
	 */
	public void addListener(final ConnectionListener listener) {
		Set<ConnectionListener> oldSet;
		Set<ConnectionListener> newSet;
		do {
			oldSet = listeners.get();
			newSet = new HashSet<ConnectionListener>(oldSet);

			newSet.add(listener);
		} while (!listeners.compareAndSet(oldSet, Collections.unmodifiableSet(newSet)));
		LOGGER.debug("added {}", listener);
	}

	/**
	 * Removes a listener atomically. Once this method returns <code>listener</code> will no longer receive events from
	 * this DelegatingConnectionListener.
	 * 
	 * @param listener
	 *            the ConnectionListener to remove
	 */
	public void removeListener(final ConnectionListener listener) {
		Set<ConnectionListener> oldSet;
		Set<ConnectionListener> newSet;
		do {
			oldSet = listeners.get();
			newSet = new HashSet<ConnectionListener>(oldSet);

			newSet.remove(listener);
		} while (!listeners.compareAndSet(oldSet, Collections.unmodifiableSet(newSet)));
		LOGGER.debug("removed {}", listener);
	}

	@Override
	public void connectionEstablished(final ModuleID moduleID) {
		for (final ConnectionListener listener : listeners.get()) {
			LOGGER.debug("calling connectionEstablished({}) on {}", moduleID, listener);
			try {
				listener.connectionEstablished(moduleID);
			} catch (final Throwable t) {
				LOGGER.catching(t);
			}
		}
	}

	@Override
	public void connectionClosed(final ModuleID moduleID) {
		for (final ConnectionListener listener : listeners.get()) {
			LOGGER.debug("calling connectionClosed({}) on {}", moduleID, listener);
			try {
				listener.connectionClosed(moduleID);
			} catch (final Throwable t) {
				LOGGER.catching(t);
			}
		}
	}
}
