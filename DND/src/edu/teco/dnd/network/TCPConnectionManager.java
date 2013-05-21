package edu.teco.dnd.network;

import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import edu.teco.dnd.network.messages.BeaconMessage;
import edu.teco.dnd.network.messages.Message;

/**
 * An implementation of ConnectionManager that uses TCP connections and JSON for communication.
 * 
 * @author Philipp Adolf
 */
public class TCPConnectionManager implements ConnectionManager, BeaconListener {
	/**
	 * The EventLoopGroup used for server channels.
	 */
	private final EventLoopGroup serverEventLoopGroup = null;
	
	/**
	 * The EventLoopGroup used for client channels.
	 */
	private final EventLoopGroup clientEventLoopGroup = null;
	
	/**
	 * A factory for new server channels.
	 */
	private final ChannelFactory<? extends ServerSocketChannel> serverChannelFactory = null;
	
	/**
	 * Contains all active server channels.
	 */
	private final Set<ServerSocketChannel> serverChannels = null;
	
	// TODO: a list of channels that have been created but didn't complete the handshake yet is probably needed
	/**
	 * Contains all client channels with an established connection.
	 */
	private final Map<UUID, Channel> clientChannels = null;
	
	/**
	 * Handlers for given application IDs.
	 */
	private final Map<UUID, MessageHandler> handlers = null;
	
	/**
	 * Creates a new TCPConnectionManager.
	 * 
	 * @param serverEventLoopGroup the EventLoopGroup to use for server channels
	 * @param clientEventLoopGroup the EventLoopGroup to use for client channels
	 * @param serverChannelFactory a factory for new server channels
	 * @param prettyPrint enables pretty printing for JSON if set to <code>true</code>
	 */
	public TCPConnectionManager(final EventLoopGroup serverEventLoopGroup, final EventLoopGroup clientEventLoopGroup,
			final ChannelFactory<? extends ServerSocketChannel> serverChannelFactory, final boolean prettyPrint) {
	}
	
	/**
	 * Creates a new TCPConnectionManager that does not use pretty printing.
	 * 
	 * @param serverEventLoopGroup the EventLoopGroup to use for server channels
	 * @param clientEventLoopGroup the EventLoopGroup to use for client channels
	 * @param serverChannelFactory a factory for new server channels
	 */
	public TCPConnectionManager(final EventLoopGroup serverEventLoopGroup, final EventLoopGroup clientEventLoopGroup,
			final ChannelFactory<? extends ServerSocketChannel> serverChannelFactory) {
	}
	
	/**
	 * Adds a type of Message. If either the class or the type name are already in use, nothing is done.
	 * 
	 * @param cls the class to add
	 * @param type the name to use when (de-)serializing this class
	 */
	public void addMessageType(final Class<? extends Message> cls, final String type) {
	}
	
	/**
	 * Adds a type of Message. The attribute named {@value #TYPE_ATTRIBUTE_NAME} is used to determine the type name.
	 * If either the class or the type name are already in use, nothing is done.
	 * 
	 * @param cls the class to add
	 * @see #addMessageType(Class, String)
	 */
	public void addMessageType(final Class<? extends Message> cls) {
	}
	
	@Override
	public void beaconFound(final BeaconMessage beacon) {
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
