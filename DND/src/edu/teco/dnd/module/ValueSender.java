package edu.teco.dnd.module;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.FunctionBlockID;
import edu.teco.dnd.blocks.Input;
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
 * Provides functionality to send values to a remote {@link FunctionBlock}. This includes searching for the
 * FunctionBlock using its {@link ModuleID} and buffering values until the {@link Module} on which the FunctionBlock is
 * running is found. Values are cached using {@link SoftReference}s so they may get garbage collected before the Module
 * is found if RAM is needed.
 */
// TODO: What should be done if a value gets sent but a negative response is received?
public class ValueSender implements FutureListener<FutureNotifier<ModuleID>> {
	private static final Logger LOGGER = LogManager.getLogger(ValueSender.class);

	private final ApplicationID applicationID;
	private final FunctionBlockID targetBlockID;
	private final ConnectionManager connectionManager;

	private final List<Reference<TargetedValue>> pendingValues = new LinkedList<Reference<TargetedValue>>();

	private ModuleID moduleID = null;
	private boolean queriesPending = false;

	/**
	 * Used to synchronize access to {@link #pendingValues}, {@link #moduleID} and {@link #queriesPending}.
	 */
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	/**
	 * Initializes a new ValueSender.
	 * 
	 * @param applicationID
	 *            the ID of the Application
	 * @param targetBlockID
	 *            the ID of the FunctionBlock this object should send values to
	 * @param connectionManager
	 *            the ConnectionManager that will be used to send messages
	 */
	public ValueSender(final ApplicationID applicationID, final FunctionBlockID targetBlockID,
			final ConnectionManager connectionManager) {
		this.applicationID = applicationID;
		this.targetBlockID = targetBlockID;
		this.connectionManager = connectionManager;
	}

	/**
	 * Sends a value to the {@link FunctionBlock}. If the {@link Module} the FunctionBlock is on is unknown the value is
	 * queued to be sent later.
	 * 
	 * @param targetInput
	 *            the {@link Input} of the target FunctionBlock the value is for
	 * @param value
	 *            the value to send
	 */
	public void sendValue(final String targetInput, final Serializable value) {
		LOGGER.entry(targetInput, value);

		ModuleID id = null;
		lock.readLock().lock();
		try {
			id = moduleID;
		} finally {
			lock.readLock().unlock();
		}

		if (id == null) {
			lock.writeLock().lock();
			try {
				if (moduleID == null) {
					LOGGER.debug("no ModuleID, adding value to pendingValues, starting query");
					pendingValues.add(new SoftReference<TargetedValue>(new TargetedValue(targetInput, value)));
					queryID();
					LOGGER.exit();
					return;
				} else {
					id = moduleID;
				}
			} finally {
				lock.writeLock().unlock();
			}
		}

		// this part will only be reached if the ModuleID was not null in either one of the locked blocks above
		sendValue(id, targetInput, value);
		LOGGER.exit();
	}

	/**
	 * Sends a {@link ValueMessage} to the given {@link Module}.
	 * 
	 * @param moduleID
	 *            the ID of the Module the message should be send to
	 * @param targetInput
	 *            the name of the {@link Input} of the {@link FunctionBlock} the value is for
	 * @param value
	 *            the value to send
	 */
	private void sendValue(final ModuleID moduleID, final String targetInput, final Serializable value) {
		if (LOGGER.isTraceEnabled()) {
			try {
				LOGGER.trace("sending value {} to {}:{}", UsercodeWrapper.getToString(value), moduleID, targetInput);
			} catch (UserSuppliedCodeException e) {
				// ignore.
			}
		}
		connectionManager.sendMessage(moduleID, new ValueMessage(applicationID, targetBlockID, targetInput, value));
	}

	/**
	 * Sends queries to all connected {@link Module}s if the {@link ModuleID} for the Module that has the target
	 * {@link FunctionBlock} is unknown and no queries are pending.
	 */
	private void queryID() {
		LOGGER.entry();
		lock.readLock().lock();
		try {
			if (moduleID != null || queriesPending) {
				LOGGER.exit();
				return;
			}
		} finally {
			lock.readLock().unlock();
		}

		lock.writeLock().lock();
		try {
			if (moduleID != null || queriesPending) {
				LOGGER.exit();
				return;
			}
			queriesPending = true;
		} finally {
			lock.writeLock().unlock();
		}

		LOGGER.debug("no query for {} pending, starting", targetBlockID);

		final Collection<ModuleID> modules = connectionManager.getConnectedModules();
		final ModuleIDFutureNotifier futureNotifier = new ModuleIDFutureNotifier(modules.size());
		futureNotifier.addListener(this);
		final WhoHasBlockResponseListener blockFoundResponseListener = new WhoHasBlockResponseListener(futureNotifier);
		for (final ModuleID id : modules) {
			connectionManager.sendMessage(id, new WhoHasBlockMessage(applicationID, targetBlockID)).addListener(
					blockFoundResponseListener);
		}

		LOGGER.exit();
	}

