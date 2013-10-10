package edu.teco.dnd.network.tests;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.teco.dnd.network.ConnectionListener;
import edu.teco.dnd.network.DelegatingConnectionListener;

@RunWith(MockitoJUnitRunner.class)
public class DelegatingConnectionListenerTest {
	@Mock
	private ConnectionListener listener1;
	@Mock
	private ConnectionListener listener2;
	@Mock
	private ConnectionListener listener3;
	
	private UUID uuid;
	
	private DelegatingConnectionListener delegatingConnectionListener;
	
	@Before
	public void setup() {
		uuid = UUID.randomUUID();
		
		delegatingConnectionListener = new DelegatingConnectionListener();
	}
	
	@Test
	public void testSingleEstablished() {
		delegatingConnectionListener.addListener(listener1);
		
		delegatingConnectionListener.connectionEstablished(uuid);
		
		verify(listener1).connectionEstablished(uuid);
		verify(listener1, never()).connectionClosed(any(UUID.class));
	}
	
	@Test
	public void testMultipleEstablished() {
		delegatingConnectionListener.addListener(listener1);
		delegatingConnectionListener.addListener(listener2);
		delegatingConnectionListener.addListener(listener3);
		
		delegatingConnectionListener.connectionEstablished(uuid);

		verify(listener1).connectionEstablished(uuid);
		verify(listener2).connectionEstablished(uuid);
		verify(listener3).connectionEstablished(uuid);
	}
	
	@Test
	public void testSingleClosed() {
		delegatingConnectionListener.addListener(listener1);
		
		delegatingConnectionListener.connectionClosed(uuid);
		
		verify(listener1).connectionClosed(uuid);
	}
	
	@Test
	public void testMultipleClosed() {
		delegatingConnectionListener.addListener(listener1);
		delegatingConnectionListener.addListener(listener2);
		delegatingConnectionListener.addListener(listener3);
		
		delegatingConnectionListener.connectionClosed(uuid);

		verify(listener1).connectionClosed(uuid);
		verify(listener2).connectionClosed(uuid);
		verify(listener3).connectionClosed(uuid);
	}
}
