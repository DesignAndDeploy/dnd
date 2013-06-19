package edu.teco.dnd.deploy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.deploy.Distribution.BlockTarget;
import edu.teco.dnd.module.Module;

/**
 * An EvaluationStrategy that rates higher the higher the number of used modules is.
 *
 * @author Philipp Adolf
 */
public class MaximalModuleCountEvaluator implements EvaluationStrategy {
	@Override
	public int evaluate(final Distribution distribution) {
		final Set<Module> modules = new HashSet<Module>();
		for (final BlockTarget blockTarget : distribution.getMapping().values()) {
			modules.add(blockTarget.getModule());
		}
		return Integer.MAX_VALUE - distribution.getMapping().size() + modules.size();
	}

	@Override
	public int upperBound(Distribution distribution,
			Collection<FunctionBlock> blocks, Collection<Module> modules) {
		return Integer.MAX_VALUE;
	}
}
