package edu.teco.dnd.module;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.Output;

/**
 * Wrapper for a FunctionBlock. The idea is, that FunctionBlock code can not be trusted to work properly, yet must still
 * be executed. Thus every code executed on aFunctionBlock is wrapped by this. Not only does this give an easy way to
 * discern untrusted code on a stacktrace (as used in securityManagers) But it also limits the entry/exit points and
 * allows for mor cnscious wrapping and lessends the likelyhood of forgetten wrapping.
 * 
 * @author Marvin Marx
 * 
 */
public class FunctionBlockSecurityDecorator {
	private final FunctionBlock block;

	public FunctionBlockSecurityDecorator(final FunctionBlock block) {
		this.block = block;
	}

	public static FunctionBlockSecurityDecorator getDecorator(final Class<? extends FunctionBlock> blockClass)
			throws UserSuppliedCodeException {
		final FunctionBlock realBlock;
		try {
			realBlock = (FunctionBlock) blockClass.getConstructor().newInstance();
		} catch (Throwable e) {
			// Throwing a new exception so no user supplied Throwable will be called later on
			throw new UserSuppliedCodeException("Could not instantiate block of class " + blockClass);
		}
		return new FunctionBlockSecurityDecorator(realBlock);
	}

	public void doInit(final UUID blockUUID) throws UserSuppliedCodeException {
		try {
			this.block.doInit(blockUUID);
		} catch (Throwable e) {
			// Throwing a new exception so no user supplied Throwable will be called later on
			throw new UserSuppliedCodeException("an exception was thrown while calling doInit on block " + blockUUID);
		}
	}

	public void init(final Map<String, String> options) throws UserSuppliedCodeException {
		try {
			block.init(options);
		} catch (Throwable t) {
			// Throwing a new exception so no user supplied Throwable will be called later on
			throw new UserSuppliedCodeException("an exception was thrown while calling init on block "
					+ block.getBlockUUID());
		}
	}

	public void shutdown() throws UserSuppliedCodeException {
		try {
			block.shutdown();
		} catch (Throwable t) {
			// Throwing a new exception so no user supplied Throwable will be called later on
			throw new UserSuppliedCodeException("an exception was thrown while calling shutdown on block "
					+ block.getBlockUUID());
		}
	}

	public void update() throws UserSuppliedCodeException {
		try {
			block.update();
		} catch (Throwable t) {
			// Throwing a new exception so no user supplied Throwable will be called later on
			throw new UserSuppliedCodeException("an exception was thrown while calling update on block "
					+ block.getBlockUUID());
		}
	}

	/**
	 * 
	 * @return type of the block this FunctionBlockSecurityDecorator contains.
	 */
	public String getBlockType() {
		return block.getBlockType();
	}

	/**
	 * 
	 * @return UUID of the block this FunctionBlockSecurityDecorator contains.
	 */
	public UUID getBlockUUID() {
		return block.getBlockUUID();
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FunctionBlockSecurityDecorator other = (FunctionBlockSecurityDecorator) obj;
		if (block == null) {
			if (other.block != null)
				return false;
		} else if (!block.equals(other.block))
			return false;
		return true;
	}
}
