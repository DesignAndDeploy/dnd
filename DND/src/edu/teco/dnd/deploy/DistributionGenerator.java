package edu.teco.dnd.deploy;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.config.BlockTypeHolder;
import edu.teco.dnd.module.config.BlockTypeHolderIterator;

/**
 * A genarator for {@link Distribution}s. Not thread safe.
 * 
 * @author Philipp Adolf
 */
public class DistributionGenerator {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(DistributionGenerator.class);

	/**
	 * The strategy that will be used to select the best distribution.
	 */
	private final EvaluationStrategy strategy;

	/**
	 * A list of Constraints that will be enforced.
	 */
	private final Collection<Constraint> constraints;

	/**
	 * The best Distribution found so far.
	 */
	private Distribution best = null;

	/**
	 * The value of the best Distribution found so far.
	 */
	private int bestValue = Integer.MIN_VALUE;

	/**
	 * Initializes a new generator with the given strategy and the given constraints. A {@link ResourceConstraint} and a
	 * {@link TypeConstraint} will always be added.
	 * 
	 * @param strategy
	 *            the strategy to use
	 * @param constraints
	 *            constraints that will be used. {@link ResourceConstraint} will always be added.
	 */
	public DistributionGenerator(final EvaluationStrategy strategy, final Collection<Constraint> constraints) {
		this.strategy = strategy;
		this.constraints = new ArrayList<Constraint>();
		this.constraints.add(new ResourceConstraint());
		this.constraints.addAll(constraints);
	}

	/**
	 * Initializes a new generator with the given strategy. A {@link ResourceConstraint} and a {@link TypeConstraint}
	 * will be used.
	 * 
	 * @param strategy
	 *            the strategy to use
	 */
	public DistributionGenerator(final EvaluationStrategy strategy) {
		this(strategy, Collections.<Constraint> emptyList());
	}

	/**
	 * Returns the best distribution for the given blocks and modules.
	 * 
	 * @param blocks
	 *            the blocks to distributed
	 * @param modules
	 *            the available modules
	 * @return the best Distribution found or null if none exists
	 */
	public Distribution getDistribution(final Collection<FunctionBlockModel> blocks, final Collection<Module> modules) {
		best = null;
		bestValue = Integer.MIN_VALUE;
		getDistribution(new ArrayList<FunctionBlockModel>(blocks), modules, new Distribution());
		return best;
	}

	/**
	 * Recursively finds the best distribution. Updates {@link #best} and {@link #bestValue}.
	 * 
	 * @param blocks
	 *            the blocks that still need to be distributed
	 * @param modules
	 *            the modules that are available
	 * @param start
	 *            the Distribution that has been built so far
	 */
	private void getDistribution(final Collection<FunctionBlockModel> blocks, final Collection<Module> modules,
			final Distribution start) {
		LOGGER.entry(blocks, modules, start);
		if (blocks.isEmpty()) {
			final int value = strategy.evaluate(start);
			if (value > bestValue) {
				LOGGER.trace("updating best to new distribution with value {}", value);
				best = new Distribution(start);
				bestValue = value;
			}
			LOGGER.exit();
			return;
		}

		final Queue<FunctionBlockModel> blockStack = new ArrayDeque<FunctionBlockModel>(blocks);

		while (!blockStack.isEmpty()) {
			final FunctionBlockModel block = blockStack.remove();

			blocks.remove(block);

			for (final Module module : modules) {
				for (final BlockTypeHolder holder : new BlockTypeHolderIterator(module.getHolder())) {
					if (holder.isLeave()) {
						if (isAllowed(start, block, module, holder)) {
							start.add(block, module, holder);
							final int upperBound = strategy.upperBound(start, blockStack, modules);
							if (upperBound > bestValue) {
								getDistribution(blocks, modules, start);
							}
							start.remove(block);
						}
					}
				}
			}

			blocks.add(block);
		}

		LOGGER.exit();
	}

	/**
	 * Checks all constraints to see if a given block can be added to a holder given a partial distribution.
	 * 
	 * @param distribution
	 *            the Distribution so far
	 * @param block
	 *            the block that should be checked
	 * @param module
	 *            the module where the block should be added
	 * @param holder
	 *            the holder where the block should be added
	 * @return
	 */
	private boolean isAllowed(final Distribution distribution, final FunctionBlockModel block, final Module module,
			final BlockTypeHolder holder) {
		for (final Constraint constraint : constraints) {
			if (!constraint.isAllowed(distribution, block, module, holder)) {
				LOGGER.debug("not adding block {} to module {}, holder {} in distribution {} because of {}", block,
						module, holder, distribution, constraint);
				return false;
			}
		}
		return true;
	}
}