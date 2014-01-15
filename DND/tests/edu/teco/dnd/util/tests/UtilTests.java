package edu.teco.dnd.util.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ StringUtilTest.class, DefaultFutureNotifierTest.class, Base64AdapterTest.class,
		UniqueUUIDUtilTest.class, MessageDigestAlgorithmTest.class })
public class UtilTests {

}
