package edu.teco.dnd.deploy;

import java.util.Collection;

import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.module.Module;

/**
 * Rates Distributions to determine the best.
 * 
 * @author Philipp Adolf
 */
public interface EvaluationStrategy {
	/**
	 * Rates a complete distribution. Higher values mean better Distributions.
	 * 
	 * @param distribution
	 *            the Distribution to rate
	 * @return a value for the Distribution. Higher values mean better Distributions.
	 */
	int evaluate(Distribution distribution);

	/**
	 * Gives an upper bound for the value a partial Distribution can achieve.
	 * 
	 * @param distribution
	 *            the partial Distribution to check
	 * @param blocks
	 *            the blocks that have not been assigned
	 * @param modules
	 *            all available Modules
	 * @return an upper bound for the value all Distributions based on the partial Distribution can achieve
	 */
	int upperBound(Distribution distribution, Collection<FunctionBlockModel> blocks, Collection<Module> modules);
}
