package edu.teco.dnd.network;

import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutorGroup;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.teco.dnd.network.codecs.GsonCodec;
import edu.teco.dnd.network.codecs.MessageAdapter;
import edu.teco.dnd.network.messages.ApplicationSpecificMessage;
import edu.teco.dnd.network.messages.BeaconMessage;
import edu.teco.dnd.network.messages.ConnectionClosedMessage;
import edu.teco.dnd.network.messages.ConnectionEstablishedMessage;
import edu.teco.dnd.network.messages.HelloMessage;
import edu.teco.dnd.network.messages.Message;

/**
 * An implementation of ConnectionManager that uses TCP connections and JSON for communication.
 * 
 * @author Philipp Adolf
 */
public class TCPConnectionManager implements ConnectionManager, BeaconListener {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(TCPConnectionManager.class);
	
	/**
	 * Default maximum size of a frame that can be received.
	 */
	public static final int DEFAULT_MAX_FRAME_LENGTH = 512 * 1024;
	
	/**
	 * The charset to use (UTF-8).
	 */
	public static final Charset CHARSET = Charset.forName("UTF-8");
	
	public static final AttributeKey<UUID> REMOTE_UUID_KEY = new AttributeKey<UUID>("remoteUUID");
	
	/**
	 * Contains all active server channels.
	 */
	private final Set<ServerSocketChannel> serverChannels = new HashSet<ServerSocketChannel>();
	
	// TODO: a list of channels that have been created but didn't complete the handshake yet is probably needed
	/**
	 * Contains all client channels with an established connection.
	 */
	private final Map<UUID, Channel> clientChannels = new HashMap<UUID, Channel>();
	
	/**
	 * Lock used for synchronizing access to {@link #clientChannels}.
	 */
	private final ReadWriteLock clientChannelsLock = new ReentrantReadWriteLock();
	
	/**
	 * Handlers for given application IDs.
	 */
	private final Map<UUID, MessageHandler> handlers = new HashMap<UUID, MessageHandler>();
	
	/**
	 * Lock for reading/writing to {@link #handlers}.
	 */
	private final ReadWriteLock handlersLock = new ReentrantReadWriteLock();
	
	/**
	 * Factory used to connect to other TCPConnectionManagers.
	 */
	private final TCPClientChannelFactory clientFactory;
	
	/**
	 * Factory used to create new server channels.
	 */
	private final TCPServerChannelFactory serverFactory;
	
	/**
	 * Gson adapter for messages.
	 */
	private final MessageAdapter messageAdapter;
	
	/**
	 * The UUID of the module this TCPConnectionManager is running on.
	 */
	private final UUID localUUID;
	
	/**
	 * Creates a new TCPConnectionManager.
	 * 
	 * @param networkEventLoopGroup the EventLoopGroup to use for server channels
	 * @param applicationExecutor the EventLoopGroup to use for client channels
	 * @param serverChannelFactory a factory for new server channels
	 * @param clientChannelFactory a factory for new client channels
	 * @param uuid the UUID of the module this TCPConnectionManager is running on
	 * @param prettyPrint enables pretty printing for JSON if set to <code>true</code>
	 */
	// TODO: a way to add more Gson type adapters may be needed later on (maybe like addHandler)
	public TCPConnectionManager(final EventLoopGroup networkEventLoopGroup,
			final EventExecutorGroup applicationExecutor,
			final ChannelFactory<? extends ServerSocketChannel> serverChannelFactory,
			final ChannelFactory<? extends Channel> clientChannelFactory, final UUID uuid, final boolean prettyPrint) {
		LOGGER.entry(networkEventLoopGroup, applicationExecutor, serverChannelFactory, clientChannelFactory, uuid,
				prettyPrint);
		this.localUUID = uuid;
		
		this.messageAdapter = new MessageAdapter();
		this.messageAdapter.addMessageType(HelloMessage.class);
		this.messageAdapter.addMessageType(ConnectionEstablishedMessage.class);
		this.messageAdapter.addMessageType(ConnectionClosedMessage.class);
		
		final GsonBuilder gsonBuilder = new GsonBuilder();
		if (prettyPrint) {
			gsonBuilder.setPrettyPrinting();
		}
		gsonBuilder.registerTypeAdapter(Message.class, messageAdapter);
		final ChannelHandler channelInitializer = new TCPConnectionChannelInitializer(gsonBuilder.create(),
				applicationExecutor, new TCPClientConnectionHandler(new HelloMessage(uuid, DEFAULT_MAX_FRAME_LENGTH)));
		
		this.serverFactory = new TCPServerChannelFactory(serverChannelFactory, networkEventLoopGroup,
				channelInitializer);
		
		this.clientFactory = new TCPClientChannelFactory(clientChannelFactory, networkEventLoopGroup,
				channelInitializer);
		LOGGER.exit();
	}
	
