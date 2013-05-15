package edu.teco.dnd.deploy.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.deploy.DistributionAlgorithm;
import edu.teco.dnd.deploy.MaximalBlockNumberEvaluation;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleConfig;

import org.junit.Before;
import org.junit.Test;

/**
 * This class is for testing the creation of the distribution plans.
 */
public class TestDistributionAlgorithm {

	/** The number of plans the correct collection of distribution plans should contain. */
	private final int PLAN_NUMBER = 7;

	/** Initializes a new FunctionBlock. */
	private FunctionBlockTestFile block_x = new FunctionBlockTestFile("blockX", "x");

	/** Initializes a new FunctionBlock. */
	private FunctionBlockTestFile block_y = new FunctionBlockTestFile("blockY", "y");

	/** Initializes a new FunctionBlock. */
	private FunctionBlockTestFile block_z = new FunctionBlockTestFile("blockZ", "z");

	/** Creates a new Module. */
	private Module module_a;

	/** Creates a new Module. */
	private Module module_b;

	/** Creates a new Module. */
	private Module module_c;

	/** Initializes a new DistributionAlgorithm class. */
	private DistributionAlgorithm db = new DistributionAlgorithm();

	/** Initializes a new list of modules. */
	private List<Module> modules = new ArrayList<Module>();

	/** Initializes a new list of function blocks. */
	private List<FunctionBlock> blocks = new ArrayList<FunctionBlock>();

	/** Initializes a new distribution plan. */
	private Map<FunctionBlock, Module> plan1 = new HashMap<>();

	/** Initializes a new distribution plan. */
	private Map<FunctionBlock, Module> plan2 = new HashMap<>();

	/** Initializes a new distribution plan. */
	private Map<FunctionBlock, Module> plan3 = new HashMap<>();

	/** Initializes a new distribution plan. */
	private Map<FunctionBlock, Module> plan4 = new HashMap<>();

	/** Initializes a new distribution plan. */
	private Map<FunctionBlock, Module> plan5 = new HashMap<>();

	/** Initializes a new distribution plan. */
	private Map<FunctionBlock, Module> plan6 = new HashMap<>();

	/** Initializes a new distribution plan. */
	private Map<FunctionBlock, Module> plan7 = new HashMap<>();

	/** Initializes a new distribution plan. */
	private Map<FunctionBlock, Module> plan8 = new HashMap<>();

	/** Initializes a new Collection of distribution plans which is used for comparison. */
	private Collection<Map<FunctionBlock, Module>> toCompare = new ArrayList<>();

	/**
	 * Assigns the runnable blocks to the initialized modules.
	 */
	@Before
	public void initModulesAndPlans() {

		// init modules

		ModuleConfig config_a = new ModuleConfig();
		config_a.addSupportedBlockId("x");
		config_a.addSupportedBlockId("y");
		config_a.addSupportedBlockId("z");
		config_a.setMaxNumberOfBlocks(1);
		module_a = new Module(config_a);

		ModuleConfig config_b = new ModuleConfig();
		config_b.addSupportedBlockId("x");
		config_b.addSupportedBlockId("y");
		config_b.setMaxNumberOfBlocks(2);
		module_b = new Module(config_b);

		ModuleConfig config_c = new ModuleConfig();
		config_c.addSupportedBlockId("y");
		config_c.addSupportedBlockId("z");
		config_c.setMaxNumberOfBlocks(2);
		module_c = new Module(config_c);

		// init parameters for getDistributionPlans

		modules.add(module_a);
		modules.add(module_b);
		modules.add(module_c);

		blocks.add(block_x);
		blocks.add(block_y);
		blocks.add(block_z);

		// init plans to compare with

		plan1.put(block_z, module_a);
		plan1.put(block_y, module_b);
		plan1.put(block_x, module_b);
		toCompare.add(plan1);

		plan2.put(block_z, module_a);
		plan2.put(block_y, module_c);
		plan2.put(block_x, module_b);
		toCompare.add(plan2);

		plan3.put(block_z, module_c);
		plan3.put(block_y, module_c);
		plan3.put(block_x, module_a);
		toCompare.add(plan3);

		plan4.put(block_z, module_c);
		plan4.put(block_y, module_b);
		plan4.put(block_x, module_a);
		toCompare.add(plan4);

		plan5.put(block_z, module_c);
		plan5.put(block_y, module_a);
		plan5.put(block_x, module_b);
		toCompare.add(plan5);

		plan6.put(block_z, module_c);
		plan6.put(block_y, module_b);
		plan6.put(block_x, module_b);
		toCompare.add(plan6);

		plan7.put(block_z, module_c);
		plan7.put(block_y, module_c);
		plan7.put(block_x, module_b);
		toCompare.add(plan7);

		plan8.put(block_z, module_c);
		plan8.put(block_y, module_c);
		plan8.put(block_x, module_c);

	}

