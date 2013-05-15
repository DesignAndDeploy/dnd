package edu.teco.dnd.deploy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.Module;

/**
 * Deployment strategy minimizing number of Modules used.
 */
public class MaximalBlockNumberEvaluation implements EvaluationStrategy {

	/**
	 * Returns the plan which uses the least number of modules.
	 * 
	 * @param distributionPlans
	 *            A Collection of all possible distribution plans
	 * @return a map of functionblocks and modules used as distribution plan, null if none exists
	 */
	@Override
	public final Map<FunctionBlock, Module> evaluate(
			final Collection<Map<FunctionBlock, Module>> distributionPlans) {
		Map<FunctionBlock, Module> bestFit = null;
		int maximum = 0;
		for (Map<FunctionBlock, Module> plan : distributionPlans) {
			List<Module> diffModules = new ArrayList<Module>();
			for (Module module : plan.values()) {
				if (!diffModules.contains(module)) {
					diffModules.add(module);
				}
			}
			if (diffModules.size() > maximum) {
				bestFit = plan;
				maximum = diffModules.size();
			}
		}

		return bestFit;
	}

}
