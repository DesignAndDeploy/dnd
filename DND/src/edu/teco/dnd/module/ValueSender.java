package edu.teco.dnd.module;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.messages.values.BlockFoundResponse;
import edu.teco.dnd.module.messages.values.ValueMessage;
import edu.teco.dnd.module.messages.values.WhoHasBlockMessage;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.util.DefaultFutureNotifier;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;
import edu.teco.dnd.util.ReferenceIterator;

/**
 * Provides functionality to send values to a remote FunctionBlock. This includes searching for the FunctionBlock using
 * its UUID and buffering values until the Module on which the FunctionBlock is running is found. Values are cached
 * using SoftReferences so they may get garbage collected before the Module is found.
 * 
 * @author Philipp Adolf
 */
// TODO: What should be done if a value gets sent but a negative response is received?
public class ValueSender implements FutureListener<FutureNotifier<UUID>> {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ValueSender.class);

	/**
	 * The UUID of the Application the block belongs to.
	 */
	private final UUID appId;

	/**
	 * The UUID of the FunctionBlock this object sends values to.
	 */
	private final UUID targetBlockID;

	/**
	 * The ConnectionManager that will be used to send messages.
	 */
	private final ConnectionManager connectionManager;

	/**
	 * Values that are queued to be send once the right Module is found.
	 */
	private final List<Reference<TargetedValue>> pendingValues = new LinkedList<Reference<TargetedValue>>();

	/**
	 * The UUID of the Module that has the target FunctionBlock. null if unknown.
	 */
	private UUID moduleUUID = null;

	/**
	 * Is set to true if queries have been sent out and we're still waiting for the Reponses.
	 */
	private boolean queriesPending = false;

	/**
	 * Used to synchronize access to {@link #pendingValues}, {@link #moduleUUID} and {@link #queriesPending}.
	 */
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	/**
	 * Initializes a new ValueSender.
	 * 
	 * @param appId
	 *            the UUID of the Application
	 * @param targetBlockID
	 *            the UUID of the FunctionBlock this object should send values to
	 * @param connectionManager
	 *            the ConnectionManager that will be used to send messages
	 */
	public ValueSender(final UUID appId, final UUID targetBlockID, final ConnectionManager connectionManager) {
		this.appId = appId;
		this.targetBlockID = targetBlockID;
		this.connectionManager = connectionManager;
	}

	/**
	 * Sends a value to the FunctionBlock. If the Module the FunctionBlock is unknown the value is queued to be sent
	 * later.
	 * 
	 * @param targetInput
	 *            the input of the target block the value is for
	 * @param value
	 *            the value to send
	 */
	public void sendValue(final String targetInput, final Serializable value) {
		LOGGER.entry(targetInput, value);

		UUID uuid = null;
		lock.readLock().lock();
		try {
			uuid = moduleUUID;
		} finally {
			lock.readLock().unlock();
		}

		if (uuid == null) {
			lock.writeLock().lock();
			try {
				if (moduleUUID == null) {
					LOGGER.debug("no module UUID, adding value to pendingValues, starting query");
					pendingValues.add(new SoftReference<TargetedValue>(new TargetedValue(targetInput, value)));
					queryUUID();
					LOGGER.exit();
					return;
				} else {
					uuid = moduleUUID;
				}
			} finally {
				lock.writeLock().unlock();
			}
		}

		// this part will only be reached if the Module UUID was not null in either one of the locked blocks above
		sendValue(uuid, targetInput, value);
		LOGGER.exit();
	}

	/**
	 * Sends a ValueMessage to the given Module.
	 * 
	 * @param modUUID
	 *            the UUID of the Module the message should be send to
	 * @param targetInput
	 *            the name of the input of the FunctionBlock the value is for
	 * @param value
	 *            the value to send
	 */
	private void sendValue(final UUID modUUID, final String targetInput, final Serializable value) {
		try {
			LOGGER.trace("sending value {} to {}:{}", UsercodeWrapper.getToString(value), modUUID, targetInput);
		} catch (UserSuppliedCodeException e) {
			// ignore.
		}
		connectionManager.sendMessage(modUUID, new ValueMessage(appId, targetBlockID, targetInput, value));
	}

	/**
	 * Sends queries to all connected Modules if the Module UUID for the Module that has the target FunctionBlock is
	 * unknown and no queries are pending.
	 */
	private void queryUUID() {
		LOGGER.entry();
		lock.readLock().lock();
		try {
			if (moduleUUID != null || queriesPending) {
				LOGGER.exit();
				return;
			}
		} finally {
			lock.readLock().unlock();
		}

		lock.writeLock().lock();
		try {
			if (moduleUUID != null || queriesPending) {
				LOGGER.exit();
				return;
			}
			queriesPending = true;
		} finally {
			lock.writeLock().unlock();
		}

		LOGGER.debug("no query for {} pending, starting", targetBlockID);

		final Collection<UUID> modules = connectionManager.getConnectedModules();
		final ModuleUUIDFutureNotifier futureNotifier = new ModuleUUIDFutureNotifier(modules.size());
		futureNotifier.addListener(this);
		final WhoHasBlockResponseListener blockFoundResponseListener = new WhoHasBlockResponseListener(futureNotifier);
		for (final UUID modUUID : modules) {
			connectionManager.sendMessage(modUUID, new WhoHasBlockMessage(appId, targetBlockID)).addListener(
					blockFoundResponseListener);
		}

		LOGGER.exit();
	}

	/**
	 * This is called once the {@link ModuleUUIDFutureNotifier} finishes. Either sets {@link #moduleUUID} and sends all
	 * pending values or retries finding the Module UUID.
	 * 
	 * @param future
	 *            the FutureNotifier that finished
	 */
	@Override
	public void operationComplete(final FutureNotifier<UUID> future) {
		if (future.isSuccess()) {
			lock.writeLock().lock();
			try {
				moduleUUID = future.getNow();
				LOGGER.debug("found block {} on {}", targetBlockID, moduleUUID);
				sendPendingValues();
				queriesPending = false;
			} finally {
				lock.writeLock().unlock();
			}
		} else {
			LOGGER.info("did not find module for block {}, trying again", targetBlockID);
			// TODO: add some time between queries
			queryUUID();
		}
	}

	/**
	 * Sends all pending values that have not yet been garbage collected.
	 */
	private void sendPendingValues() {
		final Iterator<TargetedValue> iterator = new ReferenceIterator<TargetedValue>(pendingValues);
		while (iterator.hasNext()) {
			final TargetedValue value = iterator.next();
			sendValue(moduleUUID, value.getInputName(), value.getValue());
		}
		pendingValues.clear();
	}

	/**
	 * Used to track the status of the queries that were sent out. Finishes when either a positive Response (
	 * {@link BlockFoundResponse}) was received or all queries returned a negative result.
	 * 
	 * @author Philipp Adolf
	 */
	private static class ModuleUUIDFutureNotifier extends DefaultFutureNotifier<UUID> {
		/**
		 * The number of queries that have not yet finished.
		 */
		private final AtomicInteger unfinishedQueries;

		/**
		 * Initializes a new ModuleUUIDFutureNotifier.
		 * 
		 * @param queries
		 *            the number of queries sent out
		 */
		public ModuleUUIDFutureNotifier(final int queries) {
			this.unfinishedQueries = new AtomicInteger(queries);
			if (queries <= 0) {
				setFailure(null);
			}
		}

		/**
		 * This is called by {@link WhoHasBlockResponseListener} if a Module responded with a {@link BlockFoundResponse}
		 * . This marks this FutureNotifier as completed successfully with the given moduleUUID.
		 * 
		 * @param moduleUUID
		 *            the UUID of the Module containing the FunctionBlock
		 */
		public void queryFinishedSuccessfully(final UUID moduleUUID) {
			setSuccess(moduleUUID);
		}

		/**
		 * This is called by {@link WhoHasBlockResponseListener} if a query either couldn't be sent or a negative
		 * Response was received. If all queries fail this FutureNotifier is also set to be failed.
		 */
		public void queryFailed() {
			if (unfinishedQueries.decrementAndGet() <= 0) {
				setFailure(null);
			}
		}
	}

	/**
	 * This listener is used to react to the individual queries. It calls
	 * {@link ModuleUUIDFutureNotifier#queryFinishedSuccessfully(UUID)} and
	 * {@link ModuleUUIDFutureNotifier#queryFailed()} accordingly.
	 * 
	 * @author Philipp Adolf
	 */
	private static class WhoHasBlockResponseListener implements FutureListener<FutureNotifier<Response>> {
		/**
		 * The ModuleUUIDFutureNotifier that should be informed about the result of the queries.
		 */
		private final ModuleUUIDFutureNotifier moduleUUIDFutureNotifier;

		/**
		 * Initializes a new WhoHasBlockResponseListener.
		 * 
		 * @param moduleUUIDFutureNotifier
		 *            the ModuleUUIDFutureNotifier that should be informed about the results of the queries
		 */
		public WhoHasBlockResponseListener(final ModuleUUIDFutureNotifier moduleUUIDFutureNotifier) {
			this.moduleUUIDFutureNotifier = moduleUUIDFutureNotifier;
		}

		/**
		 * Either calls {@link ModuleUUIDFutureNotifier#queryFinishedSuccessfully(UUID)} or
		 * {@link ModuleUUIDFutureNotifier#queryFailed()} depending on the state of the future.
		 * 
		 * @param future
		 *            the FutureNotifier that finished
		 */
		@Override
		public void operationComplete(final FutureNotifier<Response> future) {
			LOGGER.entry(future);
			boolean success = false;
			if (future.isSuccess()) {
				final Response response = future.getNow();
				LOGGER.trace("future {} was a success and returned {}", future, response);
				LOGGER.trace(response.getClass());
				if (response instanceof BlockFoundResponse) {
					final UUID moduleUUID = ((BlockFoundResponse) response).moduleId;
					if (moduleUUID != null) {
						LOGGER.debug("future {} returned UUID {}", moduleUUID);
						moduleUUIDFutureNotifier.queryFinishedSuccessfully(moduleUUID);
						success = true;
					}
				}
			}
			if (!success) {
				LOGGER.debug("future {} failed", future);
				moduleUUIDFutureNotifier.queryFailed();
			}
			LOGGER.exit();
		}
	}
}
