package edu.teco.dnd.network.tcp.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ServerChannelManagerTest.class, ClientChannelManagerTest.class, HelloMessageHandlerTest.class })
public class TCPTests {

}
