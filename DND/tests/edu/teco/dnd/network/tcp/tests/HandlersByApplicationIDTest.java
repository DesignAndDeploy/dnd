package edu.teco.dnd.network.tcp.tests;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Message;
import edu.teco.dnd.network.tcp.HandlersByApplicationID;
import edu.teco.dnd.util.UniqueUUIDUtil;

@RunWith(MockitoJUnitRunner.class)
public class HandlersByApplicationIDTest {
	private static UUID applicationID1;
	
	private static UUID applicationID2;
	
	@Mock
	private MessageHandler<Message> handler1;
	
	@Mock
	private MessageHandler<Message> handler2;
	
	private HandlersByApplicationID<Message> handlersByApplicationID;
	
	@BeforeClass
	public static void setupApplicationID() {
		final UniqueUUIDUtil util = new UniqueUUIDUtil();
		applicationID1 = util.getNewUUID();
		applicationID2 = util.getNewUUID();
	}
	
	@Before
	public void setupObjectToBeTested() {
		handlersByApplicationID = new HandlersByApplicationID<Message>();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetHandlerApplicationIDNull() {
		handlersByApplicationID.setHandler(null, handler1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetHandlerHandlerNull() {
		handlersByApplicationID.setHandler(applicationID1, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetDefaultHandlerNull() {
		handlersByApplicationID.setDefaultHandler(null);
	}
	
	@Test
	public void testGetDefaultHandler() {
		handlersByApplicationID.setDefaultHandler(handler1);
		
		assertSame(handlersByApplicationID.getDefaultHandler(), handler1);
	}
	
	@Test
	public void testGetApplicationSpecificHandler() {
		handlersByApplicationID.setHandler(applicationID1, handler1);
		
		assertSame(handlersByApplicationID.getApplicationSpecificHandler(applicationID1), handler1);
	}
	
	@Test
	public void testGetApplicationSpecificHandlerWithMultiple() {
		handlersByApplicationID.setHandler(applicationID1, handler1);
		handlersByApplicationID.setHandler(applicationID2, handler2);
	
		assertSame(handlersByApplicationID.getHandler(applicationID1), handler1);
		assertSame(handlersByApplicationID.getHandler(applicationID2), handler2);
	}

	@Test
	public void testGetApplicationSpecificHandlerDoesNotReturnDefault() {
		handlersByApplicationID.setDefaultHandler(handler1);
		
		assertNull(handlersByApplicationID.getApplicationSpecificHandler(applicationID1));
	}
	
	@Test
	public void testGetApplicationSpecificUnset() {
		assertNull(handlersByApplicationID.getApplicationSpecificHandler(applicationID1));
	}
	
	@Test
	public void testGetHandlerWithoutAny() {
		assertNull(handlersByApplicationID.getHandler(applicationID1));
	}

	@Test
	public void testGetHandlerWithoutApplicationSpecific() {
		handlersByApplicationID.setDefaultHandler(handler1);
		
		assertSame(handlersByApplicationID.getHandler(applicationID1), handler1);
	}
	
	@Test
	public void testGetHandlerWithoutDefault() {
		handlersByApplicationID.setHandler(applicationID1, handler1);
		
		assertSame(handlersByApplicationID.getHandler(applicationID1), handler1);
	}
	
	@Test
	public void testGetHandler() {
		handlersByApplicationID.setDefaultHandler(handler1);
		handlersByApplicationID.setHandler(applicationID1, handler2);
		
		assertSame(handlersByApplicationID.getHandler(applicationID1), handler2);
	}
	
	@Test
	public void testGetHandlerMultiple() {
		handlersByApplicationID.setHandler(applicationID1, handler1);
		handlersByApplicationID.setHandler(applicationID2, handler2);

		assertSame(handlersByApplicationID.getHandler(applicationID1), handler1);
		assertSame(handlersByApplicationID.getHandler(applicationID2), handler2);
	}
	
	@Test
	public void testGetHandlerMultipleWithDefault() {
		handlersByApplicationID.setDefaultHandler(handler1);
		handlersByApplicationID.setHandler(applicationID2, handler2);

		assertSame(handlersByApplicationID.getHandler(applicationID1), handler1);
		assertSame(handlersByApplicationID.getHandler(applicationID2), handler2);
	}
}
