package edu.teco.dnd.deploy.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.deploy.DistributionAlgorithm;
import edu.teco.dnd.deploy.EvaluationStrategy;
import edu.teco.dnd.deploy.MaximalBlockNumberEvaluation;
import edu.teco.dnd.deploy.MinimalBlockNumberEvaluation;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleConfig;

import org.junit.Before;
import org.junit.Test;

/**
 * This class is for testing the evaluation of the distribution plans.
 */
public class TestEvaluation {

	/** Initializes a new DistributionAlgorithm class. */
	private DistributionAlgorithm db = new DistributionAlgorithm();

	/**
	 * Initializes a new MinimalBlockNumberEvaluation class which contains an algorithm that finds the plan
	 * with the least number of modules.
	 */
	private EvaluationStrategy evaluateMinimal = new MinimalBlockNumberEvaluation();

	/**
	 * Initializes a new MaximalBlockNumberEvaluation class which contains an algorithm that finds the plan
	 * with the highest number of modules.
	 */
	private EvaluationStrategy evaluateMaximal = new MaximalBlockNumberEvaluation();

	/** Initializes a new Collection of distribution plans. */
	private Collection<Map<FunctionBlock, Module>> distributionPlan = new ArrayList<>();

	/** Initializes a new distribution plan. */
	private Map<FunctionBlock, Module> planA = new HashMap<>();

	/** Initializes a new distribution plan. */
	private Map<FunctionBlock, Module> planB = new HashMap<>();

	/** Initializes a new distribution plan. */
	private Map<FunctionBlock, Module> planC = new HashMap<>();

	/** Initializes a new distribution plan. */
	private Map<FunctionBlock, Module> planD = new HashMap<>();

	/** Initializes a new FunctionBlock. */
	private FunctionBlockTestFile x = new FunctionBlockTestFile("blockX", "x");

	/** Initializes a new FunctionBlock. */
	private FunctionBlockTestFile y = new FunctionBlockTestFile("blockY", "y");

	/** Initializes a new FunctionBlock. */
	private FunctionBlockTestFile z = new FunctionBlockTestFile("blockZ", "z");

	/** Initializes a new Module. */
	private Module a = new Module(new ModuleConfig());

	/** Initializes a new Module. */
	private Module b = new Module(new ModuleConfig());

	/** Initializes a new Module. */
	private Module c = new Module(new ModuleConfig());

	/** Initializes a new Module. */
	private Module d = new Module(new ModuleConfig());

	/**
	 * Adds the initialized distribution plans to the already initialized collection.
	 */
	@Before
	public void init() {
		planA.put(x, a);
		planA.put(y, a);
		planA.put(z, a);

		planB.put(x, a);
		planB.put(y, b);
		planB.put(z, c);

		planC.put(x, d);
		planC.put(y, b);
		planC.put(z, b);

		planD.put(x, a);
		planD.put(y, a);
		planD.put(z, d);

		distributionPlan.add(planA);
		distributionPlan.add(planB);
		distributionPlan.add(planC);
		distributionPlan.add(planD);

	}

	/**
	 * Tests the setEvaluationStrategy method on an invalid parameter.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetEvaluation() {
		db.setEvaluationStrategy(null);
	}

	/**
	 * Tests the MaximalBlockNumber evaluation strategy.
	 */
	@Test
	public void testEvaluateMaximal() {
		db.setEvaluationStrategy(evaluateMaximal);
		assertNotNull(db.evaluate(distributionPlan));
		assertNotSame(planA, db.evaluate(distributionPlan));
		assertNotSame(planC, db.evaluate(distributionPlan));
		assertNotSame(planD, db.evaluate(distributionPlan));
		assertSame(planB, db.evaluate(distributionPlan));
		assertNull(db.evaluate(new ArrayList<Map<FunctionBlock, Module>>()));
	}

	/**
	 * Tests the MaximalBlockNumber evaluation strategy on an invalid parameter.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEvaluateMaximalOnException() {
		db.evaluate(null);
	}

	/**
	 * Tests the MinimalBlockNumber evaluation strategy.
	 */
	@Test
	public void testEvaluateMinimal() {
		db.setEvaluationStrategy(evaluateMinimal);
		assertNotNull(db.evaluate(distributionPlan));
		assertNotSame(planB, db.evaluate(distributionPlan));
		assertNotSame(planC, db.evaluate(distributionPlan));
		assertNotSame(planD, db.evaluate(distributionPlan));
		assertSame(planA, db.evaluate(distributionPlan));
		assertNull(db.evaluate(new ArrayList<Map<FunctionBlock, Module>>()));
	}

	/**
	 * Tests the MinimalBlockNumber evaluation strategy on an invalid parameter.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEvaluateMinimalOnException() {
		db.evaluate(null);
	}

}
