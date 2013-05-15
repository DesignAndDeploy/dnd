package edu.teco.dnd.deploy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.module.Module;

/**
 * This class is responsible for the creation of a correct plan for the distribution of processes.
 */
public class DistributionAlgorithm {
	/** A specific strategy to evaluate which distribution plan is used. */
	private EvaluationStrategy evaluation;

	/**
	 * Initializes a new DistributionAlgorithm.
	 * 
	 * @param evaluation
	 *            the EvaluationStrategy to use
	 */
	public DistributionAlgorithm(final EvaluationStrategy evaluation) {
		this.evaluation = evaluation;
	}

	/**
	 * Initializes a new DistributionAlgorithm with the standard {@link EvaluationStrategy}.
	 */
	public DistributionAlgorithm() {
		this(new MinimalBlockNumberEvaluation());
	}

	/**
	 * Sets the evaluation strategy.
	 * 
	 * @param evaluation
	 *            The strategy to be used
	 */
	public final void setEvaluationStrategy(final EvaluationStrategy evaluation) {
		if (evaluation == null) {
			throw new IllegalArgumentException("evaluation must not be null!");
		}
		this.evaluation = evaluation;
	}

	/**
	 * Returns all possible, valid distribution plans.
	 * 
	 * @param blocks
	 *            The {@link FunctionBlock}s which shall be distributed
	 * @param modules
	 *            A given collection of {@link Module} among which the process parts can be distributed
	 * @return A Collection of all possible plans
	 */
	public final Collection<Map<FunctionBlock, Module>> getDistributionPlans(
			final Collection<FunctionBlock> blocks, final Collection<Module> modules) {
		if ((blocks == null) || (modules == null)) {
			throw new IllegalArgumentException("blocks and modules must not be null!");
		}
		Map<FunctionBlock, Collection<Module>> possibleModules = new HashMap<FunctionBlock, Collection<Module>>();
		for (FunctionBlock block : blocks) {
			if (block != null) {
				possibleModules.put(block, new ArrayList<Module>());
				for (Module module : modules) {
					if (module != null) {
						if (module.canRun(block)) {
							possibleModules.get(block).add(module);
						}
					} else {
						throw new IllegalArgumentException("modules may not contain null!");
					}
				}
			} else {
				throw new IllegalArgumentException("blocks may not contain null!");
			}
		}
		Collection<Map<FunctionBlock, Module>> plans = new ArrayList<Map<FunctionBlock, Module>>();
		completeSinglePlans(plans, possibleModules, new HashMap<FunctionBlock, Module>());
		return checkForValidPlans(plans, blocks.size());
	}

	/**
	 * Recursively creates all possible distribution plans and adds them to a given collection.
	 * 
	 * @param plans
	 *            The Collection of all plans
	 * @param possibleModules
	 *            Used to identify the possible modules for a single function block
	 * @param toComplete
	 *            a single plan which shall be completed
	 */
	private void completeSinglePlans(final Collection<Map<FunctionBlock, Module>> plans,
			final Map<FunctionBlock, Collection<Module>> possibleModules,
			final Map<FunctionBlock, Module> toComplete) {
		if (possibleModules.keySet().size() == 0) {
			plans.add(toComplete);
		} else {
			FunctionBlock block = (FunctionBlock) possibleModules.keySet().iterator().next();
			for (Module module : possibleModules.get(block)) {
				Map<FunctionBlock, Module> toCompleteCopy = new HashMap<FunctionBlock, Module>();
				toCompleteCopy.putAll(toComplete);
				toCompleteCopy.put(block, module);
				Map<FunctionBlock, Collection<Module>> possibleModulesCopy = new HashMap<>();
				possibleModulesCopy.putAll(possibleModules);
				possibleModulesCopy.remove(block);
				completeSinglePlans(plans, possibleModulesCopy, toCompleteCopy);
			}

		}
	}

	/**
	 * Checks whether the given distribution plans are valid.
	 * 
	 * @param plans
	 *            the given plans
	 * @param blockNumber
	 *            the number of blocks every plan should contain
	 * @return the valid plans
	 */
	private Collection<Map<FunctionBlock, Module>> checkForValidPlans(
			final Collection<Map<FunctionBlock, Module>> plans, final int blockNumber) {
		boolean isValid;
		Collection<Map<FunctionBlock, Module>> planCopy = new ArrayList<>();
		planCopy.addAll(plans);
		for (Map<FunctionBlock, Module> singlePlan : planCopy) {
			isValid = true;
			if ((singlePlan.keySet().size() != blockNumber) || (singlePlan.values().size() != blockNumber)
					|| (blockNumber == 0)) {
				isValid = false;
			}
			Map<Module, Collection<FunctionBlock>> reverseMap = new HashMap<>();
			for (Map.Entry<FunctionBlock, Module> entry : singlePlan.entrySet()) {
				if (!reverseMap.containsKey(entry.getValue())) {
					reverseMap.put(entry.getValue(), new ArrayList<FunctionBlock>());
				}
				reverseMap.get(entry.getValue()).add(entry.getKey());
			}
			for (Map.Entry<Module, Collection<FunctionBlock>> entry : reverseMap.entrySet()) {
				if (!entry.getKey().canRun(entry.getValue())) {
					isValid = false;
					break;
				}
			}
			if (!isValid) {
				plans.remove(singlePlan);
			}
		}
		return plans;
	}

	/**
	 * Evaluates which distribution plan is the best by using the set evaluation strategy.
	 * 
	 * @param distributionPlans
	 *            A Collection of all possible distribution plans
	 * @return the best distribution plan according to the set strategy.
	 */
	public final Map<FunctionBlock, Module> evaluate(
			final Collection<Map<FunctionBlock, Module>> distributionPlans) {
		if (distributionPlans == null) {
			throw new IllegalArgumentException("distributionPlans must not be null!");
		}
		return evaluation.evaluate(distributionPlans);
	}

	/**
	 * Directly returns a distribution. Used if no interactive process is wanted.
	 * 
	 * @param blocks
	 *            blocks to distribute
	 * @param modules
	 *            modules to distribute to
	 * @return a distribution
	 */
	public Map<FunctionBlock, Module> getDistribution(final Collection<FunctionBlock> blocks,
			final Collection<Module> modules) {
		if (blocks == null || modules == null) {
			throw new IllegalArgumentException("arguments must not be null!");
		}

		return evaluate(getDistributionPlans(blocks, modules));
	}
}
