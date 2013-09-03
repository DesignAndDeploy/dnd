package edu.teco.dnd.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.teco.dnd.module.tests.ModuleTests;
import edu.teco.dnd.util.tests.UtilTests;

@RunWith(Suite.class)
@SuiteClasses({ UtilTests.class, ModuleTests.class, ContainsInOrderTest.class })
public class AllTests {

}
