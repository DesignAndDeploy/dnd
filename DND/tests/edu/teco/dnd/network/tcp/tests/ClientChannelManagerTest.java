package edu.teco.dnd.network.tcp.tests;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.SocketAddress;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.network.tcp.ClientChannelFactory;
import edu.teco.dnd.network.tcp.ClientChannelManager;

@RunWith(MockitoJUnitRunner.class)
public class ClientChannelManagerTest {
	private MockChannel channel1;
	private MockChannel channel2;

	@Mock
	private ClientChannelFactory clientChannelFactory;

	private ModuleID remoteID = new ModuleID();
	
	private ClientChannelManager manager;

	@Before
	public void setup() {
		channel1 = new MockChannel();
		channel2 = new MockChannel();
		
		manager = new ClientChannelManager(clientChannelFactory);
	}

	@Test
	public void testConnect() {
		final ChannelFuture channelFuture = mock(ChannelFuture.class);
		final SocketAddress address = mock(SocketAddress.class);
		when(clientChannelFactory.connect(address)).thenReturn(channelFuture);

		final ChannelFuture actualFuture = manager.connect(address);
		
		verify(clientChannelFactory).connect(address);
		assertEquals(channelFuture, actualFuture);
	}

	@Test
	public void testAddChannel() {
		manager.addChannel(channel1.getChannel());

		assertThat(manager.getChannels(), hasItem(channel1.getChannel()));
	}

	@Test
	public void testAddChannelMultiple() {
		manager.addChannel(channel1.getChannel());
		manager.addChannel(channel2.getChannel());

		assertThat(manager.getChannels(), hasItems(channel1.getChannel(), channel2.getChannel()));
	}

	@Test
	public void testCloseChannel() throws Exception {
		manager.addChannel(channel1.getChannel());

		channel1.close();

		assertThat(manager.getChannels(), not(hasItem(channel1.getChannel())));
	}

	@Test
	public void testCloseChannelWithMultipleChannels() throws Exception {
		manager.addChannel(channel1.getChannel());
		manager.addChannel(channel2.getChannel());

		channel1.close();

		assertThat(manager.getChannels(), not(hasItem(channel1.getChannel())));
	}

	@Test
	public void testCloseOtherChannel() throws Exception {
		manager.addChannel(channel1.getChannel());
		manager.addChannel(channel2.getChannel());

		channel2.close();

		assertThat(manager.getChannels(), hasItem(channel1.getChannel()));
	}

	@Test
	public void testGetRemoteUUIDUnset() {
		manager.addChannel(channel1.getChannel());

		assertNull(manager.getRemoteID(channel1.getChannel()));
	}

	@Test
	public void testGetRemoteUUID() {
		manager.addChannel(channel1.getChannel());

		manager.setRemoteID(channel1.getChannel(), remoteID);

		assertEquals(remoteID, manager.getRemoteID(channel1.getChannel()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetRemoteUUIDWithoutAdd() {
		manager.setRemoteID(channel1.getChannel(), remoteID);
	}

	@Test
	public void testGetChannelsByRemoteUUID() {
		manager.addChannel(channel1.getChannel());
		manager.setRemoteID(channel1.getChannel(), remoteID);

		assertThat(manager.getChannels(remoteID), hasItem(channel1.getChannel()));
		assertThat(manager.getChannels(remoteID), everyItem(isChannelWithRemoteID(remoteID)));
	}

	@Test
	public void testGetMultipleChannelsByRemoteUUID() {
		manager.addChannel(channel1.getChannel());
		manager.setRemoteID(channel1.getChannel(), remoteID);
		manager.addChannel(channel2.getChannel());
		manager.setRemoteID(channel2.getChannel(), remoteID);

		assertThat(manager.getChannels(remoteID), hasItem(channel1.getChannel()));
		assertThat(manager.getChannels(remoteID), hasItem(channel2.getChannel()));
		assertThat(manager.getChannels(remoteID), everyItem(isChannelWithRemoteID(remoteID)));
	}

	@Test
	public void testGetDifferentChannelsByRemoteUUID() {
		manager.addChannel(channel1.getChannel());
		manager.setRemoteID(channel1.getChannel(), remoteID);
		manager.addChannel(channel2.getChannel());

		assertThat(manager.getChannels(remoteID), hasItem(channel1.getChannel()));
		assertThat(manager.getChannels(remoteID), not(hasItem(channel2.getChannel())));
		assertThat(manager.getChannels(remoteID), everyItem(isChannelWithRemoteID(remoteID)));
	}

	@Test
	public void testIsActiveNotAdded() {
		assertFalse(manager.isActive(channel1.getChannel()));
	}

	@Test
	public void testIsActiveUnset() {
		manager.addChannel(channel1.getChannel());

		assertFalse(manager.isActive(channel1.getChannel()));
	}

	@Test
	public void testIsActive() {
		manager.addChannel(channel1.getChannel());

		manager.setActive(channel1.getChannel());

		assertTrue(manager.isActive(channel1.getChannel()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetActiveNotAdded() {
		manager.setActive(channel1.getChannel());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetActiveIfFirstNoUUID() {
		manager.addChannel(channel1.getChannel());

		manager.setActiveIfFirst(channel1.getChannel());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetActiveIfFirstNotAdded() {
		manager.setActive(channel1.getChannel());
	}

	@Test
	public void testSetActiveIfFirst() {
		manager.addChannel(channel1.getChannel());
		manager.setRemoteID(channel1.getChannel(), remoteID);

		assertTrue(manager.setActiveIfFirst(channel1.getChannel()));

		assertTrue(manager.isActive(channel1.getChannel()));
	}

	@Test
	public void testSetActiveIfFirstNotFirst() {
		manager.addChannel(channel1.getChannel());
		manager.setRemoteID(channel1.getChannel(), remoteID);
		manager.addChannel(channel2.getChannel());
		manager.setRemoteID(channel2.getChannel(), remoteID);
		assumeTrue(manager.setActiveIfFirst(channel1.getChannel()));

		assertFalse(manager.setActiveIfFirst(channel2.getChannel()));

		assertFalse(manager.isActive(channel2.getChannel()));
	}

	private Matcher<Channel> isChannelWithRemoteID(final ModuleID remoteID) {
		return new ChannelWithRemoteID(remoteID);
	}

	private final class ChannelWithRemoteID extends TypeSafeMatcher<Channel> {
		private final ModuleID remoteID;

		public ChannelWithRemoteID(final ModuleID remoteID) {
			this.remoteID = remoteID;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("a Channel with remote ID ");
			description.appendValue(remoteID);
		}

		@Override
		public boolean matchesSafely(final Channel channel) {
			final ModuleID channelRemoteUUID = manager.getRemoteID(channel);
			if (remoteID == null) {
				return channelRemoteUUID == null;
			} else {
				return remoteID.equals(channelRemoteUUID);
			}
		}
	}
}