	/**
	 * Creates a new TCPConnectionManager that does not use pretty printing for JSON.
	 * 
	 * @param networkEventLoopGroup the EventLoopGroup to use for server channels
	 * @param applicationExecutor the EventLoopGroup to use for client channels
	 * @param serverChannelFactory a factory for new server channels
	 * @param clientChannelFactory a factory for new client channels
	 * @param uuid the UUID of the module this TCPConnectionManager is running on
	 */
	public TCPConnectionManager(final EventLoopGroup networkEventLoopGroup,
			final EventExecutorGroup applicationExecutor,
			final ChannelFactory<? extends ServerSocketChannel> serverChannelFactory,
			final ChannelFactory<? extends Channel> clientChannelFactory, final UUID uuid) {
		this(networkEventLoopGroup, applicationExecutor, serverChannelFactory, clientChannelFactory, uuid, false);
	}
	
	/**
	 * Adds a type of Message. If either the class or the type name are already in use, nothing is done.
	 * 
	 * @param cls the class to add
	 * @param type the name to use when (de-)serializing this class
	 */
	public void addMessageType(final Class<? extends Message> cls, final String type) {
		messageAdapter.addMessageType(cls, type);
	}
	
	/**
	 * Adds a type of Message. The attribute named {@value #TYPE_ATTRIBUTE_NAME} is used to determine the type name.
	 * If either the class or the type name are already in use, nothing is done.
	 * 
	 * @param cls the class to add
	 * @see #addMessageType(Class, String)
	 */
	public void addMessageType(final Class<? extends Message> cls) {
		messageAdapter.addMessageType(cls);
	}
	
