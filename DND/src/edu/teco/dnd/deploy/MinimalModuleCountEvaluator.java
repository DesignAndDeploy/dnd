package edu.teco.dnd.deploy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.teco.dnd.deploy.Distribution.BlockTarget;
import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.module.ModuleInfo;

/**
 * An EvaluationStrategy that rates higher the lower the number of used modules is.
 * 
 * @author Philipp Adolf
 */
public class MinimalModuleCountEvaluator implements EvaluationStrategy {
	@Override
	public int evaluate(final Distribution distribution) {
		final Set<ModuleInfo> modules = new HashSet<ModuleInfo>();
		for (final BlockTarget blockTarget : distribution.getMapping().values()) {
			modules.add(blockTarget.getModule());
		}
		if (modules.isEmpty()) {
			return Integer.MAX_VALUE;
		}
		return Integer.MAX_VALUE - modules.size() + 1;
	}

	@Override
	public int upperBound(Distribution distribution, Collection<FunctionBlockModel> blocks, Collection<ModuleInfo> modules) {
		if (distribution.getMapping().isEmpty()) {
			return Integer.MAX_VALUE;
		}
		return evaluate(distribution);
	}
}
