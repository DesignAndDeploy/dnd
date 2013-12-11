package edu.teco.dnd.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.teco.dnd.module.tests.ModuleTests;
import edu.teco.dnd.network.tests.NetworkTests;
import edu.teco.dnd.util.tests.UtilTests;

@RunWith(Suite.class)
@SuiteClasses({ UtilTests.class, ModuleTests.class, MatcherTests.class, NetworkTests.class })
public class AllTests {

}
