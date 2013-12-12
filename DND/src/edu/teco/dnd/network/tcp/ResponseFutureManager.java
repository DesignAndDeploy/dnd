package edu.teco.dnd.network.tcp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.util.DefaultFutureNotifier;

/**
 * <p>Creates and updates {@link ResponseFutureNotifier}s.</p>
 * 
 * @author Philipp Adolf
 */
public class ResponseFutureManager {
	private static Logger LOGGER = LogManager.getLogger();
	
	private final Map<UUID, ResponseFutureNotifier> responseFutureNotifier = new HashMap<UUID, ResponseFutureNotifier>();
	
	/**
	 * Creates a new ResponseFutureNotifier for a given Message UUID.
	 * 
	 * @param sourceUUID the UUID of the Message the ResponseFutureNotifier should be for
	 * @return a ResponseFutureNotifier for the given Message UUID
	 * @throws IllegalArgumentException if <code>sourceUUID</code> is null
	 * @throws IllegalStateException if there already is a ResponseFutureNotifier for the given UUID
	 */
	public ResponseFutureNotifier createResponseFuture(final UUID sourceUUID) {
		LOGGER.entry(sourceUUID);
		if (sourceUUID == null) {
			throw LOGGER.throwing(new IllegalArgumentException("sourceUUID must not be null"));
		}
		final ResponseFutureNotifier newNotifier = new ResponseFutureNotifier(sourceUUID);
		synchronized (responseFutureNotifier) {
			if (responseFutureNotifier.containsKey(sourceUUID)) {
				throw LOGGER.throwing(new IllegalStateException("ResponseFutureNotifier for " + sourceUUID + " already exists"));
			}
			responseFutureNotifier.put(sourceUUID, newNotifier);
		}
		return LOGGER.exit(newNotifier);
	}
	
	/**
	 * Sets the state of the matching ResponseFutureNotifier to success.
	 * 
	 * @param response the Response that was received
	 */
	public void setSuccess(final Response response) {
		LOGGER.entry(response);
		final UUID sourceUUID = response.getSourceUUID();
		ResponseFutureNotifier notifier = null;
		synchronized (responseFutureNotifier) {
			notifier = responseFutureNotifier.remove(sourceUUID);
		}
		if (notifier != null) {
			notifier.setSuccess0(response);
		}
		LOGGER.exit();
	}
	
	/**
	 * Sets the state of the ResponseFutureNotifier for the given UUID to failure.
	 * 
	 * @param sourceUUID the UUID for which no Response will be received
	 * @param cause the cause for the failure
	 */
	public void setFailure(final UUID sourceUUID, final Throwable cause) {
		LOGGER.entry(sourceUUID, cause);
		ResponseFutureNotifier notifier = null;
		synchronized (responseFutureNotifier) {
			notifier = responseFutureNotifier.remove(sourceUUID);
		}
		if (notifier != null) {
			notifier.setFailure0(cause);
		}
		LOGGER.exit();
	}
	
	/**
	 * A FutureNotifier that will return a Response.
	 * 
	 * @author Philipp Adolf
	 */
	public class ResponseFutureNotifier extends DefaultFutureNotifier<Response> {
		private final UUID sourceUUID;
		
		protected ResponseFutureNotifier(final UUID sourceUUID) {
			this.sourceUUID = sourceUUID;
		}
		
		/**
		 * Returns the UUID of the Message the Response is for.
		 * 
		 * @return the UUID of the Message the Response is for
		 */
		public UUID getSourceUUID() {
			return sourceUUID;
		}
		
		public void setSuccess0(final Response response) {
			setSuccess(response);
		}
		
		public void setFailure0(final Throwable cause) {
			setFailure(cause);
		}
		
		@Override
		public String toString() {
			String description = "ResponseFutureNotifer[sourceUUID" + sourceUUID;
			if (isSuccess()) {
				description += ",result=" + getNow();
			} else if (cause() != null) {
				description += ",cause=" + cause();
			}
			description += "]";
			return description;
		}
	}
}
