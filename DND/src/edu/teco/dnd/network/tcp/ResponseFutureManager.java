package edu.teco.dnd.network.tcp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.util.DefaultFutureNotifier;

public class ResponseFutureManager {
	private static Logger LOGGER = LogManager.getLogger();
	
	private final Map<UUID, ResponseFutureNotifier> responseFutureNotifier = new HashMap<UUID, ResponseFutureNotifier>();
	
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
	
	public class ResponseFutureNotifier extends DefaultFutureNotifier<Response> {
		private final UUID sourceUUID;
		
		protected ResponseFutureNotifier(final UUID sourceUUID) {
			this.sourceUUID = sourceUUID;
		}
		
		public UUID getSourceUUID() {
			return sourceUUID;
		}
		
		protected void setSuccess0(final Response response) {
			setSuccess(response);
		}
		
		protected void setFailure0(final Throwable cause) {
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
