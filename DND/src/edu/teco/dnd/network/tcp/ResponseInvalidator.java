package edu.teco.dnd.network.tcp;

import edu.teco.dnd.network.tcp.ResponseFutureManager.ResponseFutureNotifier;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * Notifies {@link ResponseFutureNotifier}s about failed writes.
 * 
 * @author Philipp Adolf
 */
public class ResponseInvalidator implements ChannelFutureListener {
	private final ResponseFutureNotifier responseFutureNotifier;
	
	public ResponseInvalidator(final ResponseFutureNotifier responseFutureNotifier) {
		this.responseFutureNotifier = responseFutureNotifier;
	}
	
	@Override
	public void operationComplete(final ChannelFuture future) {
		if (future.isDone() && !future.isSuccess()) {
			responseFutureNotifier.setFailure0(future.cause());
		}
	}
}
