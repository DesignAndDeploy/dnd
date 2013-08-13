package edu.teco.dnd.module;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.blocks.FunctionBlock;

public class FunctionBlockSecurityDecorator extends FunctionBlock {
	private static final long serialVersionUID = -2698446647806837671L;

	private static final Random rnd = new Random();
	private static final Logger LOGGER = LogManager.getLogger(FunctionBlockSecurityDecorator.class);

	private final FunctionBlock block;

	public FunctionBlockSecurityDecorator(FunctionBlock block) throws UserSuppliedCodeException, NullPointerException,
			IllegalArgumentException, IllegalAccessException {
		doInit(block.getBlockUUID());
		this.block = block;
	}

	public void doInitBlock(Application associatedApp) {

	}

	@Override
	public void init() {

		try {
			block.init();
		} catch (Throwable t) {
			Thread.dumpStack();
			LOGGER.warn("Block {} ({}), threw an exception in init()", block.getClass(), this.getBlockUUID());
			// ignoring. Don't kill the rest of the application.
		}
	}

	@Override
	public void update() {
		try {
			block.update();
		} catch (Throwable t) {
			Thread.dumpStack();
			// Ignoring, Do not want to kill application because of this.
		}
	}

	@Override
	public int hashCode() {
		// Note that we cannot prevent breaking the equals/hashCode contract.
		int hashCode;
		try {
			hashCode = block.hashCode();
		} catch (Throwable t) {
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("Block {} ({}), threw an exception in hashCode()", block.getClass(), this.getBlockUUID());
			}
			hashCode = rnd.nextInt();
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {

		boolean equal;
		try {
			equal = block.equals(obj);
		} catch (Throwable t) {
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("Block {} ({}), threw an exception in equals()", block.getClass(), this.getBlockUUID());
			}
			return false;
		}
		return equal;
	}

	@Override
	public String toString() {
		String toStr;
		try {
			toStr = block.toString();
		} catch (Throwable t) {
			LOGGER.warn("Block {} ({}), threw an exception in toString()", block.getClass(), this.getBlockUUID());
			try {
				return "[ERROR: " + t.getMessage() + "]";
			} catch (Throwable t2) {
				// Throwable might be overridden as well;
				return "[ERROR]";
			}
		}
		if (toStr == null) {
			return "[NULL]";
		} else {
			return toStr;
		}
	}

}
