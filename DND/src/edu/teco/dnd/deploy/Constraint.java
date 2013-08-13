package edu.teco.dnd.deploy;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.config.BlockTypeHolder;

/**
 * A Constraint can be used to enforce certain attributes of a Distribution, for example to force a block
 * 
 * @author Philipp Adolf
 */
public interface Constraint {
	/**
	 * Checks to see if a given block can be assigned to a holder of a module given a partial distribution.
	 * 
	 * @param distribution
	 *            a partial distribution
	 * @param block
	 *            the block that should be check
	 * @param module
	 *            the module to check
	 * @param holder
	 *            the holder to check
	 * @return true if the block can be assigned, false otherwise
	 */
	boolean isAllowed(Distribution distribution, FunctionBlock block, Module module, BlockTypeHolder holder);
}
