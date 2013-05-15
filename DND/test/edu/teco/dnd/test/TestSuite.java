package edu.teco.dnd.test;

import edu.teco.dnd.blocks.test.BlocksTestSuite;
import edu.teco.dnd.deploy.test.DeployTestSuite;
import edu.teco.dnd.messages.test.MessageTestSuite;
import edu.teco.dnd.module.test.ModuleTestSuite;
import edu.teco.dnd.uPart.test.ActorTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Runs all tests.
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ BlocksTestSuite.class, MessageTestSuite.class, DeployTestSuite.class, ModuleTestSuite.class,
		ActorTestSuite.class })
public class TestSuite {

}
