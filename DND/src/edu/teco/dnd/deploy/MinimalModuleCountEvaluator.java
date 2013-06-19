package edu.teco.dnd.deploy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.deploy.Distribution.BlockTarget;
import edu.teco.dnd.module.Module;

public class MinimalModuleCountEvaluator implements EvaluationStrategy {
	@Override
	public int evaluate(final Distribution distribution) {
		final Set<Module> modules = new HashSet<Module>();
		for (final BlockTarget blockTarget : distribution.getMapping().values()) {
			modules.add(blockTarget.getModule());
		}
		if (modules.isEmpty()) {
			return Integer.MAX_VALUE;
		}
		return Integer.MAX_VALUE - modules.size() + 1;
	}

	@Override
	public int upperBound(Distribution distribution,
			Collection<FunctionBlock> blocks, Collection<Module> modules) {
		if (distribution.getMapping().isEmpty()) {
			return Integer.MAX_VALUE;
		}
		return evaluate(distribution);
	}
}
