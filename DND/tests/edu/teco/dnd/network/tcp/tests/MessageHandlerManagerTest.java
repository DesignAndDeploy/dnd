package edu.teco.dnd.network.tcp.tests;

import static org.junit.Assert.assertSame;

import java.util.NoSuchElementException;
import java.util.concurrent.Executor;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Message;
import edu.teco.dnd.network.tcp.MessageHandlerManager;
import edu.teco.dnd.network.tcp.MessageHandlerWithExecutor;
import edu.teco.dnd.util.UUIDFactory;
import edu.teco.dnd.util.UniqueUUIDFactory;

@RunWith(MockitoJUnitRunner.class)
public class MessageHandlerManagerTest {
	private static ApplicationID applicationID1;
	
	private static ApplicationID applicationID2;

	@Mock
	private MessageHandler<Message> handler1;
	
	@Mock
	private Executor executor1;
	
	@Mock
	private MessageHandler<Message> handler2;
	
	@Mock
	private Executor executor2;
	
	private MessageHandlerManager manager;
	
	@BeforeClass
	public static void setupApplicationID() {
		final UUIDFactory uuidFactory = new UniqueUUIDFactory();
		applicationID1 = new ApplicationID(uuidFactory.createUUID());
		applicationID2 = new ApplicationID(uuidFactory.createUUID());
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
		manager.setDefaultHandler(Message.class, handler1, executor1);
		
		final MessageHandlerWithExecutor<Message> result = manager.getDefaultHandler(Message.class);
		assertSame(handler1, result.getMessageHandler());
		assertSame(executor1, result.getExecutor());
	}
	
	@Test
	public void testApplicationSpecificHandler() {
		manager.setHandler(Message.class, handler1, applicationID1, executor1);
		
		final MessageHandlerWithExecutor<Message> result = manager.getHandler(Message.class, applicationID1);
		assertSame(handler1, result.getMessageHandler());
		assertSame(executor1, result.getExecutor());
	}
	
	@Test
	public void testFallback() {
		manager.setDefaultHandler(Message.class, handler1, executor1);
		
		final MessageHandlerWithExecutor<Message> result = manager.getHandler(Message.class, applicationID1);
		assertSame(handler1, result.getMessageHandler());
		assertSame(executor1, result.getExecutor());
	}
	
	@Test
	public void testApplicationSpecificPrefered() {
		manager.setDefaultHandler(Message.class, handler1, executor1);
		manager.setHandler(Message.class, handler2, applicationID1, executor2);
		
		final MessageHandlerWithExecutor<Message> result = manager.getHandler(Message.class, applicationID1);
		assertSame(handler2, result.getMessageHandler());
		assertSame(executor2, result.getExecutor());
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
		manager.setDefaultHandler(Message.class, handler1, executor1);
		
		final MessageHandlerWithExecutor<TestMessageClass> result = manager.getDefaultHandler(TestMessageClass.class);
		assertSame(handler1, result.getMessageHandler());
		assertSame(executor1, result.getExecutor());
	}
	
	@Test
	public void testRecursionApplicationSpecific() {
		manager.setHandler(Message.class, handler1, applicationID1, executor1);
		
		final MessageHandlerWithExecutor<TestMessageClass> result = manager.getHandler(TestMessageClass.class, applicationID1);
		assertSame(handler1, result.getMessageHandler());
		assertSame(executor1, result.getExecutor());
	}
	
	@Test
	public void testRecursionDefaultSubclassPrefered() {
		manager.setDefaultHandler(Message.class, handler1, executor1);
		manager.setDefaultHandler(TestMessageClass.class, handler2, executor2);
		
		final MessageHandlerWithExecutor<TestMessageClass> result = manager.getDefaultHandler(TestMessageClass.class);
		assertSame(handler2, result.getMessageHandler());
		assertSame(executor2, result.getExecutor());
	}
	
	@Test
	public void testRecursionApplicationSpecificSubclassPrefered() {
		manager.setHandler(Message.class, handler1, applicationID1, executor1);
		manager.setHandler(TestMessageClass.class, handler2, applicationID1, executor2);
		
		final MessageHandlerWithExecutor<TestMessageClass> result = manager.getHandler(TestMessageClass.class, applicationID1);
		assertSame(handler2, result.getMessageHandler());
		assertSame(executor2, result.getExecutor());
	}
	
	@Test
	public void testRecursionApplicationSpecificPrefered() {
		manager.setHandler(Message.class, handler1, applicationID1, executor1);
		manager.setDefaultHandler(TestMessageClass.class, handler2, executor2);
		
		final MessageHandlerWithExecutor<TestMessageClass> result = manager.getHandler(TestMessageClass.class, applicationID1);
		assertSame(handler2, result.getMessageHandler());
		assertSame(executor2, result.getExecutor());
	}
	
	@Test
	public void testMultipleApplications() {
		manager.setHandler(Message.class, handler1, applicationID1, executor1);
		manager.setHandler(Message.class, handler2, applicationID2, executor2);

		final MessageHandlerWithExecutor<Message> result1 = manager.getHandler(Message.class, applicationID1);
		final MessageHandlerWithExecutor<Message> result2 = manager.getHandler(Message.class, applicationID2);
		assertSame(handler1, result1.getMessageHandler());
		assertSame(executor1, result1.getExecutor());
		assertSame(handler2, result2.getMessageHandler());
		assertSame(executor2, result2.getExecutor());
	}
	
	@Test
	public void testMultipleApplicationsDefault() {
		manager.setDefaultHandler(Message.class, handler1, executor1);
		manager.setHandler(Message.class, handler2, applicationID2, executor2);
		
		final MessageHandlerWithExecutor<Message> result1 = manager.getHandler(Message.class, applicationID1);
		final MessageHandlerWithExecutor<Message> result2 = manager.getHandler(Message.class, applicationID2);
		assertSame(handler1, result1.getMessageHandler());
		assertSame(executor1, result1.getExecutor());
		assertSame(handler2, result2.getMessageHandler());
		assertSame(executor2, result2.getExecutor());
	}
	
	private final class TestMessageClass extends Message {
	}
}
