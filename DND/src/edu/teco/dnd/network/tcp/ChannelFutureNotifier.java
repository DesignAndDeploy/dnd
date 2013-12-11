package edu.teco.dnd.network.tcp;

import io.netty.channel.Channel;
import edu.teco.dnd.util.FutureNotifier;

/**
 * A FutureNotifier that has a Channel associated with it.
 * 
 * @author Philipp Adolf
 */
public interface ChannelFutureNotifier extends FutureNotifier<Void> {
	Channel channel();
}
