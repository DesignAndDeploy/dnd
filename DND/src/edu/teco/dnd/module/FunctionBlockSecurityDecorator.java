package edu.teco.dnd.module;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.blocks.FunctionBlock;

public class FunctionBlockSecurityDecorator {
	private static final Logger LOGGER = LogManager.getLogger(FunctionBlockSecurityDecorator.class);

	private final FunctionBlock block;

	public FunctionBlockSecurityDecorator(final FunctionBlock block) {
		this.block = block;
	}

	public static FunctionBlockSecurityDecorator getDecorator(final Class<? extends FunctionBlock> blockClass) {
		final FunctionBlock realBlock;
		try {
			realBlock = (FunctionBlock) blockClass.getConstructor().newInstance();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (SecurityException e) {
			e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
		return new FunctionBlockSecurityDecorator(realBlock);
	}

	public boolean doInit(final UUID blockUUID) {
		try {
			this.block.doInit(blockUUID);
		} catch (IllegalAccessException e) {
			Thread.dumpStack();
			LOGGER.warn("{} threw an exception in doInit()", block);
			return LOGGER.exit(false);
		}
		return true;
	}

	public boolean init() {
		try {
			block.init();
		} catch (Throwable t) {
			Thread.dumpStack();
			LOGGER.warn("{} threw an exception in init()", block);
			return LOGGER.exit(false);
		}
		return true;
	}

	public boolean shutdown() {
		try {
			block.shutdown();
		} catch (Throwable t) {
			Thread.dumpStack();
			LOGGER.warn("{} threw an exception in init()", block);
			return LOGGER.exit(false);
		}
		return true;
	}

	public boolean update() {
		try {
			block.update();
		} catch (Throwable t) {
			Thread.dumpStack();
			return LOGGER.exit(false);
		}
		return true;
	}

	public FunctionBlock getRealBlock() {
		return block;
	}
}
