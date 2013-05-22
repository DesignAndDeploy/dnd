package edu.teco.dnd.network;

import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.GsonBuilder;

import edu.teco.dnd.network.codecs.MessageAdapter;
import edu.teco.dnd.network.messages.BeaconMessage;
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
	 * A factory for new server channels.
	 */
	private final ChannelFactory<? extends ServerSocketChannel> serverChannelFactory;
	
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
	 * Handlers for given application IDs.
	 */
	private final Map<UUID, MessageHandler> handlers = new HashMap<UUID, MessageHandler>();
	
	private final TCPConnectionChannelInitializer channelInitializer;
	
	private final TCPClientChannelFactory clientFactory;
	
	private final TCPServerChannelFactory serverFactory;
	
	private final MessageAdapter messageAdapter;
	
	
	
	
	// FIXME: only for testing
	public static void main(final String[] args) {
		int port = Integer.parseInt(args[0]);
		int connectPort = Integer.parseInt(args[1]);
		final TCPConnectionManager cm = new TCPConnectionManager(new NioEventLoopGroup(),
				new DefaultEventExecutorGroup(8),
				new ChannelFactory<NioServerSocketChannel>() {
					@Override
					public NioServerSocketChannel newChannel() {
						return new NioServerSocketChannel();
					}
				},
				new ChannelFactory<NioSocketChannel>() {
					@Override
					public NioSocketChannel newChannel() {
						return new NioSocketChannel();
					}
				},
				UUID.randomUUID(),
				true);
		cm.startListening(new InetSocketAddress(port));
		cm.connectTo(new InetSocketAddress(connectPort));
	}
	
	
	
	
	
	
	
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
		this.serverChannelFactory = serverChannelFactory;
		
		this.messageAdapter = new MessageAdapter();
		this.messageAdapter.addMessageType(HelloMessage.class);
		
		final GsonBuilder gsonBuilder = new GsonBuilder();
		if (prettyPrint) {
			gsonBuilder.setPrettyPrinting();
		}
		gsonBuilder.registerTypeAdapter(Message.class, messageAdapter);
		this.channelInitializer = new TCPConnectionChannelInitializer(gsonBuilder.create(), applicationExecutor,
				new HelloMessage(uuid, TCPConnectionChannelInitializer.DEFAULT_MAX_FRAME_LENGTH));
		
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
	public TCPConnectionManager(final EventLoopGroup networkEventLoopGroup, final EventExecutor applicationExecutor,
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
						LOGGER.warn("bind failed");
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
		// FIXME: only tries to connect to the first address in the beacon
		connectTo(beacon.getAddresses().get(0));
	}

	@Override
	public void sendMessage(final UUID uuid, final Message message) {
	}

	@Override
	public void addHandler(final UUID appid, final MessageHandler handler) {
	}

	@Override
	public void addHandler(final MessageHandler handler) {
	}

	@Override
	public List<UUID> getConnectedModules() {
		return null;
	}
}
