package edu.teco.dnd.network.codecs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.MessageBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * Extracts a ByteBuf from a DatagramPacket.
 *
 * @author Philipp Adolf
 */
@Sharable
public class DatagramPacketUnwrapper extends MessageToMessageDecoder<DatagramPacket> {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(DatagramPacketUnwrapper.class);
	
	@Override
	protected void decode(final ChannelHandlerContext ctx, final DatagramPacket msg, final MessageBuf<Object> out) {
		LOGGER.entry(ctx, msg, out);
		out.add(msg.content().copy());
		LOGGER.exit();
	}
}
