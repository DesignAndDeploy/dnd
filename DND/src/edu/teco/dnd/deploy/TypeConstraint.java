package edu.teco.dnd.deploy;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.config.BlockTypeHolder;

/**
 * A Constraint that ensures that the type of the BlockTypeHolder matches the type of the FunctionBlock.
 *
 * @author Philipp Adolf
 */
public class TypeConstraint implements Constraint {
	@Override
	public boolean isAllowed(final Distribution distribution, final FunctionBlock block, final Module module, 
			final BlockTypeHolder holder) {
		final String holderType = holder.getType();
		if (holderType == null) {
			return false;
		}
		return holderType.equals(block.getType());
	}
}
