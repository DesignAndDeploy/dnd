package edu.teco.dnd.deploy;

import java.util.Collection;
import java.util.Map;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.Module;

/**
 * This interface is used to implement a strategy pattern which makes it possible to swap between different
 * working algorithms for the evaluation.
 */
public interface EvaluationStrategy {

	/**
	 * Evaluates which plan shall be used for the distribution.
	 * 
	 * @param distributionPlans
	 *            The given plans
	 * @return The optimal plan
	 */
	Map<FunctionBlock, Module> evaluate(Collection<Map<FunctionBlock, Module>> distributionPlans);
}
