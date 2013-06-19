package edu.teco.dnd.deploy;

import java.util.Collection;
import java.util.Map;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.Module;

public interface EvaluationStrategy {
	int evaluate(Distribution distribution);
	
	int upperBound(Distribution distribution, Collection<FunctionBlock> blocks, Collection<Module> modules);
}
