package edu.teco.dnd.deploy;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.config.BlockTypeHolder;

/**
 * A Constraint that ensures that there are enough free slots in the given BlockTypeHolder.
 *
 * @author Philipp Adolf
 */
public class ResourceConstraint implements Constraint {
	@Override
	public boolean isAllowed(final Distribution distribution,
			final FunctionBlock block, final Module module, final BlockTypeHolder holder) {
		return distribution.canAdd(block, module, holder);
	}
}