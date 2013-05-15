package edu.teco.dnd.blocks.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite contains all tests for edu.teco.dnd.blocks.
 * 
 * @author philipp
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ FunctionBlockTestSuite.class, TestNullTimer.class, TestSystemTimer.class, TestOutput.class,
		TestSimpleConnectionTarget.class, TestQueuedConnectionTarget.class,
		TestNewValueConnectionTargetDecorator.class })
public class BlocksTestSuite {

}
