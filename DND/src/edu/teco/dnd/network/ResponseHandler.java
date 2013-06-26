package edu.teco.dnd.network;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.util.DefaultFutureNotifier;

public class ResponseHandler {
	private final ConcurrentMap<UUID, ResponseFutureNotifier> futures = new ConcurrentHashMap<UUID, ResponseFutureNotifier>();
	
	public ResponseFutureNotifier getResponseFutureNotifier(final UUID uuid) {
		final ResponseFutureNotifier newNotifier = new ResponseFutureNotifier();
		final ResponseFutureNotifier oldNotifier = futures.putIfAbsent(uuid, newNotifier);
		if (oldNotifier == null) {
			return newNotifier;
		} else {
			return oldNotifier;
		}
	}
	
	public void handleResponse(final Response response) {
		final ResponseFutureNotifier notifier = futures.remove(response.getSourceUUID());
		if (notifier != null) {
			notifier.setSuccess(response);
		}
	}
	
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
