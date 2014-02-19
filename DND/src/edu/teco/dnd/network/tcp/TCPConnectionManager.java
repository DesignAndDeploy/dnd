package edu.teco.dnd.network.tcp;

import io.netty.channel.Channel;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.GsonBuilder;

import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.network.BeaconListener;
import edu.teco.dnd.network.ConnectionListener;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.UDPMulticastBeacon;
import edu.teco.dnd.network.messages.BeaconMessage;
import edu.teco.dnd.network.messages.Message;
import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.network.tcp.ResponseFutureManager.ResponseFutureNotifier;
import edu.teco.dnd.util.FinishedFutureNotifier;
import edu.teco.dnd.util.FutureListener;
import edu.teco.dnd.util.FutureNotifier;

/**
 * <p>
 * An implementation of {@link ConnectionManager} that uses TCP connections and sends Messages encoded as JSON obejcts.
 * </p>
 * 
 * <p>
 * The basic protocol is explained in the package documentation. To use this class you generally want to do something
 * like
 * 
 * <pre>
 * TCPConnectionManager mgr = new TCPConnectionManager(serverChannelFactory, clientChannelFactory, localID);
 * mgr.addMessageType(MyAwesomeMessage.class);
 * mgr.addHandler(MyAwesomeMessage.class, new MyAwesomeMessageHandler());
 * mgr.startListening(new InetSocketAddress(4242));
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * You will probably want to register the TCPConnectionManager to a class like {@link UDPMulticastBeacon} which will
 * handle autodiscovery of other clients.
 * </p>
 * 
 * <p>
 * TCPConnectionManager uses Netty 4 for its network code and GSON to (de-)serialize Messages to/from JSON.
 * </p>
 * 
 * @author Philipp Adolf
 */
public class TCPConnectionManager implements ConnectionManager, BeaconListener {
	private static final Logger LOGGER = LogManager.getLogger(TCPConnectionManager.class);

	public static final long TIMEOUT_DELAY = 30;
	public static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;

	private final ClientChannelManager clientChannelManager;
	private final ServerChannelManager serverChannelManager;
	private final ResponseFutureManager responseFutureManager;
	private final TimeoutResponseInvalidator timeoutResponseInvalidator;
	private final ClientMessageDispatcher messageDispatcher;
	private final ClientChannelInitializer clientChannelInitializer;

	private final ModuleID localID;

	private boolean isShuttingDown = false;
	protected ShutdownFuture shutdownFuture = new ShutdownFuture();

	/**
	 * Initializes a new TCPConnectionManager.
	 * 
	 * @param serverChannelFactory
	 *            a factory that will be used to create listening Channels
	 * @param clientChannelFactory
	 *            a factory that will be used to connect to other clients
	 * @param localID
	 *            the ModuleID of this client
	 */
	public TCPConnectionManager(final ServerChannelFactory serverChannelFactory,
			final ClientChannelFactory clientChannelFactory, final ScheduledExecutorService invalidatorThread,
			final ModuleID localID) {
		this.localID = localID;
		responseFutureManager = new ResponseFutureManager();
		timeoutResponseInvalidator = new TimeoutResponseInvalidator(invalidatorThread, TIMEOUT_DELAY, TIMEOUT_UNIT);
		clientChannelManager = new ClientChannelManager(clientChannelFactory);
		clientChannelInitializer = new ClientChannelInitializer(clientChannelManager, localID);
		clientChannelFactory.setChannelInitializer(clientChannelInitializer);
		messageDispatcher = new ClientMessageDispatcher(clientChannelManager, responseFutureManager);
		clientChannelInitializer.setMessageHandler(messageDispatcher);
		serverChannelFactory.setChildChannelInitializer(clientChannelInitializer);
		serverChannelManager = new ServerChannelManager(serverChannelFactory);
	}

	/**
	 * <p>
	 * Adds a Message type to the list of known types.
	 * </p>
	 * 
	 * <p>
	 * This method has to be called before a Message of that type is sent or received, otherwise sending/receiving will
	 * fail. The type that will be sent encoded in the JSON representation will be taken from the
	 * <code>public static final
	 * String</code> field called {@value MessageAdapter#DEFAULT_TYPE_FIELD_NAME} defined in the class.
	 * </p>
	 * 
	 * @param cls
	 *            the Message class to register
	 * @see MessageAdapter
	 */
	public void addMessageType(final Class<? extends Message> cls) {
		clientChannelInitializer.addMessageType(cls);
	}

	/**
	 * <p>
	 * Registers a type adapter for GSON.
	 * </p>
	 * 
	 * <p>
	 * Some classes cannot be serialized correctly by GSON. For these classes a type adapter can be added. This method
	 * is thread safe and all Messages sent after the method returned will use the new adapter. Adapters should be added
	 * as early as possible, especially if they are used to deserialize incoming Messages.
	 * </p>
	 * 
	 * @param type
	 *            the type for which the adapter should be registered
	 * @param adapter
	 *            the adapter to register
	 * @see GsonBuilder#registerTypeAdapter(Type, Object)
	 */
	public void registerTypeAdapter(final Type type, final Object adapter) {
		clientChannelInitializer.registerTypeAdapter(type, adapter);
	}

