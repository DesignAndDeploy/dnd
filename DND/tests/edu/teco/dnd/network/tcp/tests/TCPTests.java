package edu.teco.dnd.network.tcp.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ServerChannelManagerTest.class, ClientChannelManagerTest.class, ClientChannelInitializerTest.class,
		HandlersByApplicationIDTest.class, MessageHandlerManagerTest.class, ClientMessageDispatcherTest.class,
		HelloMessageHandlerTest.class, ConnectionEstablishedMessageHandlerTest.class, ResponseFutureManagerTest.class,
		TimeoutResponseInvalidatorTest.class })
public class TCPTests {

}
