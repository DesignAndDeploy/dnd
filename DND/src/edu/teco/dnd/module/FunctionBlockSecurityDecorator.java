package edu.teco.dnd.module;

import java.io.Serializable;
import java.util.Map;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.FunctionBlockID;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.module.permissions.ApplicationSecurityManager;

/**
 * A wrapper for {@link FunctionBlock} that catches any {@link Throwable}. The idea is that FunctionBlocks can be loaded
 * via the network and are therefore untrusted. Together with the {@link ApplicationSecurityManager} this class tries to
 * minimize possible attack vectors.
 */
public class FunctionBlockSecurityDecorator {
	private final FunctionBlock realBlock;

	/**
	 * Instantiates a new {@link FunctionBlock} and wraps it in a FunctionBlockSecurityDecorator.
	 * 
	 * @param blockClass
	 *            the class of the FunctionBlock that should be instantiated. Must have a constructor that takes no
	 *            arguments.
	 * @throws UserSuppliedCodeException
	 *             if any {@link Throwable} is thrown
	 */
	public FunctionBlockSecurityDecorator(final Class<? extends FunctionBlock> blockClass)
			throws UserSuppliedCodeException {
		final FunctionBlock realBlock;
		try {
			realBlock = (FunctionBlock) blockClass.getConstructor().newInstance();
			if (realBlock == null) {
				throw new NullPointerException();
			}
		} catch (Throwable e) {
			// Throwing a new exception so no user supplied Throwable will be called later on
			throw new UserSuppliedCodeException("Could not instantiate block of class " + blockClass);
		}
		this.realBlock = realBlock;
	}

	/**
	 * Wrapper for {@link FunctionBlock#doInit(FunctionBlockID, String)}. Catches any {@link Throwable}.
	 * 
	 * @throws UserSuppliedCodeException
	 *             if any {@link Throwable} is thrown
	 * @see FunctionBlock#doInit(FunctionBlockID, String)
	 */
	public void initInternal(final FunctionBlockID blockID, final String blockName) throws UserSuppliedCodeException {
		try {
			this.realBlock.initInternal(blockID, blockName);
		} catch (Throwable e) {
			// Throwing a new exception so no user supplied Throwable will be called later on
			throw new UserSuppliedCodeException("an exception was thrown while calling doInit on block " + blockID);
		}
	}

	/**
	 * Wrapper for {@link FunctionBlock#init(Map)}. Catches any {@link Throwable}.
	 * 
	 * @throws UserSuppliedCodeException
	 *             if any {@link Throwable} is thrown
	 * @see FunctionBlock#init(Map)
	 */
	public void init(final Map<String, String> options) throws UserSuppliedCodeException {
		try {
			realBlock.init(options);
		} catch (Throwable t) {
			// Throwing a new exception so no user supplied Throwable will be called later on
			throw new UserSuppliedCodeException("an exception was thrown while calling init on block "
					+ realBlock.getBlockID());
		}
	}

	/**
	 * Wrapper for {@link FunctionBlock#shutdown()}. Catches any {@link Throwable}.
	 * 
	 * @throws UserSuppliedCodeException
	 *             if any {@link Throwable} is thrown
	 * @see FunctionBlock#shutdown()
	 */
	public void shutdown() throws UserSuppliedCodeException {
		try {
			realBlock.shutdown();
		} catch (Throwable t) {
			// Throwing a new exception so no user supplied Throwable will be called later on
			throw new UserSuppliedCodeException("an exception was thrown while calling shutdown on block "
					+ realBlock.getBlockID());
		}
	}

	/**
	 * Wrapper for {@link FunctionBlock#update()}. Catches any {@link Throwable}.
	 * 
	 * @throws UserSuppliedCodeException
	 *             if any {@link Throwable} is thrown
	 * @see FunctionBlock#update()
	 */
	public void update() throws UserSuppliedCodeException {
		try {
			realBlock.update();
		} catch (Throwable t) {
			// Throwing a new exception so no user supplied Throwable will be called later on
			throw new UserSuppliedCodeException("an exception was thrown while calling update on block "
					+ realBlock.getBlockID());
		}
	}

	/**
	 * @see FunctionBlock#getBlockType()
	 */
	public String getBlockType() {
		return realBlock.getBlockType();
	}

	/**
	 * @see FunctionBlock#getBlockName()
	 */
	public String getBlockName() {
		return realBlock.getBlockName();
	}

	/**
	 * @see FunctionBlock#getBlockID()
	 */
	public FunctionBlockID getBlockID() {
		return realBlock.getBlockID();
	}

	/**
	 * @see FunctionBlock#getUpdateInterval()
	 */
	public long getUpdateInterval() {
		return realBlock.getUpdateInterval();
	}

	/**
	 * @see FunctionBlock#getOutputs()
	 */
	public Map<String, Output<? extends Serializable>> getOutputs() {
		return realBlock.getOutputs();
	}

	/**
	 * @see FunctionBlock.getInputs()
	 */
	public Map<String, Input<? extends Serializable>> getInputs() {
		return realBlock.getInputs();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((realBlock == null) ? 0 : realBlock.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FunctionBlockSecurityDecorator other = (FunctionBlockSecurityDecorator) obj;
		if (realBlock == null) {
			if (other.realBlock != null) {
				return false;
			}
		} else if (!realBlock.equals(other.realBlock)) {
			return false;
		}
		return true;
	}
}
