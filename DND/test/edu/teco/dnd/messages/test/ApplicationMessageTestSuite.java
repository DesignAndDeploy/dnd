package edu.teco.dnd.messages.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite contains all tests for application messages.
 */
@RunWith(Suite.class)
@SuiteClasses({ TestApplicationBlockMessage.class, TestApplicationBlockStartErrorMessage.class,
		TestApplicationClassLoadedMessage.class, TestApplicationKillMessage.class,
		TestApplicationLoadClassErrorMessage.class, TestApplicationLoadClassMessage.class,
		TestApplicationModuleMessage.class, TestApplicationStartBlockMessage.class })
public class ApplicationMessageTestSuite {

}
