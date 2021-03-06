package edu.teco.dnd.network.tcp;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A codec that translate from Java objects to JSON representations and vice versa.
 */
@Sharable
public class GsonCodec extends MessageToMessageCodec<String, Object> {
	private static final Logger LOGGER = LogManager.getLogger(GsonCodec.class);

	private final GsonBuilder gsonBuilder;
	private final AtomicReference<Gson> gson;
	private final Type type;

	/**
	 * Creates a new GsonCodec that uses <code>type</code> as the target type for (de-)serialization.
	 * 
	 * @param type
	 *            the type to use for (de-)serialization
	 * @param prettyPrint
	 *            if true, pretty printing is enabled
	 */
	public GsonCodec(final Type type, final boolean prettyPrint) {
		this.type = type;

		gsonBuilder = new GsonBuilder();
		if (prettyPrint) {
			gsonBuilder.setPrettyPrinting();
		}

		gsonBuilder.disableHtmlEscaping();
		gsonBuilder.enableComplexMapKeySerialization();

		gson = new AtomicReference<Gson>(gsonBuilder.create());
	}

	/**
	 * Creates a new GsonCodec that uses <code>type</code> as the target type for (de-)>serialization and that does not
	 * use pretty printing.
	 * 
	 * @param type
	 *            the type to use for (de-)serialization
	 * @see #GsonCodec(Type, boolean)
	 */
	public GsonCodec(final Type type) {
		this(type, false);
	}

	/**
	 * Registers a new type adapter.
	 * 
	 * @param type
	 *            the type the adapter should be used for
	 * @param adapter
	 *            the adapter to register
	 * @see GsonBuilder#registerTypeAdapter(Type, Object)
	 */
	public void registerTypeAdapter(final Type type, final Object adapter) {
		synchronized (gsonBuilder) {
			gsonBuilder.registerTypeAdapter(type, adapter);
			gson.set(gsonBuilder.create());
		}
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
		LOGGER.entry(ctx, msg, out);
		String json = null;
		json = gson.get().toJson(msg, type);
		LOGGER.debug("adding {} to outbound queue", json);
		out.add(json);
		LOGGER.exit();
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
		LOGGER.entry(ctx, msg, out);
		final Object obj = gson.get().fromJson(msg, type);
		if (obj != null) {
			LOGGER.debug("adding {} to inbound queue", obj);
			out.add(obj);
		}
		LOGGER.exit();
	}
}
