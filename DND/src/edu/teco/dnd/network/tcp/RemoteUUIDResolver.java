package edu.teco.dnd.network.tcp;

import java.util.UUID;

import io.netty.channel.Channel;

public interface RemoteUUIDResolver {
	UUID getRemoteUUID(final Channel channel);
}