	/**
	 * This is called once the {@link ModuleIDFutureNotifier} finishes. Either sets {@link #moduleID} and sends all
	 * pending values or retries finding the ModuleID.
	 * 
	 * @param future
	 *            the FutureNotifier that finished
	 */
	@Override
	public void operationComplete(final FutureNotifier<ModuleID> future) {
		if (future.isSuccess()) {
			lock.writeLock().lock();
			try {
				moduleID = future.getNow();
				LOGGER.debug("found block {} on {}", targetBlockID, moduleID);
				sendPendingValues();
				queriesPending = false;
			} finally {
				lock.writeLock().unlock();
			}
		} else {
			LOGGER.info("did not find module for block {}, trying again", targetBlockID);
			// TODO: add some time between queries
			queryID();
		}
	}

	/**
	 * Sends all pending values that have not yet been garbage collected.
	 */
	private void sendPendingValues() {
		final Iterator<TargetedValue> iterator = new ReferenceIterator<TargetedValue>(pendingValues);
		while (iterator.hasNext()) {
			final TargetedValue value = iterator.next();
			sendValue(moduleID, value.getInputName(), value.getValue());
		}
		pendingValues.clear();
	}

	/**
	 * Used to track the status of the queries that were sent out. Finishes when either a positive Response (
	 * {@link BlockFoundResponse}) was received or all queries returned a negative result.
	 * 
	 * @author Philipp Adolf
	 */
	private static class ModuleIDFutureNotifier extends DefaultFutureNotifier<ModuleID> {
		/**
		 * The number of queries that have not yet finished.
		 */
		private final AtomicInteger unfinishedQueries;

		/**
		 * Initializes a new ModuleIDFutureNotifier.
		 * 
		 * @param queries
		 *            the number of queries sent out
		 */
		public ModuleIDFutureNotifier(final int queries) {
			this.unfinishedQueries = new AtomicInteger(queries);
			if (queries <= 0) {
				setFailure(null);
			}
		}

		/**
		 * This is called by {@link WhoHasBlockResponseListener} if a ModuleInfo responded with a
		 * {@link BlockFoundResponse} . This marks this FutureNotifier as completed successfully with the given
		 * moduleID.
		 * 
		 * @param moduleID
		 *            the ID of the Module containing the FunctionBlock
		 */
		public void queryFinishedSuccessfully(final ModuleID moduleID) {
			setSuccess(moduleID);
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
	 * {@link ModuleIDFutureNotifier#queryFinishedSuccessfully(ModuleID)} and
	 * {@link ModuleIDFutureNotifier#queryFailed()} accordingly.
	 * 
	 * @author Philipp Adolf
	 */
	private static class WhoHasBlockResponseListener implements FutureListener<FutureNotifier<Response>> {
		/**
		 * The ModuleIDFutureNotifier that should be informed about the result of the queries.
		 */
		private final ModuleIDFutureNotifier moduleIDFutureNotifier;

		/**
		 * Initializes a new WhoHasBlockResponseListener.
		 * 
		 * @param moduleIDFutureNotifier
		 *            the ModuleIDFutureNotifier that should be informed about the results of the queries
		 */
		public WhoHasBlockResponseListener(final ModuleIDFutureNotifier moduleIDFutureNotifier) {
			this.moduleIDFutureNotifier = moduleIDFutureNotifier;
		}

		/**
		 * Either calls {@link ModuleIDFutureNotifier#queryFinishedSuccessfully(ModuleID)} or
		 * {@link ModuleIDFutureNotifier#queryFailed()} depending on the state of the future.
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
					final ModuleID moduleID = ((BlockFoundResponse) response).moduleId;
					if (moduleID != null) {
						LOGGER.debug("future {} returned ID {}", moduleID);
						moduleIDFutureNotifier.queryFinishedSuccessfully(moduleID);
						success = true;
					}
				}
			}
			if (!success) {
				LOGGER.debug("future {} failed", future);
				moduleIDFutureNotifier.queryFailed();
			}
			LOGGER.exit();
		}
	}
}
