package edu.teco.dnd.network.codecs;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundByteHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.AttributeKey;

/**
 * A class that wraps ByteBufs into a DatagramPacket.
 *
 * @author Philipp Adolf
 */
@Sharable
public class DatagramPacketWrapper extends ChannelOutboundByteHandlerAdapter {
	@Override
	protected void flush(final ChannelHandlerContext ctx, final ByteBuf in, final ChannelPromise promise)
			throws Exception {
	}
}
