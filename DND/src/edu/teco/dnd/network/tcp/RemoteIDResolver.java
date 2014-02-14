package edu.teco.dnd.network.tcp;

import io.netty.channel.Channel;
import edu.teco.dnd.module.ModuleID;

/**
 * Provides a way to get the ModuleID of a remote client.
 * 
 * @author Philipp Adolf
 */
public interface RemoteIDResolver {
	/**
	 * Returns the ModuleID of the client on the other end of the Channel or null if unknown.
	 * 
	 * @return the ModuleID of the client on the other end of the Channel or null if unknown
	 */
	ModuleID getRemoteID(final Channel channel);
}