	/**
	 * Starts listening on the given address.
	 * 
	 * @param listenAddress
	 *            the address to listen on
	 */
	public void startListening(final InetSocketAddress listenAddress) {
		LOGGER.entry(listenAddress);
		synchronized (this) {
			if (!isShuttingDown) {
				LOGGER.info("listening on {}", listenAddress);
				serverChannelManager.bind(listenAddress);
			}
		}
		LOGGER.exit();
	}

	@Override
	public void beaconFound(final BeaconMessage beacon) {
		LOGGER.entry(beacon);
		if (localID.equals(beacon.getModuleID())) {
			LOGGER.exit();
			return;
		}
		if (!hasActiveChannel(beacon.getModuleID())) {
			for (final InetSocketAddress address : beacon.getAddresses()) {
				if (address != null) {
					connectTo(address);
				}
			}
		}
		LOGGER.exit();
	}

	private boolean hasActiveChannel(final ModuleID moduleID) {
		try {
			getActiveChannel(moduleID);
			return true;
		} catch (final NoSuchElementException e) {
			return false;
		}
	}

	/**
	 * Tries to connect to the given address.
	 * 
	 * @param address
	 *            the address to connect to
	 */
	public void connectTo(final InetSocketAddress address) {
		LOGGER.debug("trying to connect to {}", address);
		clientChannelManager.connect(address);
	}

	@Override
	public FutureNotifier<Response> sendMessage(final ModuleID moduleID, final Message message) {
		try {
			final Channel channel = getActiveChannel(moduleID);
			final ResponseFutureNotifier futureNotifier = responseFutureManager.createResponseFuture(message.getUUID());
			channel.writeAndFlush(message).addListener(new ResponseInvalidator(futureNotifier));
			timeoutResponseInvalidator.addTimeout(futureNotifier);
			return futureNotifier;
		} catch (final NoSuchElementException e) {
			return new FinishedFutureNotifier<Response>(e);
		}
	}

	/**
	 * Returns an active Channel that is connected to the given client. If there are multiple active channels for the
	 * {@link ModuleID}, one of them is returned without any guarantees as to which one (and subsequent calls for the
	 * same ModuleID may return different channels in this case).
	 * 
	 * @param moduleID
	 *            the ID of the remote Module
	 * @return an active channel that is connected to the Module
	 * @throws NoSuchElementException
	 *             if no active connection to the Module exists
	 */
	private Channel getActiveChannel(final ModuleID moduleID) {
		for (final Channel channel : clientChannelManager.getChannels(moduleID)) {
			if (clientChannelManager.isActive(channel)) {
				return channel;
			}
		}
		throw new NoSuchElementException("no active channel for " + moduleID);
	}

	@Override
	public <T extends Message> void addHandler(final ApplicationID appid, final Class<? extends T> msgType,
			final MessageHandler<? super T> handler, final Executor executor) {
		messageDispatcher.setHandler(msgType, handler, appid, executor);
	}

	@Override
	public <T extends Message> void addHandler(final ApplicationID appid, final Class<? extends T> msgType,
			final MessageHandler<? super T> handler) {
		messageDispatcher.setHandler(msgType, handler, appid);
	}

	@Override
	public <T extends Message> void addHandler(final Class<? extends T> msgType,
			final MessageHandler<? super T> handler, final Executor executor) {
		messageDispatcher.setDefaultHandler(msgType, handler, executor);
	}

	@Override
	public <T extends Message> void addHandler(final Class<? extends T> msgType, final MessageHandler<? super T> handler) {
		messageDispatcher.setDefaultHandler(msgType, handler);
	}

	@Override
	public Collection<ModuleID> getConnectedModules() {
		final Set<ModuleID> moduleIDs = new HashSet<ModuleID>();
		for (final Channel channel : clientChannelManager.getChannels()) {
			if (clientChannelManager.isActive(channel)) {
				final ModuleID moduleID = clientChannelManager.getRemoteID(channel);
				if (moduleID != null) {
					moduleIDs.add(moduleID);
				}
			}
		}
		return moduleIDs;
	}

	@Override
	public void addConnectionListener(final ConnectionListener listener) {
		clientChannelManager.addConnectionListener(listener);
	}

	@Override
	public void removeConnectionListener(final ConnectionListener listener) {
		clientChannelManager.removeConnectionListener(listener);
	}

	@Override
	public void shutdown() {
		LOGGER.entry();
		synchronized (this) {
			if (isShuttingDown) {
				LOGGER.exit();
				return;
			}

			LOGGER.info("shutting down");
			isShuttingDown = true;

			final FutureNotifier<Collection<Void>> serverCloseFuture = closeAllServerSockets();
			serverCloseFuture.addListener(new FutureListener<FutureNotifier<Collection<Void>>>() {
				@Override
				public void operationComplete(final FutureNotifier<Collection<Void>> future) throws Exception {
					shutdownFuture.setComplete();
				}
			});
		}
		LOGGER.exit();
	}

	private FutureNotifier<Collection<Void>> closeAllServerSockets() {
		return serverChannelManager.closeAllChannels();
	}

	@Override
	public synchronized boolean isShuttingDown() {
		return isShuttingDown;
	}

	@Override
	public FutureNotifier<Void> getShutdownFuture() {
		return shutdownFuture;
	}
}
