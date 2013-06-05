package edu.teco.dnd.network.logging;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;

public class Log4j2Logger implements InternalLogger {
	public static final Map<InternalLogLevel, Level> INTERNAL_TO_LOG4J2_LEVEL;
	
	static {
		INTERNAL_TO_LOG4J2_LEVEL = new HashMap<InternalLogLevel, Level>();
		INTERNAL_TO_LOG4J2_LEVEL.put(InternalLogLevel.TRACE, Level.TRACE);
		INTERNAL_TO_LOG4J2_LEVEL.put(InternalLogLevel.DEBUG, Level.DEBUG);
		INTERNAL_TO_LOG4J2_LEVEL.put(InternalLogLevel.INFO, Level.INFO);
		INTERNAL_TO_LOG4J2_LEVEL.put(InternalLogLevel.WARN, Level.WARN);
		INTERNAL_TO_LOG4J2_LEVEL.put(InternalLogLevel.ERROR, Level.ERROR);
	}
	
	private final Logger logger;
	
	public Log4j2Logger(final Logger logger) {
		this.logger = logger;
	}

	@Override
	public String name() {
		return logger.getName();
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public void trace(final String msg) {
		logger.trace(msg);
	}

	@Override
	public void trace(final String format, final Object arg) {
		logger.trace(format, arg);
	}

	@Override
	public void trace(final String format, final Object argA, final Object argB) {
		logger.trace(format, argA, argB);
	}

	@Override
	public void trace(final String format, final Object... arguments) {
		logger.trace(format, arguments);
	}

	@Override
	public void trace(final String msg, final Throwable t) {
		logger.trace(msg, t);
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public void debug(final String msg) {
		logger.debug(msg);
	}

	@Override
	public void debug(final String format, final Object arg) {
		logger.debug(format, arg);
	}

	@Override
	public void debug(final String format, final Object argA, final Object argB) {
		logger.debug(format, argA, argB);
	}

	@Override
	public void debug(final String format, final Object... arguments) {
		logger.debug(format, arguments);
	}

	@Override
	public void debug(final String msg, final Throwable t) {
		logger.debug(msg, t);
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public void info(final String msg) {
		logger.info(msg);
	}

	@Override
	public void info(final String format, final Object arg) {
		logger.info(format, arg);
	}

	@Override
	public void info(final String format, final Object argA, final Object argB) {
		logger.info(format, argA, argB);
	}

	@Override
	public void info(final String format, final Object... arguments) {
		logger.info(format, arguments);
	}

	@Override
	public void info(final String msg, final Throwable t) {
		logger.info(msg, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public void warn(final String msg) {
		logger.warn(msg);
	}

	@Override
	public void warn(final String format, final Object arg) {
		logger.warn(format, arg);
	}

	@Override
	public void warn(final String format, final Object argA, final Object argB) {
		logger.warn(format, argA, argB);
	}

	@Override
	public void warn(final String format, final Object... arguments) {
		logger.warn(format, arguments);
	}

	@Override
	public void warn(final String msg, final Throwable t) {
		logger.warn(msg, t);
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public void error(final String msg) {
		logger.error(msg);
	}

	@Override
	public void error(final String format, final Object arg) {
		logger.error(format, arg);
	}

	@Override
	public void error(final String format, final Object argA, final Object argB) {
		logger.error(format, argA, argB);
	}

	@Override
	public void error(final String format, final Object... arguments) {
		logger.error(format, arguments);
	}

	@Override
	public void error(final String msg, final Throwable t) {
		logger.error(msg, t);
	}
	
	@Override
	public boolean isEnabled(final InternalLogLevel level) {
		return logger.isEnabled(INTERNAL_TO_LOG4J2_LEVEL.get(level));
	}

	@Override
	public void log(final InternalLogLevel level, String msg) {
		logger.log(INTERNAL_TO_LOG4J2_LEVEL.get(level), msg);
	}

	@Override
	public void log(final InternalLogLevel level, String format, Object arg) {
		logger.log(INTERNAL_TO_LOG4J2_LEVEL.get(level), format, arg);
	}

	@Override
	public void log(final InternalLogLevel level, final String format, Object argA, Object argB) {
		logger.log(INTERNAL_TO_LOG4J2_LEVEL.get(level), format, argA, argB);
	}

	@Override
	public void log(final InternalLogLevel level, final String format, Object... arguments) {
		logger.log(INTERNAL_TO_LOG4J2_LEVEL.get(level), format, arguments);
	}

	@Override
	public void log(final InternalLogLevel level, final String msg, final Throwable t) {
		logger.log(INTERNAL_TO_LOG4J2_LEVEL.get(level), msg, t);
	}
}
