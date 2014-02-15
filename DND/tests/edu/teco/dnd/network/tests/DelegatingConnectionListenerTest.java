package edu.teco.dnd.network.tests;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.teco.dnd.module.ModuleID;
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
	
	private ModuleID moduleID;
	
	private DelegatingConnectionListener delegatingConnectionListener;
	
	@Before
	public void setup() {
		moduleID = new ModuleID();
		
		delegatingConnectionListener = new DelegatingConnectionListener();
	}
	
	@Test
	public void testSingleEstablished() {
		delegatingConnectionListener.addListener(listener1);
		
		delegatingConnectionListener.connectionEstablished(moduleID);
		
		verify(listener1).connectionEstablished(moduleID);
		verify(listener1, never()).connectionClosed(any(ModuleID.class));
	}
	
	@Test
	public void testMultipleEstablished() {
		delegatingConnectionListener.addListener(listener1);
		delegatingConnectionListener.addListener(listener2);
		delegatingConnectionListener.addListener(listener3);
		
		delegatingConnectionListener.connectionEstablished(moduleID);

		verify(listener1).connectionEstablished(moduleID);
		verify(listener2).connectionEstablished(moduleID);
		verify(listener3).connectionEstablished(moduleID);
	}
	
	@Test
	public void testSingleClosed() {
		delegatingConnectionListener.addListener(listener1);
		
		delegatingConnectionListener.connectionClosed(moduleID);
		
		verify(listener1).connectionClosed(moduleID);
	}
	
	@Test
	public void testMultipleClosed() {
		delegatingConnectionListener.addListener(listener1);
		delegatingConnectionListener.addListener(listener2);
		delegatingConnectionListener.addListener(listener3);
		
		delegatingConnectionListener.connectionClosed(moduleID);

		verify(listener1).connectionClosed(moduleID);
		verify(listener2).connectionClosed(moduleID);
		verify(listener3).connectionClosed(moduleID);
	}
}
