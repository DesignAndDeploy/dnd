package edu.teco.dnd.network.tcp;

import edu.teco.dnd.util.DefaultFutureNotifier;

public class ShutdownFuture extends DefaultFutureNotifier<Void> {
	void setComplete() {
		setSuccess(null);
	}
}
