package edu.teco.dnd.network.tcp.tests;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.network.messages.ConnectionEstablishedMessage;
import edu.teco.dnd.network.messages.HelloMessage;
import edu.teco.dnd.network.tcp.ClientChannelManager;
import edu.teco.dnd.network.tcp.HelloMessageHandler;
import edu.teco.dnd.util.UniqueUUIDUtil;

@RunWith(MockitoJUnitRunner.class)
public class HelloMessageHandlerTest {
	@Mock
	private ChannelHandlerContext channelHandlerContext;

	private ModuleID localUUID;
	private ModuleID lowerUUID;
	private ModuleID higherUUID;

	@Mock
	private ClientChannelManager clientChannelManager;
	private HelloMessageHandler handler;

	@Before
	public void setupContext() {
		when(channelHandlerContext.channel()).thenReturn(mock(Channel.class));
	}

	@Before
	public void setupUUIDAndHandler() {
		final UniqueUUIDUtil util = new UniqueUUIDUtil();
		final List<UUID> uuids = new ArrayList<UUID>(3);
		for (int i = 0; i < 3; i++) {
			uuids.add(util.getNewUUID());
		}
		Collections.sort(uuids);

		lowerUUID = new ModuleID(uuids.get(0));
		localUUID = new ModuleID(uuids.get(1));
		higherUUID = new ModuleID(uuids.get(2));

		handler = new HelloMessageHandler(clientChannelManager, localUUID);
	}

	@Test
	public void testNullUUID() throws Exception {
		handler.channelRead(channelHandlerContext, new HelloMessage(null, 0));

		verify(channelHandlerContext).close();
	}

	@Test
	public void testLocalUUID() throws Exception {
		handler.channelRead(channelHandlerContext, new HelloMessage(localUUID, 0));

		verify(channelHandlerContext).close();
	}

	@Test
	public void testSlaveNew() throws Exception {
		when(clientChannelManager.setActiveIfFirst(any(Channel.class))).thenReturn(true, false);

		handler.channelRead(channelHandlerContext, new HelloMessage(higherUUID, 0));

		final InOrder inOrder = inOrder(clientChannelManager);
		inOrder.verify(clientChannelManager).setRemoteID(channelHandlerContext.channel(), higherUUID);
		inOrder.verify(clientChannelManager).setActiveIfFirst(channelHandlerContext.channel());
		verify(channelHandlerContext, times(1)).writeAndFlush(any(ConnectionEstablishedMessage.class));
		verify(channelHandlerContext, never()).write(any());
	}

	@Test
	public void testSlaveExisting() throws Exception {
		when(clientChannelManager.setActiveIfFirst(any(Channel.class))).thenReturn(false);

		handler.channelRead(channelHandlerContext, new HelloMessage(higherUUID, 0));

		final InOrder inOrder = inOrder(clientChannelManager);
		inOrder.verify(clientChannelManager).setRemoteID(channelHandlerContext.channel(), higherUUID);
		inOrder.verify(clientChannelManager).setActiveIfFirst(channelHandlerContext.channel());
		verify(channelHandlerContext).close();
		verify(channelHandlerContext, never()).write(any());
		verify(channelHandlerContext, never()).writeAndFlush(any());
	}

	@Test
	public void testMaster() throws Exception {
		handler.channelRead(channelHandlerContext, new HelloMessage(lowerUUID, 0));

		verify(clientChannelManager).setRemoteID(channelHandlerContext.channel(), lowerUUID);
		verify(channelHandlerContext, never()).close();
		verify(channelHandlerContext, never()).write(any());
		verify(channelHandlerContext, never()).writeAndFlush(any());
	}
}