	public void startListening(final InetSocketAddress address) {
		// FIXME: if the channel future has not yet finished there is no way to cancel the bind
		LOGGER.entry(address);
		serverFactory.bind(address).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(final ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					LOGGER.debug("bind {} successful, registering server channel", future);
					synchronized (serverChannels) {
						serverChannels.add((ServerSocketChannel) future.channel());
					}
				} else {
					if (LOGGER.isWarnEnabled()) {
						LOGGER.warn("bind {} failed", future);
					}
				}
			}
		});
		LOGGER.exit();
	}
	
	/**
	 * Tries to connect to a given address.
	 * 
	 * @param address the address to connect to
	 */
	public void connectTo(final InetSocketAddress address) {
		clientFactory.connect(address);
	}
	
	@Override
	public void beaconFound(final BeaconMessage beacon) {
		// TODO: Maybe store addresses somewhere in case beacons have a high package loss
		clientChannelsLock.readLock().lock();
		try {
			if (clientChannels.containsKey(beacon.getUUID())) {
				return;
			}
		} finally {
			clientChannelsLock.readLock().unlock();
		}
		for (final InetSocketAddress address : beacon.getAddresses()) {
			connectTo(address);
		}
	}

	@Override
	public void sendMessage(final UUID uuid, final Message message) {
		Channel channel = null;
		clientChannelsLock.readLock().lock();
		try {
			channel = clientChannels.get(uuid);
		} finally {
			clientChannelsLock.readLock().unlock();
		}
		if (channel != null) {
			channel.write(message);
		}
	}

	@Override
	public void addHandler(final UUID appid, final MessageHandler handler) {
		handlersLock.writeLock().lock();
		try {
			if (handler == null) {
				handlers.remove(appid);
			} else {
				handlers.put(appid, handler);
			}
		} finally {
			handlersLock.writeLock().unlock();
		}
	}

	@Override
	public void addHandler(final MessageHandler handler) {
		addHandler(APPID_DEFAULT, handler);
	}

	@Override
	public Set<UUID> getConnectedModules() {
		clientChannelsLock.readLock().lock();
		try {
			return Collections.unmodifiableSet(clientChannels.keySet());
		} finally {
			clientChannelsLock.readLock().unlock();
		}
	}
	
	@Sharable
	private class TCPConnectionChannelInitializer extends ChannelInitializer<SocketChannel> {
		/**
		 * The Gson object that will be used by the channels.
		 */
		private final Gson gson;
		
		/**
		 * The maximum frame length for the channels.
		 */
		private final int maxFrameLength;

		/**
		 * The executor to use for application code.
		 */
		private final EventExecutorGroup executor;
		
		/**
		 * The application level handler to use for new connections.
		 */
		private final ChannelHandler handler;
		
		/**
		 * Initializes a new TCPConnectionChannelInitializer.
		 * 
		 * @param gson the Gson object to use for new channels
		 * @param executorGroup the EventExecutorGroup that should be used for application code
		 * @param handler the application level handler for new channels
		 * @param maxFrameLength the maximum length of a frame that can be received
		 */
		public TCPConnectionChannelInitializer(final Gson gson, final EventExecutorGroup executorGroup,
				final ChannelHandler handler, final int maxFrameLength) {
			LOGGER.entry(gson, executorGroup, handler, maxFrameLength);
			this.gson = gson;
			this.executor = executorGroup;
			this.maxFrameLength = maxFrameLength;
			this.handler = handler;
			LOGGER.exit();
		}
		
		/**
		 * Initializes a new TCPConnectionChannelInitializer. Will use {@value #DEFAULT_MAX_FRAME_LENGTH} as maximum
		 * frame length.
		 * 
		 * @param gson the Gson object to use for new channels
		 * @param executor the EventExecutorGroup that should be used for application code
		 * @param firstMessage a message to send after a connection has been established
		 * @param handler the application level handler for new channels
		 */
		TCPConnectionChannelInitializer(final Gson gson, final EventExecutorGroup executor,
				final ChannelHandler handler) {
			this(gson, executor, handler, DEFAULT_MAX_FRAME_LENGTH);
		}

		@Override
		protected void initChannel(final SocketChannel channel) throws Exception {
			LOGGER.entry(channel);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("initializing channel {} connecting {} to {}", channel, channel.localAddress(),
						channel.remoteAddress());
			}
			channel.pipeline()
				.addLast(new LengthFieldPrepender(2))
				.addLast(new LengthFieldBasedFrameDecoder(maxFrameLength, 0, 2, 0, 2))
				.addLast(new StringEncoder(CHARSET))
				.addLast(new StringDecoder(CHARSET))
				.addLast(new GsonCodec(gson, Message.class))
				.addLast(executor, handler);
			LOGGER.exit();
		}
	}
	
	@Sharable
	private class TCPClientConnectionHandler extends ChannelInboundMessageHandlerAdapter<Message> {
		/**
		 * The message to send after the connection has been established. Use null to disable.
		 */
		private final Message firstMessage;
		
		/**
		 * Initializes a new ClientConnectionHandler.
		 * 
		 * @param firstMessage a message to send after a connection has been established
		 */
		TCPClientConnectionHandler(final Message firstMessage) {
			LOGGER.entry(firstMessage);
			this.firstMessage = firstMessage;
			LOGGER.exit();
		}
		
		@Override
		public void messageReceived(final ChannelHandlerContext ctx, final Message msg) {
			ThreadContext.put("remoteAddress", ctx.channel().remoteAddress().toString());
			ThreadContext.put("remoteUUID", ctx.attr(REMOTE_UUID_KEY).toString());
			LOGGER.entry(ctx, msg);
			if (msg instanceof HelloMessage) {
				handleHelloMessage(ctx, (HelloMessage) msg);
			} else if (msg instanceof ConnectionClosedMessage) {
				handleConnectionClosedMessage(ctx, (ConnectionClosedMessage) msg);
			} else if (msg instanceof ConnectionEstablishedMessage) {
				handleConnectionEstablishedMessage(ctx, (ConnectionEstablishedMessage) msg);
			} else if (msg instanceof ApplicationSpecificMessage) {
				handleApplicationSpecificMessage(ctx, (ApplicationSpecificMessage) msg);
			} else {
				handleGenericMessage(ctx, msg);
			}
			LOGGER.exit();
			ThreadContext.remove("remoteUUID");
			ThreadContext.remove("remoteAddress");
		}
		
		private void handleHelloMessage(final ChannelHandlerContext ctx, final HelloMessage msg) {
			LOGGER.entry(ctx, msg);
			if (ctx.attr(REMOTE_UUID_KEY).get() != null) {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("got {} but {} is already set as remote UUID", msg, ctx.attr(REMOTE_UUID_KEY).get());
				}
				LOGGER.exit();
				return;
			}
			final UUID remoteUUID = msg.getUUID();
			ctx.attr(REMOTE_UUID_KEY).set(remoteUUID);
			ThreadContext.put("remoteUUID", remoteUUID.toString());
			if (localUUID.equals(remoteUUID)) {
				LOGGER.warn("remote UUID is my own, closing connection");
				ctx.write(new ConnectionClosedMessage());
				ctx.close();
				LOGGER.exit();
			}
			if (localUUID.compareTo(remoteUUID) < 0) {
				boolean establish = false;
				clientChannelsLock.readLock().lock();
				if (!clientChannels.containsKey(remoteUUID)) {
					clientChannelsLock.readLock().unlock();
					clientChannelsLock.writeLock().lock();
					if (!clientChannels.containsKey(remoteUUID)) {
						clientChannels.put(remoteUUID, ctx.channel());
						establish = true;
					}
					clientChannelsLock.readLock().lock();
					clientChannelsLock.writeLock().unlock();
				}
				clientChannelsLock.readLock().unlock();
				if (establish) {
					ctx.write(new ConnectionEstablishedMessage());
				} else {
					LOGGER.debug("we already have a connection, closing");
					ctx.write(new ConnectionClosedMessage());
					ctx.close();
				}
			}
			LOGGER.exit();
		}
		
		private void handleConnectionEstablishedMessage(final ChannelHandlerContext ctx,
				final ConnectionEstablishedMessage msg) {
			LOGGER.entry(ctx, msg);
			final UUID remoteUUID = ctx.attr(REMOTE_UUID_KEY).get();
			if (remoteUUID == null) {
				LOGGER.warn("got {} before a HelloMessage was received", msg);
				LOGGER.exit();
				return;
			}
			if (localUUID.compareTo(remoteUUID) < 0) {
				LOGGER.warn("got {} but the other module has a higher UUID", msg);
				LOGGER.exit();
				return;
			}
			clientChannelsLock.writeLock().lock();
			final Channel channel = clientChannels.get(remoteUUID);
			if (channel != null && !channel.equals(ctx.channel())) {
				LOGGER.info("got {} but there is already another connection ({}), closing that");
				channel.close();
			}
			clientChannels.put(remoteUUID, ctx.channel());
			clientChannelsLock.writeLock().unlock();
			LOGGER.exit();
		}
		
		private void handleConnectionClosedMessage(final ChannelHandlerContext ctx, final ConnectionClosedMessage msg) {
			LOGGER.entry(ctx, msg);
			final UUID remoteUUID = ctx.attr(REMOTE_UUID_KEY).get();
			if (remoteUUID == null) {
				LOGGER.exit();
				return;
			}
			clientChannelsLock.readLock().lock();
			if (ctx.channel().equals(clientChannels.get(remoteUUID))) {
				clientChannelsLock.readLock().unlock();
				clientChannelsLock.writeLock().lock();
				if (ctx.channel().equals(clientChannels.get(remoteUUID))) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("removing {} for {}", ctx.channel(), remoteUUID);
					}
					clientChannels.remove(remoteUUID);
				}
				clientChannelsLock.readLock().lock();
				clientChannelsLock.writeLock().unlock();
			}
			clientChannelsLock.readLock().unlock();
			ctx.close();
			LOGGER.exit();
		}
		
		private void handleApplicationSpecificMessage(final ChannelHandlerContext ctx,
				final ApplicationSpecificMessage msg) {
			if (msg.getApplicationID() == null) {
				LOGGER.debug("{} does not have an application UUID, using generic handler");
				handleGenericMessage(ctx, msg);
				return;
			}
			MessageHandler handler = null;
			handlersLock.readLock().lock();
			try {
				handler = handlers.get(((ApplicationSpecificMessage) msg).getApplicationID());
			} finally {
				handlersLock.readLock().unlock();
			}
			if (handler != null) {
				LOGGER.debug("using handler {} to handle {}", handler, msg);
				handler.handleMessage(msg);
			} else {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("got {}, but no handler is registered for {}", msg,
							((ApplicationSpecificMessage) msg).getApplicationID());
				}
			}
		}
		
		private void handleGenericMessage(final ChannelHandlerContext ctx, final Message msg) {
			MessageHandler handler = null;
			handlersLock.readLock().lock();
			try {
				handler = handlers.get(APPID_DEFAULT);
			} finally {
				handlersLock.readLock().unlock();
			}
			if (handler != null) {
				LOGGER.debug("using default handler {} to handle {}", handler, msg);
				handler.handleMessage(msg);
			} else {
				LOGGER.warn("got {}, but no default handler is installed", msg);
			}
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) {
			ThreadContext.put("remoteAddress", ctx.channel().remoteAddress().toString());
			LOGGER.entry(ctx);
			ctx.write(firstMessage);
			LOGGER.exit();
		}
	}
}
