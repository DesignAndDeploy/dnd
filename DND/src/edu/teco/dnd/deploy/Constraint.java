package edu.teco.dnd.deploy;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.config.BlockTypeHolder;

public interface Constraint {
	boolean isAllowed(Distribution distribution, FunctionBlock block, Module module, BlockTypeHolder holder);
}
