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
import java.util.concurrent.atomic.AtomicReference;

import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.network.messages.ConnectionEstablishedMessage;
import edu.teco.dnd.network.messages.HelloMessage;
import edu.teco.dnd.network.messages.Message;

/**
 * <p>
 * Initializes the Channel's pipeline, adds it to a {@link ClientChannelManager} and sends a {@link HelloMessage}.
 * <p>
 * 
 * <p>
 * When a new channel is becoming active this initializer will set up its pipeline for use with
 * {@link TCPConnectionManager}. This pipeline will consist of:
 * 
 * <ul>
 * <li>a {@link LengthFieldBasedFrameDecoder}</li>
 * <li>a {@link LengthFieldPrepender}</li>
 * <li>a {@link StringEncoder}</li>
 * <li>a {@link StringDecoder}</li>
 * <li>a {@link MessageAdapter}</li>
 * <li>a {@link GsonCodec}</li>
 * <li>a {@link HelloMessageHandler}</li>
 * <li>a {@link ConnectionEstablishedMessage}</li>
 * <li>an additional handler if set with {@link #setMessageHandler(ChannelHandler)}</li>
 * </ul>
 * </p>
 * 
 * <p>
 * The channel will also be registered at a ClientChannelManager and a HelloMessage is sent.
 * 
 * @author Philipp Adolf
 */
public class ClientChannelInitializer extends ChannelInitializer<Channel> {
	/**
	 * Number of bytes that are used for the length field.
	 */
	public static final int LENGTH_FIELD_LENGTH = 2;

	/**
	 * Maximum length a single Message can be to successfully receive it.
	 */
	public static final int MAX_FRAME_LENGTH = 512 * 1024;

	private final GsonCodec gsonCodec = new GsonCodec(Message.class);
	private final MessageAdapter messageAdapter = new MessageAdapter();
	private final List<ChannelHandler> defaultHandlers;
	private final Message firstMessage;

	private final ClientChannelManager clientChannelManager;

	private final AtomicReference<ChannelHandler> messageHandler = new AtomicReference<ChannelHandler>();
	private final AtomicReference<EventExecutorGroup> handlerGroup = new AtomicReference<EventExecutorGroup>();

	/**
	 * Initializes a new ClientChannelInitializer.
	 * 
	 * @param clientChannelManager
	 *            a ClientChannelManager that will be used by the {@link HelloMessageHandler}, the
	 *            {@link ConnectionEstablishedMessageHandler} and for registering new Channels
	 * @param localID
	 *            the ModuleID of the client this initializer is running on
	 */
	public ClientChannelInitializer(final ClientChannelManager clientChannelManager, final ModuleID localID) {
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

		handlers.add(new HelloMessageHandler(clientChannelManager, localID));
		handlers.add(new ConnectionEstablishedMessageHandler(clientChannelManager, localID));

		defaultHandlers = Collections.unmodifiableList(handlers);

		firstMessage = new HelloMessage(localID, MAX_FRAME_LENGTH);
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
		pipeline.addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, 0, LENGTH_FIELD_LENGTH, 0,
				LENGTH_FIELD_LENGTH));
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

	/**
	 * Sets an additional MessageHandler that will be added to the pipeline of new Channels.
	 * 
	 * @param messageHandler the MessageHandler to add to the pipelines
	 */
	public void setMessageHandler(final ChannelHandler messageHandler) {
		this.messageHandler.set(messageHandler);
	}

	/**
	 * Sets the executor group that should be used to run the additional handler
	 * 
	 * @param handlerGroup the group to use for the additional handler
	 */
	public void setHandlerGroup(final EventExecutorGroup handlerGroup) {
		this.handlerGroup.set(handlerGroup);
	}

	/**
	 * Registers a type adapter for the pipelines.
	 * 
	 * This also works after a pipeline has been created as the {@link GsonCodec} is shared between all pipelines.
	 * 
	 * @param type the type the adapter should be used for
	 * @param adapter the adapter to register
	 * @see GsonCodec#registerTypeAdapter(Type, Object)
	 */
	public void registerTypeAdapter(final Type type, final Object adapter) {
		gsonCodec.registerTypeAdapter(type, adapter);
	}

	/**
	 * Adds a Message type.
	 * 
	 * This also works after a pipeline has been created as the {@link MessageAdapter} is shared between all pipelines.
	 * This must be called before Messages of the given type are sent or received.
	 * 
	 * @param cls the Message class to register
	 * @see MessageAdapter#addMessageType(Class)
	 */
	public void addMessageType(final Class<? extends Message> cls) {
		messageAdapter.addMessageType(cls);
	}
}
