package edu.teco.dnd.network.logging;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import org.apache.logging.log4j.LogManager;

/**
 * An implementation of Netty’s {@link InternalLoggerFactory} that uses {@link Log4j2Logger} to forward log messages
 * to Log4j2.
 */
public class Log4j2LoggerFactory extends InternalLoggerFactory {
	@Override
	protected InternalLogger newInstance(final String name) {
		return new Log4j2Logger(LogManager.getLogger(name));
	}
}
