package edu.teco.dnd.network.tcp.tests;

import static org.mockito.Mockito.when;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.teco.dnd.network.ChannelFutureNotifier;

public class MockChannel {
	@Mock
	private Channel channel;

	@Mock
	private ChannelFutureNotifier channelFutureNotifier;

	@Mock
	private ChannelFuture closeFuture;

	private ChannelFutureListenerHandler closeListenerHandler;

	public MockChannel() {
		MockitoAnnotations.initMocks(this);
		when(channelFutureNotifier.channel()).thenReturn(channel);
		when(channel.closeFuture()).thenReturn(closeFuture);
		when(closeFuture.channel()).thenReturn(channel);
		closeListenerHandler = new ChannelFutureListenerHandler(closeFuture);
	}

	public Channel getChannel() {
		return channel;
	}

	public ChannelFutureNotifier getChannelFutureNotifier() {
		return channelFutureNotifier;
	}

	public void close() throws Exception {
		closeFuture();
		notifyCloseFutureListeners();
	}

	private void closeFuture() {
		when(closeFuture.isSuccess()).thenReturn(true);
		when(closeFuture.isDone()).thenReturn(true);
	}

	private void notifyCloseFutureListeners() throws Exception {
		for (final GenericFutureListener<Future<Void>> listener : closeListenerHandler.getListeners()) {
			listener.operationComplete(closeFuture);
		}
	}
}
