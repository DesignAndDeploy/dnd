package edu.teco.dnd.network.tcp.tests;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.when;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * This class can be used to mock {@link ChannelFuture}'s addListener(s) and removeListener(s). It will keep a list of
 * listeners that are currently registered.
 * 
 * @author Philipp Adolf
 * 
 */
public class ChannelFutureListenerHandler {
	private final Set<GenericFutureListener<Future<Void>>> listeners =
			new HashSet<GenericFutureListener<Future<Void>>>();

	@SuppressWarnings("unchecked")
	public ChannelFutureListenerHandler(final ChannelFuture channelFuture) {
		when(channelFuture.addListener(any(GenericFutureListener.class))).then(addListeners());
		when(channelFuture.addListeners((GenericFutureListener<? extends Future<? super Void>>[]) anyVararg())).then(
				addListeners());
		when(channelFuture.removeListener(any(GenericFutureListener.class))).then(removeListeners());
		when(channelFuture.removeListeners((GenericFutureListener<? extends Future<? super Void>>[]) anyVararg()))
				.then(removeListeners());
	}

	private Answer<Void> addListeners() {
		return new AnswerAddListeners();
	}

	private Answer<Void> removeListeners() {
		return new AnswerRemoveListeners();
	}

	private class AnswerAddListeners implements Answer<Void> {
		@SuppressWarnings("unchecked")
		@Override
		public Void answer(final InvocationOnMock invocation) throws Throwable {
			for (final Object listener : invocation.getArguments()) {
				listeners.add((GenericFutureListener<Future<Void>>) listener);
			}
			return null;
		}
	}

	private class AnswerRemoveListeners implements Answer<Void> {
		@Override
		public Void answer(final InvocationOnMock invocation) throws Throwable {
			for (final Object listener : invocation.getArguments()) {
				listeners.remove(listener);
			}
			return null;
		}
	}

	public Set<GenericFutureListener<Future<Void>>> getListeners() {
		return Collections.unmodifiableSet(listeners);
	}
}