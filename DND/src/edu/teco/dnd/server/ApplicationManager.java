package edu.teco.dnd.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.messages.generalModule.MissingApplicationNak;
import edu.teco.dnd.module.messages.infoReq.ApplicationInformationResponse;
import edu.teco.dnd.module.messages.infoReq.RequestApplicationInformationMessage;
import edu.teco.dnd.module.messages.killApp.KillAppAck;
import edu.teco.dnd.module.messages.killApp.KillAppMessage;
import edu.teco.dnd.module.messages.killApp.KillAppNak;
import edu.teco.dnd.network.ConnectionListener;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.util.DefaultFutureNotifier;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;
import edu.teco.dnd.util.JoinedFutureNotifier;

/**
 * Collects information about running Applications and informs {@link ApplicationManagerListener}s about them.
 * 
 * @author Philipp Adolf
 */
public class ApplicationManager implements ServerStateListener, ConnectionListener {
	private static final Logger LOGGER = LogManager.getLogger();

	private ConnectionManager connectionManager;
	private final Map<UUID, ApplicationInformation> applications = new HashMap<UUID, ApplicationInformation>();
	private final Collection<ApplicationManagerListener> listeners = new ArrayList<ApplicationManagerListener>();
	private final Set<UUID> knownModules = new HashSet<UUID>();

	/**
	 * Initializes a new ApplicationManager. Normally you should just use {@link ServerManager#getApplicationManager()}
	 * to get an ApplicationManager instead of creating your own.
	 * 
	 * @param serverManager
	 *            the ServerManager that should be used
	 */
	public ApplicationManager(final ServerManager<?> serverManager) {
		LOGGER.entry(serverManager);
		serverManager.addServerStateListener(this);
		LOGGER.exit();
	}

	/**
	 * Adds a listener. A listener can be added multiple times, resulting in it getting all events multiple times.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public synchronized void addApplicationListener(final ApplicationManagerListener listener) {
		LOGGER.entry(listener);
		listeners.add(listener);

		listener.applicationsResolved(getApplications());
		LOGGER.exit();
	}

	/**
	 * Removes a listener. If a listener was added multiple times this method will have to be called the same number of
	 * times.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public synchronized void removeApplicationListener(final ApplicationManagerListener listener) {
		listeners.remove(listener);
	}

	@Override
	public synchronized void serverStateChanged(final ServerState state, final ConnectionManager connectionManager,
			final UDPMulticastBeacon beacon) {
		LOGGER.entry(state, connectionManager, beacon);
		switch (state) {
		case STOPPING:
			if (this.connectionManager != null) {
				LOGGER.debug("deregistering from {}", this.connectionManager);
				this.connectionManager.removeConnectionListener(this);
			}
			// fall-through
		case STOPPED:
			LOGGER.debug("clearing connectionManager, applications and knownModules");
			this.connectionManager = null;
			applications.clear();
			break;

		case RUNNING:
			LOGGER.debug("registering with {}", connectionManager);
			this.connectionManager = connectionManager;
			connectionManager.addConnectionListener(this);
		}
	}

	@Override
	public synchronized void connectionEstablished(final UUID uuid) {
		LOGGER.trace("adding Module {}", uuid);
		knownModules.add(uuid);
	}

	@Override
	public synchronized void connectionClosed(final UUID uuid) {
		LOGGER.trace("removing Module {}", uuid);
		knownModules.remove(uuid);
	}

	/**
	 * Returns a list of currently running Applications, represented by ApplicationInformation.
	 * 
	 * @return List of currently running applications.
	 */
	public synchronized Collection<ApplicationInformation> getApplications() {
		return new ArrayList<ApplicationInformation>(applications.values());
	}

	/**
	 * Kills an application. You should run an update beforehand or the information used to kill the Application may be
	 * too old.
	 * 
	 * @param applicationID
	 *            the ID of the Application to kill. Must be known to this manager.
	 * @return a FutureNotifier that completes successfully iff the Application is killed
	 */
	public FutureNotifier<Void> killApplication(final UUID applicationID) {
		ApplicationInformation applicationInformation = null;
		synchronized (this) {
			applicationInformation = applications.get(applicationID);
		}
		if (applicationInformation == null) {
			throw new IllegalArgumentException("Application " + applicationID + " unknown");
		}

		if (connectionManager == null) {
			throw new IllegalStateException("server not running");
		}

		final Collection<FutureNotifier<? extends Response>> futures =
				new ArrayList<FutureNotifier<? extends Response>>();
		for (final UUID moduleID : applicationInformation.getModules()) {
			final KillAppMessage killAppMessage = new KillAppMessage(applicationID);
			futures.add(connectionManager.sendMessage(moduleID, killAppMessage));
		}
		final JoinedFutureNotifier<Response> joinedFutureNotifier = new JoinedFutureNotifier<Response>(futures);
		final KillAppFutureNotifier notifier = new KillAppFutureNotifier();
		joinedFutureNotifier.addListener(notifier);
		return notifier;
	}

