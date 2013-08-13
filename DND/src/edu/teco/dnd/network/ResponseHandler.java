package edu.teco.dnd.network;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.util.DefaultFutureNotifier;

/**
 * Manages pending Futures for {@link Response}s.
 * 
 * @author Philipp Adolf
 */
public class ResponseHandler {
	/**
	 * Stores all pending Futures.
	 */
	private final ConcurrentMap<UUID, ResponseFutureNotifier> futures =
			new ConcurrentHashMap<UUID, ResponseFutureNotifier>();

	/**
	 * Returns the Future for the given Message UUID if one has already been created or creates a new one.
	 * 
	 * @param uuid
	 *            the UUID of the Message
	 * @return the FutureNotifier for the given Message
	 */
	public ResponseFutureNotifier getResponseFutureNotifier(final UUID uuid) {
		final ResponseFutureNotifier newNotifier = new ResponseFutureNotifier();
		final ResponseFutureNotifier oldNotifier = futures.putIfAbsent(uuid, newNotifier);
		if (oldNotifier == null) {
			return newNotifier;
		} else {
			return oldNotifier;
		}
	}

	/**
	 * Handles a response by marking the matching Future as successful.
	 * 
	 * @param response
	 *            the Response that should be handled
	 */
	public void handleResponse(final Response response) {
		final ResponseFutureNotifier notifier = futures.remove(response.getSourceUUID());
		if (notifier != null) {
			notifier.setSuccess(response);
		}
	}

	public void setFailed(UUID uuid, Throwable cause) {
		final ResponseFutureNotifier notifier = futures.remove(uuid);
		if (notifier != null) {
			notifier.setFailure(cause);
		}
	}

	/**
	 * A FutureNotifier used to handle Responses.
	 * 
	 * @author Philipp Adolf
	 */
	public static class ResponseFutureNotifier extends DefaultFutureNotifier<Response> {
		@Override
		protected boolean setSuccess(final Response response) {
			return super.setSuccess(response);
		}

		@Override
		protected boolean setFailure(final Throwable cause) {
			return super.setFailure(cause);
		}
	}
}
