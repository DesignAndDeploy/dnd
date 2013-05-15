package edu.teco.dnd.messages.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite contains all tests for global messages.
 */
@RunWith(Suite.class)
@SuiteClasses({ TestGlobalApplicationMessage.class, TestGlobalJoinMessage.class,
		TestGlobalModuleMessage.class })
public class GlobalMessageTestSuite {

}
