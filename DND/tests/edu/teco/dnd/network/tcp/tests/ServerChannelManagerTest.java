package edu.teco.dnd.network.tcp.tests;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.teco.dnd.network.tcp.ServerChannelFactory;
import edu.teco.dnd.network.tcp.ServerChannelManager;
import edu.teco.dnd.util.FutureNotifier;

@RunWith(MockitoJUnitRunner.class)
public class ServerChannelManagerTest {
	@Mock
	private ServerChannelFactory serverChannelFactory;

	private SocketAddress address1;

	private MockChannel channel1;

	private SocketAddress address2;

	private MockChannel channel2;

	private ServerChannelManager manager;

	@Before
	public void setup() {
		address1 = new InetSocketAddress(5000);
		channel1 = new MockChannel();
		when(serverChannelFactory.bind(address1)).thenReturn(channel1.getChannelFutureNotifier());

		address2 = new InetSocketAddress(5001);
		channel2 = new MockChannel();
		when(serverChannelFactory.bind(address2)).thenReturn(channel2.getChannelFutureNotifier());

		manager = new ServerChannelManager(serverChannelFactory);
	}

	@Test
	public void testBindCalled() {
		manager.bind(address1);

		verify(serverChannelFactory).bind(address1);
	}

	@Test
	public void testBindChannelAdded() {
		manager.bind(address1);

		assertThat(manager.getChannels(), hasItem(channel1.getChannel()));
	}

	@Test
	public void testRemovedAfterClose() throws Exception {
		manager.bind(address1);

		channel1.close();

		assertThat(manager.getChannels(), not(hasItem(channel1.getChannel())));
	}

	@Test
	public void testBindMultiple() {
		manager.bind(address1);
		manager.bind(address2);

		assertThat(manager.getChannels(), hasItems(channel1.getChannel(), channel2.getChannel()));
	}

	@Test
	public void testRemovedAfterCloseWithMultipleBinds() throws Exception {
		manager.bind(address1);
		manager.bind(address2);

		channel1.close();

		assertThat(manager.getChannels(), not(hasItem(channel1.getChannel())));
	}

	@Test
	public void testChannelKeptAfterCloseOther() throws Exception {
		manager.bind(address1);
		manager.bind(address2);

		channel1.close();

		assertThat(manager.getChannels(), hasItem(channel2.getChannel()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBindNull() {
		manager.bind(null);
	}
	
	@Test
	public void testCloseAllChannelsWithoutChannels() {
		final FutureNotifier<Collection<Void>> closeFuture = manager.closeAllChannels();
		
		assertTrue(closeFuture.isDone());
		assertTrue(closeFuture.isSuccess());
	}
	
	@Test
	public void testCloseAllChannelsWithSingleChannel() throws Exception {
		manager.bind(address1);
		
		final FutureNotifier<Collection<Void>> closeFuture = manager.closeAllChannels();
		verify(channel1.getChannel()).close();
		channel1.close();
		
		assertTrue(closeFuture.isDone());
		assertTrue(closeFuture.isSuccess());
	}
	
	@Test
	public void testCloseAllChannelsOnlyAfterChannelClosed() {
		manager.bind(address1);
		
		final FutureNotifier<Collection<Void>> closeFuture = manager.closeAllChannels();
		
		assertFalse(closeFuture.isDone());
	}
	
	@Test
	public void testCloseAllChannelsWithMultipleChannels() throws Exception {
		manager.bind(address1);
		manager.bind(address2);
		
		final FutureNotifier<Collection<Void>> closeFuture = manager.closeAllChannels();
		verify(channel1.getChannel()).close();
		verify(channel2.getChannel()).close();
		channel1.close();
		channel2.close();
		
		assertTrue(closeFuture.isDone());
		assertTrue(closeFuture.isSuccess());
	}
	
	@Test
	public void testCloseAllChannelsOnlyAfterAllChannelsClosed() throws Exception {
		manager.bind(address1);
		manager.bind(address2);
		
		final FutureNotifier<Collection<Void>> closeFuture = manager.closeAllChannels();
		channel2.close();
		
		assertFalse(closeFuture.isDone());
	}
}
