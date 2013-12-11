package edu.teco.dnd.network.tcp.tests;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.UUID;
import java.util.concurrent.Executor;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Message;
import edu.teco.dnd.network.tcp.HandlersByApplicationID;
import edu.teco.dnd.network.tcp.MessageHandlerWithExecutor;
import edu.teco.dnd.util.UniqueUUIDUtil;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("deprecation")
public class HandlersByApplicationIDTest {
	private static UUID applicationID1;

	private static UUID applicationID2;

	@Mock
	private MessageHandler<Message> handler1;

	@Mock
	private Executor executor1;

	@Mock
	private MessageHandler<Message> handler2;

	@Mock
	private Executor executor2;

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
	public void testGetDefaultHandlerWithExecutor() {
		handlersByApplicationID.setDefaultHandler(handler1, executor1);

		final MessageHandlerWithExecutor<Message> result = handlersByApplicationID.getDefaultHandlerWithExecutor();
		assertSame(handler1, result.getMessageHandler());
		assertSame(executor1, result.getExecutor());
	}

	@Test
	public void testGetApplicationSpecificHandlerWithExecutor() {
		handlersByApplicationID.setHandler(applicationID1, handler1, executor1);

		final MessageHandlerWithExecutor<Message> result =
				handlersByApplicationID.getApplicationSpecificHandlerWithExecutor(applicationID1);
		assertSame(handler1, result.getMessageHandler());
		assertSame(executor1, result.getExecutor());
	}

	@Test
	public void testGetApplicationSpecificHandlerWithExecutorWithMultiple() {
		handlersByApplicationID.setHandler(applicationID1, handler1, executor1);
		handlersByApplicationID.setHandler(applicationID2, handler2, executor2);

		final MessageHandlerWithExecutor<Message> result1 =
				handlersByApplicationID.getApplicationSpecificHandlerWithExecutor(applicationID1);
		final MessageHandlerWithExecutor<Message> result2 =
				handlersByApplicationID.getApplicationSpecificHandlerWithExecutor(applicationID2);
		assertSame(handler1, result1.getMessageHandler());
		assertSame(executor1, result1.getExecutor());
		assertSame(handler2, result2.getMessageHandler());
		assertSame(executor2, result2.getExecutor());
	}

	@Test
	public void testGetApplicationSpecificHandlerWithExecutorDoesNotReturnDefault() {
		handlersByApplicationID.setDefaultHandler(handler1);

		assertNull(handlersByApplicationID.getApplicationSpecificHandlerWithExecutor(applicationID1));
	}

	@Test
	public void testGetApplicationSpecificWithExecutorUnset() {
		assertNull(handlersByApplicationID.getApplicationSpecificHandlerWithExecutor(applicationID1));
	}

	@Test
	public void testGetHandlerWithExecutorWithoutAny() {
		assertNull(handlersByApplicationID.getHandlerWithExecutor(applicationID1));
	}

	@Test
	public void testGetHandlerWithExecutorWithoutApplicationSpecific() {
		handlersByApplicationID.setDefaultHandler(handler1, executor1);

		final MessageHandlerWithExecutor<Message> result =
				handlersByApplicationID.getHandlerWithExecutor(applicationID1);
		assertSame(handler1, result.getMessageHandler());
		assertSame(executor1, result.getExecutor());
	}

	@Test
	public void testGetHandlerWithExecutorWithoutDefault() {
		handlersByApplicationID.setHandler(applicationID1, handler1, executor1);

		final MessageHandlerWithExecutor<Message> result =
				handlersByApplicationID.getHandlerWithExecutor(applicationID1);
		assertSame(handler1, result.getMessageHandler());
		assertSame(executor1, result.getExecutor());
	}

	@Test
	public void testGetHandlerWithExecutor() {
		handlersByApplicationID.setDefaultHandler(handler1, executor1);
		handlersByApplicationID.setHandler(applicationID1, handler2, executor2);

		final MessageHandlerWithExecutor<Message> result =
				handlersByApplicationID.getHandlerWithExecutor(applicationID1);
		assertSame(handler2, result.getMessageHandler());
		assertSame(executor2, result.getExecutor());
	}

	@Test
	public void testGetHandlerWithExecutorMultiple() {
		handlersByApplicationID.setHandler(applicationID1, handler1, executor1);
		handlersByApplicationID.setHandler(applicationID2, handler2, executor2);

		final MessageHandlerWithExecutor<Message> result1 =
				handlersByApplicationID.getHandlerWithExecutor(applicationID1);
		final MessageHandlerWithExecutor<Message> result2 =
				handlersByApplicationID.getHandlerWithExecutor(applicationID2);
		assertSame(handler1, result1.getMessageHandler());
		assertSame(executor1, result1.getExecutor());
		assertSame(handler2, result2.getMessageHandler());
		assertSame(executor2, result2.getExecutor());
	}

	@Test
	public void testGetHandlerWithExecutorMultipleWithDefault() {
		handlersByApplicationID.setDefaultHandler(handler1, executor1);
		handlersByApplicationID.setHandler(applicationID2, handler2, executor2);

		final MessageHandlerWithExecutor<Message> result1 =
				handlersByApplicationID.getHandlerWithExecutor(applicationID1);
		final MessageHandlerWithExecutor<Message> result2 =
				handlersByApplicationID.getHandlerWithExecutor(applicationID2);
		assertSame(handler1, result1.getMessageHandler());
		assertSame(executor1, result1.getExecutor());
		assertSame(handler2, result2.getMessageHandler());
		assertSame(executor2, result2.getExecutor());
	}

	@Test
	public void testGetDefaultHandler() {
		handlersByApplicationID.setDefaultHandler(handler1);

		assertSame(handler1, handlersByApplicationID.getDefaultHandler());
	}

	@Test
	public void testGetApplicationSpecificHandler() {
		handlersByApplicationID.setHandler(applicationID1, handler1);

		assertSame(handler1, handlersByApplicationID.getApplicationSpecificHandler(applicationID1));
	}

	@Test
	public void testGetApplicationSpecificHandlerWithMultiple() {
		handlersByApplicationID.setHandler(applicationID1, handler1);
		handlersByApplicationID.setHandler(applicationID2, handler2);

		assertSame(handler1, handlersByApplicationID.getHandler(applicationID1));
		assertSame(handler2, handlersByApplicationID.getHandler(applicationID2));
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

		assertSame(handler1, handlersByApplicationID.getHandler(applicationID1));
	}

	@Test
	public void testGetHandlerWithoutDefault() {
		handlersByApplicationID.setHandler(applicationID1, handler1);

		assertSame(handler1, handlersByApplicationID.getHandler(applicationID1));
	}

	@Test
	public void testGetHandler() {
		handlersByApplicationID.setDefaultHandler(handler1);
		handlersByApplicationID.setHandler(applicationID1, handler2);

		assertSame(handler2, handlersByApplicationID.getHandler(applicationID1));
	}

	@Test
	public void testGetHandlerMultiple() {
		handlersByApplicationID.setHandler(applicationID1, handler1);
		handlersByApplicationID.setHandler(applicationID2, handler2);

		assertSame(handler1, handlersByApplicationID.getHandler(applicationID1));
		assertSame(handler2, handlersByApplicationID.getHandler(applicationID2));
	}

	@Test
	public void testGetHandlerMultipleWithDefault() {
		handlersByApplicationID.setDefaultHandler(handler1);
		handlersByApplicationID.setHandler(applicationID2, handler2);

		assertSame(handler1, handlersByApplicationID.getHandler(applicationID1));
		assertSame(handler2, handlersByApplicationID.getHandler(applicationID2));
	}
}
