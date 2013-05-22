package edu.teco.dnd.network.codecs;

import java.lang.reflect.Type;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(GsonCodec.class);
	
	/**
	 * The Gson object used for (de-)serialisation.
	 */
	private final Gson gson;
	
	/**
	 * The type that should be used.
	 */
	private final Type type;
	
	/**
	 * Creates a new GsonCodec that uses <code>type</code> as the target type for serialisation.
	 * 
	 * @param gson the Gson object to use
	 */
	public GsonCodec(final Gson gson, final Type type) {
		this.gson = gson;
		this.type = type;
	}

	@Override
	protected void encode(final ChannelHandlerContext ctx, final Object msg, final MessageBuf<Object> out) {
		LOGGER.entry(ctx, msg, out);
		String json = null;
		json = gson.toJson(msg, type);
		LOGGER.debug("adding {} to outbound queue", json);
		out.add(json);
		LOGGER.exit();
	}

	@Override
	protected void decode(final ChannelHandlerContext ctx, final String msg, final MessageBuf<Object> out) {
		LOGGER.entry(ctx, msg, out);
		final Object obj = gson.fromJson(msg, type);
		LOGGER.debug("adding {} to inbound queue", obj);
		out.add(obj);
		LOGGER.exit();
	}
}
