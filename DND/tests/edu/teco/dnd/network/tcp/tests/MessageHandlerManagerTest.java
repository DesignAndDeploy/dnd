package edu.teco.dnd.network.tcp.tests;

import static org.junit.Assert.assertSame;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Message;
import edu.teco.dnd.network.tcp.MessageHandlerManager;
import edu.teco.dnd.util.UniqueUUIDUtil;

@RunWith(MockitoJUnitRunner.class)
public class MessageHandlerManagerTest {
	private static UUID applicationID1;
	
	private static UUID applicationID2;

	@Mock
	private MessageHandler<Message> handler1;
	
	@Mock
	private MessageHandler<Message> handler2;
	
	private MessageHandlerManager manager;
	
	@BeforeClass
	public static void setupApplicationID() {
		final UniqueUUIDUtil util = new UniqueUUIDUtil();
		applicationID1 = util.getNewUUID();
		applicationID2 = util.getNewUUID();
	}
	
	@Before
	public void setupManager() {
		manager = new MessageHandlerManager();
	}
	
	@Test(expected = NoSuchElementException.class)
	public void testUndefinedDefault() {
		manager.getDefaultHandler(Message.class);
	}
	
	@Test(expected = NoSuchElementException.class)
	public void testUndefinedApplicationSpecific() {
		manager.getHandler(Message.class, applicationID1);
	}
	
	@Test
	public void testDefaultHandler() {
		manager.setDefaultHandler(Message.class, handler1);
		
		assertSame(handler1, manager.getDefaultHandler(Message.class));
	}
	
	@Test
	public void testApplicationSpecificHandler() {
		manager.setHandler(Message.class, handler1, applicationID1);
		
		assertSame(handler1, manager.getHandler(Message.class, applicationID1));
	}
	
	@Test
	public void testFallback() {
		manager.setDefaultHandler(Message.class, handler1);
		
		assertSame(handler1, manager.getHandler(Message.class, applicationID1));
	}
	
	@Test
	public void testApplicationSpecificPrefered() {
		manager.setDefaultHandler(Message.class, handler1);
		manager.setHandler(Message.class, handler2, applicationID1);
		
		assertSame(handler2, manager.getHandler(Message.class, applicationID1));
	}
	
	@Test(expected = NoSuchElementException.class)
	public void testRecursionDefaultUndefined() {
		manager.getDefaultHandler(TestMessageClass.class);
	}
	
	@Test(expected = NoSuchElementException.class)
	public void testRecursionApplicationSpecificUndefined() {
		manager.getHandler(TestMessageClass.class, applicationID1);
	}
	
	@Test
	public void testRecursionDefault() {
		manager.setDefaultHandler(Message.class, handler1);
		
		assertSame(handler1, manager.getDefaultHandler(TestMessageClass.class));
	}
	
	@Test
	public void testRecursionApplicationSpecific() {
		manager.setHandler(Message.class, handler1, applicationID1);
		
		assertSame(handler1, manager.getHandler(TestMessageClass.class, applicationID1));
	}
	
	@Test
	public void testRecursionDefaultSubclassPrefered() {
		manager.setDefaultHandler(Message.class, handler1);
		manager.setDefaultHandler(TestMessageClass.class, handler2);
		
		assertSame(handler2, manager.getDefaultHandler(TestMessageClass.class));
	}
	
	@Test
	public void testRecursionApplicationSpecificSubclassPrefered() {
		manager.setHandler(Message.class, handler1, applicationID1);
		manager.setHandler(TestMessageClass.class, handler2, applicationID1);
		
		assertSame(handler2, manager.getHandler(TestMessageClass.class, applicationID1));
	}
	
	@Test
	public void testRecursionApplicationSpecificPrefered() {
		manager.setHandler(Message.class, handler1, applicationID1);
		manager.setDefaultHandler(TestMessageClass.class, handler2);
		
		assertSame(handler2, manager.getHandler(TestMessageClass.class, applicationID1));
	}
	
	@Test
	public void testMultipleApplications() {
		manager.setHandler(Message.class, handler1, applicationID1);
		manager.setHandler(Message.class, handler2, applicationID2);

		assertSame(handler1, manager.getHandler(Message.class, applicationID1));
		assertSame(handler2, manager.getHandler(Message.class, applicationID2));
	}
	
	@Test
	public void testMultipleApplicationsDefault() {
		manager.setDefaultHandler(Message.class, handler1);
		manager.setHandler(Message.class, handler2, applicationID2);
		
		assertSame(handler1, manager.getHandler(Message.class, applicationID1));
		assertSame(handler2, manager.getHandler(Message.class, applicationID2));
	}
	
	private final class TestMessageClass extends Message {
	}
}
