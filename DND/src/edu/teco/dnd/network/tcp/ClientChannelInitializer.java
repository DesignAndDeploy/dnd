package edu.teco.dnd.network.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.EventExecutorGroup;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import edu.teco.dnd.network.messages.ConnectionEstablishedMessage;
import edu.teco.dnd.network.messages.HelloMessage;
import edu.teco.dnd.network.messages.Message;

public class ClientChannelInitializer extends ChannelInitializer<Channel> {
	public static final int LENGTH_FIELD_LENGTH = 2;

	public static final int MAX_FRAME_LENGTH = 512 * 1024;

	private final GsonCodec gsonCodec = new GsonCodec(Message.class);
	private final MessageAdapter messageAdapter = new MessageAdapter();
	private final List<ChannelHandler> defaultHandlers;
	private final Message firstMessage;

	private final ClientChannelManager clientChannelManager;

	private final AtomicReference<ChannelHandler> messageHandler = new AtomicReference<ChannelHandler>();
	private final AtomicReference<EventExecutorGroup> handlerGroup = new AtomicReference<EventExecutorGroup>();

	public ClientChannelInitializer(final ClientChannelManager clientChannelManager, final UUID localUUID) {
		this.clientChannelManager = clientChannelManager;

		final List<ChannelHandler> handlers = new ArrayList<ChannelHandler>();

		handlers.add(new LengthFieldPrepender(LENGTH_FIELD_LENGTH));

		final Charset charset = Charset.forName("UTF-8");
		handlers.add(new StringEncoder(charset));
		handlers.add(new StringDecoder(charset));

		messageAdapter.addMessageType(HelloMessage.class);
		messageAdapter.addMessageType(ConnectionEstablishedMessage.class);
		gsonCodec.registerTypeAdapter(Message.class, messageAdapter);
		handlers.add(gsonCodec);

		handlers.add(new HelloMessageHandler(clientChannelManager, localUUID));
		handlers.add(new ConnectionEstablishedMessageHandler(clientChannelManager, localUUID));

		defaultHandlers = Collections.unmodifiableList(handlers);

		firstMessage = new HelloMessage(localUUID, MAX_FRAME_LENGTH);
	}

	@Override
	public void initChannel(final Channel channel) {
		clientChannelManager.addChannel(channel);
		preparePipeline(channel);
		sendFirstMessage(channel);
	}

	private void sendFirstMessage(final Channel channel) {
		channel.writeAndFlush(firstMessage);
	}

	private void preparePipeline(final Channel channel) {
		ChannelPipeline pipeline = channel.pipeline();
		addDefaultHandlers(pipeline);
		addMessageHandlerIfPresent(pipeline);
	}

	private void addDefaultHandlers(ChannelPipeline pipeline) {
		pipeline.addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, 0, LENGTH_FIELD_LENGTH, 0, LENGTH_FIELD_LENGTH));
		for (final ChannelHandler handler : defaultHandlers) {
			pipeline.addLast(handler);
		}
	}

	private void addMessageHandlerIfPresent(ChannelPipeline pipeline) {
		final ChannelHandler messageHandler = this.messageHandler.get();
		if (messageHandler != null) {
			final EventExecutorGroup handlerGroup = this.handlerGroup.get();
			if (handlerGroup != null) {
				pipeline.addLast(handlerGroup, messageHandler);
			} else {
				pipeline.addLast(messageHandler);
			}
		}
	}

	public void setMessageHandler(final ChannelHandler messageHandler) {
		this.messageHandler.set(messageHandler);
	}

	public void setHandlerGroup(final EventExecutorGroup handlerGroup) {
		this.handlerGroup.set(handlerGroup);
	}

	public void registerTypeAdapter(final Type type, final Object adapter) {
		gsonCodec.registerTypeAdapter(type, adapter);
	}
	
	public void addMessageType(final Class<? extends Message> cls) {
		messageAdapter.addMessageType(cls);
	}
}
