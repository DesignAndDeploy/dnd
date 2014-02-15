package edu.teco.dnd.module;

import java.io.Serializable;
import java.util.Map;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.FunctionBlockID;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.Output;

/**
 * Wrapper for a FunctionBlock. The idea is, that FunctionBlock code can not be trusted to work properly, yet must still
 * be executed. Thus every code executed on aFunctionBlock is wrapped by this. Not only does this give an easy way to
 * discern untrusted code on a stacktrace (as used in securityManagers) But it also limits the entry/exit points and
 * allows for more conscious wrapping and lessens the likelihood of forgotten wrapping.
 * 
 * @author Marvin Marx
 * 
 */
public class FunctionBlockSecurityDecorator {
	private final FunctionBlock block;

	/**
	 * creates a new decorator wrapping the given block.
	 * 
	 * @param blockClass
	 *            the block to be wrapped.
	 * @throws UserSuppliedCodeException
	 *             if an error occured while trying to instantiate the FunctionBlock.
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
		this.block = realBlock;
	}

	/**
	 * wrapper for doInit on FunctionBlocks. just wrapped for security.
	 * 
	 * @param blockID
	 *            see {@link FunctionBlock#initInternal(FunctionBlockID, String)}
	 * @param blockName
	 *            see {@link FunctionBlock#initInternal(FunctionBlockID, String)}
	 * @throws UserSuppliedCodeException
	 *             if an error occurred in the block code.
	 */
	public void initInternal(final FunctionBlockID blockID, final String blockName) throws UserSuppliedCodeException {
		try {
			this.block.initInternal(blockID, blockName);
		} catch (Throwable e) {
			// Throwing a new exception so no user supplied Throwable will be called later on
			throw new UserSuppliedCodeException("an exception was thrown while calling doInit on block " + blockID);
		}
	}

	/**
	 * wraps FunctionBlock.init() for security.
	 * 
	 * @param options
	 *            see FunctionBlock.init()
	 * @throws UserSuppliedCodeException
	 *             if an error occurred in the block code.
	 */
	public void init(final Map<String, String> options) throws UserSuppliedCodeException {
		try {
			block.init(options);
		} catch (Throwable t) {
			// Throwing a new exception so no user supplied Throwable will be called later on
			throw new UserSuppliedCodeException("an exception was thrown while calling init on block "
					+ block.getBlockID());
		}
	}

	/**
	 * wraps FunctionBlock.shutdown() for security.
	 * 
	 * @throws UserSuppliedCodeException
	 *             if an error occurred in the block code.
	 */
	public void shutdown() throws UserSuppliedCodeException {
		try {
			block.shutdown();
		} catch (Throwable t) {
			// Throwing a new exception so no user supplied Throwable will be called later on
			throw new UserSuppliedCodeException("an exception was thrown while calling shutdown on block "
					+ block.getBlockID());
		}
	}

	/**
	 * wraps FunctionBlock.update() for security.
	 * 
	 * @throws UserSuppliedCodeException
	 *             if an error occurred in the block code.
	 */
	public void update() throws UserSuppliedCodeException {
		try {
			block.update();
		} catch (Throwable t) {
			// Throwing a new exception so no user supplied Throwable will be called later on
			throw new UserSuppliedCodeException("an exception was thrown while calling update on block "
					+ block.getBlockID());
		}
	}

	/**
	 * 
	 * @return type of the block this FunctionBlockSecurityDecorator contains.
	 */
	public String getBlockType() {
		return block.getBlockType();
	}

	public String getBlockName() {
		return block.getBlockName();
	}

	/**
	 * 
	 * @return UUID of the block this FunctionBlockSecurityDecorator contains.
	 */
	public FunctionBlockID getBlockID() {
		return block.getBlockID();
	}

	/**
	 * 
	 * @return updateIntervall of the block this FunctionBlockSecurityDecorator contains.
	 * @see FunctionBlock.getUpdateIntervall()
	 */
	public long getUpdateInterval() {

		return block.getUpdateInterval();
	}

	/**
	 * 
	 * @return Outputs of the block this FunctionBlockSecurityDecorator contains.
	 * @see FunctionBlock.getOutputs()
	 */
	public Map<String, Output<? extends Serializable>> getOutputs() {
		return block.getOutputs();
	}

	/**
	 * 
	 * @return inputs of the block this FunctionBlockSecurityDecorator contains.
	 * @see FunctionBlock.getInputs()
	 */
	public Map<String, Input<? extends Serializable>> getInputs() {
		return block.getInputs();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((block == null) ? 0 : block.hashCode());
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
		if (block == null) {
			if (other.block != null) {
				return false;
			}
		} else if (!block.equals(other.block)) {
			return false;
		}
		return true;
	}
}
