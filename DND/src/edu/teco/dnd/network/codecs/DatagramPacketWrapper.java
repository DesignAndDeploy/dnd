package edu.teco.dnd.network.codecs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class that wraps ByteBufs into a DatagramPacket.
 * 
 * @author Philipp Adolf
 */
@Sharable
public class DatagramPacketWrapper extends MessageToMessageCodec<DatagramPacket, ByteBuf> {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(DatagramPacketWrapper.class);

	/**
	 * The AttributeKey for the target address. The address must be attached to the context of this ChannelHandler.
	 */
	public static final AttributeKey<InetSocketAddress> TARGET_ADDRESS = new AttributeKey<InetSocketAddress>(
			"targetAddress");

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		LOGGER.entry(ctx, msg, out);
		out.add(new DatagramPacket(msg.retain(), ctx.attr(TARGET_ADDRESS).get()));
		LOGGER.exit();
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
		LOGGER.entry(ctx, msg, out);
		out.add(msg.content().retain());
		LOGGER.exit();
	}
}
