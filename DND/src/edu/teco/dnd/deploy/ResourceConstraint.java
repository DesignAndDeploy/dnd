package edu.teco.dnd.deploy;

import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.config.BlockTypeHolder;

/**
 * A Constraint that ensures that there are enough free slots in the given BlockTypeHolder. This only accepts slots of
 * the same type as the blocks type.
 * 
 * @author Philipp Adolf
 */
public class ResourceConstraint implements Constraint {
	@Override
	public boolean isAllowed(final Distribution distribution, final FunctionBlockModel block, final Module module,
			final BlockTypeHolder holder) {
		return distribution.canAdd(block, module, holder);
	}
}