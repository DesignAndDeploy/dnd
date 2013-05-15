package edu.teco.dnd.module.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite contains all tests for module.
 * 
 * @author philipp
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ TestCommAgent.class, TestModule.class, TestModuleConfig.class, TestAppAgent.class })
public class ModuleTestSuite {

}
