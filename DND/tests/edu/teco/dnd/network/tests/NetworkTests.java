package edu.teco.dnd.network.tests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.teco.dnd.network.tcp.tests.TCPTests;


@RunWith(Suite.class)
@SuiteClasses({ TCPTests.class, DelegatingConnectionListenerTest.class })
public class NetworkTests {

}
