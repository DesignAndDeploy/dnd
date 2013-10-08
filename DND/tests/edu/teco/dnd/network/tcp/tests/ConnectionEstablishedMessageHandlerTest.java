package edu.teco.dnd.network.tcp.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.teco.dnd.network.messages.ConnectionEstablishedMessage;
import edu.teco.dnd.network.tcp.ClientChannelManager;
import edu.teco.dnd.network.tcp.ConnectionEstablishedMessageHandler;
import edu.teco.dnd.util.UniqueUUIDUtil;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionEstablishedMessageHandlerTest {
	@Mock
	private ChannelHandlerContext channelHandlerContext;
	@Mock
	private ClientChannelManager clientChannelManager;
	
	private UUID localUUID;
	private UUID lowerUUID;
	private UUID higherUUID;
	
	private ConnectionEstablishedMessageHandler handler;
	
	@Before
	public void setup() {
		final UniqueUUIDUtil util = new UniqueUUIDUtil();
		final List<UUID> uuids = new ArrayList<UUID>(3);
		for (int i = 0; i < 3; i++) {
			uuids.add(util.getNewUUID());
		}
		Collections.sort(uuids);

		lowerUUID = uuids.get(0);
		localUUID = uuids.get(1);
		higherUUID = uuids.get(2);
		
		handler = new ConnectionEstablishedMessageHandler(clientChannelManager, localUUID);
		
		when(channelHandlerContext.channel()).thenReturn(mock(Channel.class));
	}
	
	@Test
	public void testDifferentUUID() throws Exception {
		when(clientChannelManager.getRemoteUUID(channelHandlerContext.channel())).thenReturn(lowerUUID);
		
		handler.channelRead(channelHandlerContext, new ConnectionEstablishedMessage(higherUUID));

		verify(channelHandlerContext).close();
		verify(clientChannelManager, never()).setActive(channelHandlerContext.channel());
	}
	
	@Test
	public void testNullUUID() throws Exception {
		handler.channelRead(channelHandlerContext, new ConnectionEstablishedMessage(null));
		
		verify(channelHandlerContext).close();
		verify(clientChannelManager, never()).setActive(channelHandlerContext.channel());
	}
	
	@Test
	public void testLocalUUID() throws Exception {
		handler.channelRead(channelHandlerContext, new ConnectionEstablishedMessage(localUUID));
		
		verify(channelHandlerContext).close();
		verify(clientChannelManager, never()).setActive(channelHandlerContext.channel());
	}
	
	@Test
	public void testValidMessage() throws Exception {
		handler.channelRead(channelHandlerContext, new ConnectionEstablishedMessage(lowerUUID));
		
		verify(channelHandlerContext, never()).close();
		verify(clientChannelManager).setActive(channelHandlerContext.channel());
	}
	
	@Test
	public void testChannelAlreadyActive() throws Exception {
		when(clientChannelManager.isActive(channelHandlerContext.channel())).thenReturn(true);
		
		handler.channelRead(channelHandlerContext, new ConnectionEstablishedMessage(lowerUUID));
		
		verify(channelHandlerContext).close();
	}
}
