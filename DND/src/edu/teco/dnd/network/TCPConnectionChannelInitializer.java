package edu.teco.dnd.network;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.google.gson.Gson;

import edu.teco.dnd.network.codecs.GsonCodec;
import edu.teco.dnd.network.messages.Message;

/**
 * A class that initializes the pipeline for new client connections. Used by {@link TCPConnectionManager}.
 *
 * @author Philipp Adolf
 */
@Sharable
class TCPConnectionChannelInitializer extends ChannelInitializer<SocketChannel> {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(TCPConnectionChannelInitializer.class);
	
	/**
	 * Default maximum size of a frame that can be received.
	 */
	public static final int DEFAULT_MAX_FRAME_LENGTH = 512 * 1024;
	
	/**
	 * The charset to use (UTF-8).
	 */
	public static final Charset CHARSET = Charset.forName("UTF-8");
	
	/**
	 * The Gson object that will be used by the channels.
	 */
	private final Gson gson;
	
	/**
	 * The message to send after the connection has been establised. Use null to disable.
	 */
	private final Message firstMessage;
	
	/**
	 * The maximum frame length for the channels.
	 */
	private final int maxFrameLength;

	/**
	 * The executor to use for application code.
	 */
	private EventExecutorGroup executor;
	
	/**
	 * Initializes a new TCPConnectionChannelInitializer.
	 * 
	 * @param gson the Gson object to use for new channels
	 * @param executorGroup the EventExecutorGroup that should be used for application code
	 * @param firstMessage a message to send after a connection has been established
	 * @param maxFrameLength the maximum length of a frame that can be received
	 */
	public TCPConnectionChannelInitializer(final Gson gson, final EventExecutorGroup executorGroup,
			final Message firstMessage, final int maxFrameLength) {
		LOGGER.entry(gson, executorGroup, firstMessage, maxFrameLength);
		this.gson = gson;
		this.executor = executorGroup;
		this.firstMessage = firstMessage;
		this.maxFrameLength = maxFrameLength;
		LOGGER.exit();
	}
	
	/**
	 * Initializes a new TCPConnectionChannelInitializer. Will use {@value #DEFAULT_MAX_FRAME_LENGTH} as maximum frame
	 * length.
	 * 
	 * @param gson the Gson object to use for new channels
	 * @param executor the EventExecutorGroup that should be used for application code
	 * @param firstMessage a message to send after a connection has been established
	 */
	public TCPConnectionChannelInitializer(final Gson gson, final EventExecutorGroup executor, final Message firstMessage) {
		this(gson, executor, firstMessage, DEFAULT_MAX_FRAME_LENGTH);
	}

	@Override
	protected void initChannel(final SocketChannel channel) throws Exception {
		LOGGER.entry(channel);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("initializing channel {} connecting {} to {}", channel);
		}
		channel.pipeline()
			.addLast(new LengthFieldPrepender(2))
			.addLast(new LengthFieldBasedFrameDecoder(maxFrameLength, 0, 2, 0, 2))
			.addLast(new StringEncoder(CHARSET))
			.addLast(new StringDecoder(CHARSET))
			.addLast(new GsonCodec(gson, Message.class))
			.addLast(executor, new ChannelInboundMessageHandlerAdapter<Message>() {
				@Override
				public void messageReceived(final ChannelHandlerContext ctx, final Message msg) {
					ThreadContext.put("remoteAddress", ctx.channel().remoteAddress().toString());
					LOGGER.trace("Got {}", msg);
					ThreadContext.remove("remoteAddress");
				}
				
				@Override
				public void channelActive(ChannelHandlerContext ctx) {
					ThreadContext.put("remoteAddress", ctx.channel().remoteAddress().toString());
					LOGGER.entry(ctx);
					ctx.write(firstMessage);
					LOGGER.exit();
				}
			});
		LOGGER.exit();
	}
}
