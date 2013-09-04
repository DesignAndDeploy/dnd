package edu.teco.dnd.network;

import io.netty.channel.Channel;
import edu.teco.dnd.util.FutureNotifier;

public interface ChannelFutureNotifier extends FutureNotifier<Void> {
	Channel channel();
}
