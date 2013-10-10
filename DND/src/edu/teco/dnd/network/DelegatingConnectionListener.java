package edu.teco.dnd.network;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DelegatingConnectionListener implements ConnectionListener {
	private static final Logger LOGGER = LogManager.getLogger(DelegatingConnectionListener.class);
	
	private final AtomicReference<Set<ConnectionListener>> listeners =
			new AtomicReference<Set<ConnectionListener>>(Collections.<ConnectionListener> emptySet());
	
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
	public void connectionEstablished(final UUID uuid) {
		for (final ConnectionListener listener : listeners.get()) {
			LOGGER.debug("calling connectionEstablished({}) on {}", uuid, listener);
			try {
				listener.connectionEstablished(uuid);
			} catch (final Throwable t) {
				LOGGER.catching(t);
			}
		}
	}

	@Override
	public void connectionClosed(final UUID uuid) {
		for (final ConnectionListener listener : listeners.get()) {
			LOGGER.debug("calling connectionClosed({}) on {}", uuid, listener);
			try {
				listener.connectionClosed(uuid);
			} catch (final Throwable t) {
				LOGGER.catching(t);
			}
		}
	}
}