	/**
	 * Tests the getDistributionPlans method by comparing its return value to the initialized collection of
	 * plans.
	 */
	@Test
	public void testValidRun() {
		Collection<Map<FunctionBlock, Module>> plans = db.getDistributionPlans(blocks, modules);
		assertNotNull(plans);
		assertEquals(PLAN_NUMBER, plans.size());

		for (Map<FunctionBlock, Module> singlePlan : toCompare) {
			assertTrue(containsPlan(plans, singlePlan));
		}

		assertFalse(containsPlan(plans, plan8));
		assertFalse(containsPlan(plans, new HashMap<FunctionBlock, Module>()));
	}

	@Test
	public void testBlocksIsEmpty() {
		Collection<Map<FunctionBlock, Module>> plans = db.getDistributionPlans(
				new ArrayList<FunctionBlock>(), modules);
		assertEquals(0, plans.size());

	}

	@Test
	public void testModulesIsEmpty() {
		Collection<Map<FunctionBlock, Module>> plans = db.getDistributionPlans(blocks,
				new ArrayList<Module>());
		assertEquals(0, plans.size());

	}

	@Test
	public void testBothParametersAreEmpty() {
		Collection<Map<FunctionBlock, Module>> plans = db.getDistributionPlans(
				new ArrayList<FunctionBlock>(), new ArrayList<Module>());
		assertEquals(0, plans.size());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testBlocksIsNull() {
		Collection<Map<FunctionBlock, Module>> plans = db.getDistributionPlans(null, modules);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBlocksContainsNull() {
		blocks.add(null);
		Collection<Map<FunctionBlock, Module>> plans = db.getDistributionPlans(blocks, modules);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testModulesIsNull() {
		Collection<Map<FunctionBlock, Module>> plans = db.getDistributionPlans(blocks, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testModulesContainsNull() {
		modules.add(null);
		Collection<Map<FunctionBlock, Module>> plans = db.getDistributionPlans(blocks, modules);
	}

	@Test
	public void testGetDistribution() {
		db.setEvaluationStrategy(new MaximalBlockNumberEvaluation());
		Map<FunctionBlock, Module> plan = db.getDistribution(blocks, modules);
		assertTrue(plan.equals(plan2) || plan.equals(plan4) || plan.equals(plan5));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetDistibutionInvalidBlocks() {
		db.setEvaluationStrategy(new MaximalBlockNumberEvaluation());
		Map<FunctionBlock, Module> plan = db.getDistribution(null, modules);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetDistibutionInvalidModules() {
		db.setEvaluationStrategy(new MaximalBlockNumberEvaluation());
		Map<FunctionBlock, Module> plan = db.getDistribution(blocks, null);
	}

	/**
	 * Checks whether the given collection of plans contains the given single plan.
	 * 
	 * @param plans
	 *            the collection of plans
	 * @param plan
	 *            the single plan
	 * @return true if the plan is contained, false otherwise
	 */
	private boolean containsPlan(final Collection<Map<FunctionBlock, Module>> plans,
			final Map<FunctionBlock, Module> plan) {
		if ((plans != null) && (plan != null)) {
			boolean containsPlan = false;
			for (Map<FunctionBlock, Module> singlePlan : plans) {
				for (FunctionBlock block : plan.keySet()) {
					if ((singlePlan.keySet().contains(block))
							&& (singlePlan.get(block).equals(plan.get(block)))) {
						containsPlan = true;
					} else {
						containsPlan = false;
						break;
					}
				}
				if (containsPlan == true) {
					return true;
				}
			}
			return false;
		}
		return false;
	}
}
