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
	public void shutdown() {
		try {
			block.shutdown();
		} catch (Throwable t) {
			Thread.dumpStack();
			LOGGER.warn("Block {}, threw an exception in init()", this.getBlockUUID());
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
}
