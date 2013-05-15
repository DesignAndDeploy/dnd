package edu.teco.dnd.deploy.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite contains all tests for module.
 * 
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ TestDeploymentAgent.class, TestDistributionAlgorithm.class, TestEvaluation.class })
public class DeployTestSuite {

}
