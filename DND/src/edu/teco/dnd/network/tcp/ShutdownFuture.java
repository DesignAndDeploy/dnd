package edu.teco.dnd.network.tcp;

import edu.teco.dnd.util.DefaultFutureNotifier;

/**
 * A future that is used by {@link TCPConnectionManager} to signal when it has finished shutting down.
 * 
 * @author Philipp Adolf
 */
public class ShutdownFuture extends DefaultFutureNotifier<Void> {
	/**
	 * Called by {@link TCPConnectionManager} when it has shut down.
	 */
	void setComplete() {
		setSuccess(null);
	}
}
