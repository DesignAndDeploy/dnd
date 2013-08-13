package edu.teco.dnd.module;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.blocks.AssignmentException;
import edu.teco.dnd.blocks.FunctionBlock;

public class FunctionBlockSecurityDecorator extends FunctionBlock {
	private static final long serialVersionUID = -2698446647806837671L;

	private static final Random rnd = new Random();
	private static final Logger LOGGER = LogManager.getLogger(FunctionBlockSecurityDecorator.class);

	private final FunctionBlock block;
	private final String blockType;

	public FunctionBlockSecurityDecorator(FunctionBlock block) throws UserSuppliedCodeException, NullPointerException {
		super(block.getID(), block.getBlockName());
		this.block = block;

		String type;
		try {
			type = block.getType();
		} catch (Throwable t) {
			LOGGER.warn("Block {} ({}), threw an exception in getBlockType()", this.getBlockName(), this.getID());
			throw new UserSuppliedCodeException("Exception in getBlockType.");
		}
		if (type == null) {
			LOGGER.warn("Blocktype returned by {} ({}), was null", this.getBlockName(), this.getID());
			throw new UserSuppliedCodeException("Blocktype must not be null!");
		} else {
			this.blockType = type;
		}

	}

	/**
	 * Returns the type of the given functionBlock.(Making sure it stays the same between runs.)
	 * 
	 * @param block
	 *            the FunctionBlock to get the type from.
	 * @return the type of the block
	 * @throws UserSuppliedCodeException
	 *             if the code produces any Exceptions.
	 */
	@Override
	public String getType() {
		return blockType;
	}

	public void doInitBlock(Application associatedApp) {

	}

	@Override
	public void init() {

		try {
			block.init();
		} catch (Throwable t) {
			Thread.dumpStack();
			LOGGER.warn("Block {} ({}), threw an exception in init()", this.getBlockName(), this.getID());
			// ignoring. Don't kill the rest of the application.
		}
	}

	@Override
	protected void update() {
		try {
			try {
				block.doUpdate();
			} catch (AssignmentException e) {
				LOGGER.catching(e);
			}
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
			LOGGER.warn("Block {} ({}), threw an exception in hashCode()", this.getBlockName(), this.getID());
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
			LOGGER.warn("Block {} ({}), threw an exception in equals()", this.getBlockName(), this.getID());
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
			LOGGER.warn("Block {} ({}), threw an exception in toString()", this.getBlockName(), this.getID());
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
