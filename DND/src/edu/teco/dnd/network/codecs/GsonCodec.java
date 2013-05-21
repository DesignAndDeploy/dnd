package edu.teco.dnd.network.codecs;

import com.google.gson.Gson;

import io.netty.buffer.MessageBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.MessageToMessageCodec;

/**
 * A codec that translate from Java objects to JSON representations and vice versa.
 *
 * @author Philipp Adolf
 */
@Sharable
public class GsonCodec extends MessageToMessageCodec<String, Object> {
	/**
	 * The Gson object used for (de-)serialisation.
	 */
	private final Gson gson = null;
	
	/**
	 * Creates a new GsonCodec.
	 * @param gson
	 */
	public GsonCodec(final Gson gson) {
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg,
			MessageBuf<Object> out) throws Exception {
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, String msg,
			MessageBuf<Object> out) throws Exception {
	}
}
