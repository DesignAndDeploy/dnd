package edu.teco.dnd.network.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import edu.teco.dnd.util.DefaultFutureNotifier;
import edu.teco.dnd.util.FutureNotifier;

/**
 * This class can be used if a {@link ChannelFuture} exists, but a {@link FutureNotifier} is needed.
 */
public class ChannelFutureNotifierWrapper extends DefaultFutureNotifier<Void> implements ChannelFutureListener,
		ChannelFutureNotifier {
	private final Channel channel;

	public ChannelFutureNotifierWrapper(final ChannelFuture channelFuture) {
		channelFuture.addListener(this);
		this.channel = channelFuture.channel();
	}

	@Override
	public void operationComplete(final ChannelFuture future) {
		if (future.isSuccess()) {
			setSuccess(null);
		} else {
			setFailure(future.cause());
		}
	}

	@Override
	public Channel channel() {
		return channel;
	}
}
