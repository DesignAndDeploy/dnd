package edu.teco.dnd.network.tcp.tests;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.ApplicationSpecificMessage;
import edu.teco.dnd.network.messages.DefaultResponse;
import edu.teco.dnd.network.messages.Message;
import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.network.tcp.ClientMessageDispatcher;
import edu.teco.dnd.network.tcp.RemoteIDResolver;
import edu.teco.dnd.network.tcp.ResponseFutureManager;
import edu.teco.dnd.util.UniqueUUIDUtil;

@RunWith(MockitoJUnitRunner.class)
public class ClientMessageDispatcherTest {
	private static ModuleID remoteID;

	@Mock
	private Channel channel;
	@Mock
	private ChannelHandlerContext channelHandlerContext;

	@Mock
	private MessageHandler<Message> handler1;

	@Mock
	private MessageHandler<Message> handler2;

	@Mock
	private Message genericMessage;

	private static ApplicationID applicationID1;
	private static ApplicationID applicationID2;
	@Mock
	private ApplicationSpecificMessage applicationSpecificMessage1;
	@Mock
	private ApplicationSpecificMessage applicationSpecificMessage2;

	@Mock
	private ResponseFutureManager responseFutureManager;

	private ClientMessageDispatcher dispatcher;

	@BeforeClass
	public static void setupIDs() {
		remoteID = new ModuleID();

		final UniqueUUIDUtil uuidUtil = new UniqueUUIDUtil();
		applicationID1 = new ApplicationID(uuidUtil.getNewUUID());
		applicationID2 = new ApplicationID(uuidUtil.getNewUUID());
	}

	@Before
	public void setupDispatcher() {
		when(channelHandlerContext.channel()).thenReturn(channel);
		final RemoteIDResolver resolver = mock(RemoteIDResolver.class);
		when(resolver.getRemoteID(channel)).thenReturn(remoteID);
		dispatcher = new ClientMessageDispatcher(resolver, responseFutureManager);
	}

	@Before
	public void setupMessages() {
		final UniqueUUIDUtil uuidUtil = new UniqueUUIDUtil();
		final UUID messageUUID1 = uuidUtil.getNewUUID();
		final UUID messageUUID2 = uuidUtil.getNewUUID();
		final UUID messageUUID3 = uuidUtil.getNewUUID();

		when(genericMessage.getUUID()).thenReturn(messageUUID1);

		when(applicationSpecificMessage1.getUUID()).thenReturn(messageUUID2);
		when(applicationSpecificMessage1.getApplicationID()).thenReturn(applicationID1);
		when(applicationSpecificMessage2.getUUID()).thenReturn(messageUUID3);
		when(applicationSpecificMessage2.getApplicationID()).thenReturn(applicationID2);
	}

	@Test
	public void testDefaultResponseWithoutHandler() throws Exception {
		dispatcher.channelRead(channelHandlerContext, genericMessage);

		verify(channel).writeAndFlush(isNotNull(DefaultResponse.class));
	}

	@Test
	public void testDefaultResponseWhenException() throws Exception {
		when(handler1.handleMessage(any(ModuleID.class), any(Message.class))).thenThrow(new Exception());
		dispatcher.setDefaultHandler(Message.class, handler1);

		dispatcher.channelRead(channelHandlerContext, genericMessage);

		verify(channel).writeAndFlush(isNotNull(DefaultResponse.class));
	}

	@Test
	public void testDefaultResponseWhenNull() throws Exception {
		when(handler1.handleMessage(any(ModuleID.class), any(Message.class))).thenReturn(null);
		dispatcher.setDefaultHandler(Message.class, handler1);

		dispatcher.channelRead(channelHandlerContext, genericMessage);

		verify(channel).writeAndFlush(isNotNull(DefaultResponse.class));
	}

	@Test
	public void testResponseSent() throws Exception {
		final Response response = mock(Response.class);
		when(handler1.handleMessage(any(ModuleID.class), any(Message.class))).thenReturn(response);
		dispatcher.setDefaultHandler(Message.class, handler1);

		dispatcher.channelRead(channelHandlerContext, genericMessage);

		verify(channel).writeAndFlush(response);
	}

	@Test
	public void testResponseSourceUUIDSet() throws Exception {
		final Response response = mock(Response.class);
		when(handler1.handleMessage(any(ModuleID.class), any(Message.class))).thenReturn(response);
		dispatcher.setDefaultHandler(Message.class, handler1);

		dispatcher.channelRead(channelHandlerContext, genericMessage);

		verify(response).setSourceUUID(genericMessage.getUUID());
	}

	@Test
	public void testDefaultHandlerCalled() throws Exception {
		dispatcher.setDefaultHandler(Message.class, handler1);

		dispatcher.channelRead(channelHandlerContext, genericMessage);

		verify(handler1).handleMessage(remoteID, genericMessage);
	}

	@Test
	public void testApplicationSpecificHandlerCalled() throws Exception {
		dispatcher.setHandler(Message.class, handler1, applicationID1);

		dispatcher.channelRead(channelHandlerContext, applicationSpecificMessage1);

		verify(handler1).handleMessage(remoteID, applicationSpecificMessage1);
	}

	@Test
	public void testApplicationSpecificHandlerPrefered() throws Exception {
		dispatcher.setHandler(Message.class, handler1, applicationID1);
		dispatcher.setDefaultHandler(Message.class, handler2);

		dispatcher.channelRead(channelHandlerContext, applicationSpecificMessage1);

		verify(handler1).handleMessage(remoteID, applicationSpecificMessage1);
		verifyZeroInteractions(handler2);
	}

	@Test
	public void testMultipleApplicationsDefault() throws Exception {
		dispatcher.setDefaultHandler(Message.class, handler1);
		dispatcher.setHandler(Message.class, handler2, applicationID2);

		dispatcher.channelRead(channelHandlerContext, applicationSpecificMessage1);
		dispatcher.channelRead(channelHandlerContext, applicationSpecificMessage2);

		verify(handler1).handleMessage(remoteID, applicationSpecificMessage1);
		verify(handler2).handleMessage(remoteID, applicationSpecificMessage2);
	}

	@Test
	public void testResponse() throws Exception {
		final Response response = mock(Response.class);
		dispatcher.setDefaultHandler(Message.class, handler1);

		dispatcher.channelRead(channelHandlerContext, response);

		verifyZeroInteractions(handler1);
		verify(responseFutureManager).setSuccess(response);
	}
}
