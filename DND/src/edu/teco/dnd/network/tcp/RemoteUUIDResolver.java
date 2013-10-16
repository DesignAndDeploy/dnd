package edu.teco.dnd.network.tcp;

import java.util.UUID;

import io.netty.channel.Channel;

/**
 * Provides a way to get the UUID of a remote client.
 * 
 * @author Philipp Adolf
 */
public interface RemoteUUIDResolver {
	/**
	 * Returns the UUID of the client on the other end of the Channel or null if unknown.
	 * 
	 * @return the UUID of the client on the other end of the Channel or null if unknown
	 */
	UUID getRemoteUUID(final Channel channel);
}
