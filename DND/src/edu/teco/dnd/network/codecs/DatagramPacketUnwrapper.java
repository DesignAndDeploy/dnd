package edu.teco.dnd.network.codecs;

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
	@Override
	protected void decode(final ChannelHandlerContext ctx, final DatagramPacket msg, final MessageBuf<Object> out)
			throws Exception {	
	}
}