	/**
	 * Updates the list of running Applications.
	 * 
	 * @return a FutureNotifier that will return the Applications that were found
	 */
	public FutureNotifier<Collection<ApplicationInformation>> update() {
		final Collection<FutureNotifier<? extends Response>> futures =
				new ArrayList<FutureNotifier<? extends Response>>(knownModules.size());
		synchronized (this) {
			for (final UUID moduleID : knownModules) {
				futures.add(connectionManager.sendMessage(moduleID, new RequestApplicationInformationMessage()));
			}
		}
		final ApplicationInformationFutureNotifier applicationInformationFutureNotifier =
				new ApplicationInformationFutureNotifier();
		new JoinedFutureNotifier<Response>(futures).addListener(applicationInformationFutureNotifier);
		applicationInformationFutureNotifier
				.addListener(new FutureListener<FutureNotifier<Collection<ApplicationInformation>>>() {
					@Override
					public void operationComplete(final FutureNotifier<Collection<ApplicationInformation>> future) {
						if (future.isSuccess()) {
							applicationsFound(future.getNow());
						}
					}
				});
		return applicationInformationFutureNotifier;
	}

	private synchronized void applicationsFound(final Collection<ApplicationInformation> applications) {
		this.applications.clear();
		for (final ApplicationInformation application : applications) {
			this.applications.put(application.getID(), application);
		}

		for (final ApplicationManagerListener listener : listeners) {
			try {
				listener.applicationsResolved(applications);
			} catch (final Throwable t) {
				LOGGER.catching(Level.WARN, t);
			}
		}
	}

	/**
	 * A FutureNotifier that checks the Responses sent for kill requests.
	 * 
	 * @author Philipp Adolf
	 */
	private static class KillAppFutureNotifier extends DefaultFutureNotifier<Void> implements
			FutureListener<FutureNotifier<Collection<Response>>> {
		@Override
		public void operationComplete(final FutureNotifier<Collection<Response>> future) {
			if (future.isSuccess()) {
				for (final Response response : future.getNow()) {
					if (response instanceof KillAppAck || response instanceof MissingApplicationNak) {
						continue;
					} else if (response instanceof KillAppNak) {
						setFailure(new Exception("got negative acknowledgment"));
						return;
					} else {
						setFailure(null);
						return;
					}
				}

				setSuccess(null);
			} else {
				setFailure(future.cause());
			}
		}
	}

	/**
	 * A FutureNotifier that combines a Collection of {@link ApplicationInformationResponse}s to a Collection of
	 * ApplicationInformations.
	 * 
	 * @author Philipp Adolf
	 */
	private class ApplicationInformationFutureNotifier extends
			DefaultFutureNotifier<Collection<ApplicationInformation>> implements
			FutureListener<FutureNotifier<Collection<Response>>> {
		@Override
		public void operationComplete(final FutureNotifier<Collection<Response>> future) {
			LOGGER.entry(future);
			if (future.isSuccess()) {
				final Map<UUID, ApplicationInformation> applications = new HashMap<UUID, ApplicationInformation>();

				for (final Response response : future.getNow()) {
					LOGGER.trace("Handling Response {}", response);
					if (!(response instanceof ApplicationInformationResponse)) {
						LOGGER.warn("got {} instead of an ApplicationInformationResponse", response);
						continue;
					}

					final ApplicationInformationResponse applicationListResponse =
							(ApplicationInformationResponse) response;
					for (ApplicationInformation application : applicationListResponse.getApplications()) {
						LOGGER.trace("Adding application {}", application);
						final UUID applicationID = application.getID();
						final ApplicationInformation oldApplicationInformation = applications.get(applicationID);
						if (oldApplicationInformation != null) {
							LOGGER.trace("merging {} with {}", application, oldApplicationInformation);
							application = application.combine(oldApplicationInformation);
						}
						applications.put(applicationID, application);
					}
				}

				final Collection<ApplicationInformation> unmodifiableApplications =
						Collections.unmodifiableCollection(applications.values());
				LOGGER.debug("setting success ({})", unmodifiableApplications);
				setSuccess(unmodifiableApplications);
			} else {
				final Throwable cause = cause();
				LOGGER.debug("failed: {}", cause);
				setFailure(cause);
			}
			LOGGER.exit();
		}
	}
}
