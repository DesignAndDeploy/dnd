package edu.teco.dnd.messages.test;

import static edu.teco.dnd.messages.ApplicationModuleMessage.AGENTID_INDEX;
import static edu.teco.dnd.messages.ApplicationModuleMessage.MODULECONFIG_INDEX;
import static edu.teco.dnd.messages.ApplicationModuleMessage.MODULEID_INDEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetAddress;

import lights.adapters.Tuple;
import lights.adapters.TupleSpaceFactory;
import lights.interfaces.ITuple;
import lime.AgentID;
import lime.LimeServerID;

import edu.teco.dnd.messages.ApplicationModuleMessage;
import edu.teco.dnd.module.ModuleConfig;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class is for testing the ApplicationModuleMessage.
 */
public class TestApplicationModuleMessage {
	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "Module";

	/**
	 * Contains the ID of the module.
	 */
	private Long moduleID;

	/**
	 * ID of the ApplicationAgent.
	 */
	private AgentID agentID;

	/**
	 * The ModuleConfig used to describe the module.
	 */
	private ModuleConfig moduleConfig;

	/**
	 * message used for testing.
	 */
	private ApplicationModuleMessage message;

	/**
	 * tuple used for testing and initializing.
	 */
	private ITuple tuple;

	/**
	 * Initializes the TupleSpaceFactory.
	 * 
	 * @throws ClassNotFoundException
	 *             if Class not Found
	 */
	@BeforeClass
	public static void initLights() throws ClassNotFoundException {
		TupleSpaceFactory.setFactory("lights.adapters.builtin.TupleSpaceFactory");
	}

	/**
	 * Initializes the tuple and messages for testing.
	 * 
	 * @throws IOException
	 *             trying to get the AgentID
	 */
	@Before
	public void init() throws IOException {
		moduleID = 1L;
		agentID = new AgentID(new LimeServerID(InetAddress.getLocalHost()), 0);
		moduleConfig = new ModuleConfig();

		tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addActual(moduleID);
		tuple.addActual(agentID);
		tuple.addActual(moduleConfig);
	}

	/**
	 * Initializes the message with parameters and tests whether moduleID was set correctly.
	 */
	@Test
	public void testmoduleID2param() {
		message = new ApplicationModuleMessage(moduleID, agentID, moduleConfig);
		assertEquals(moduleID, message.getModuleID());
	}

	/**
	 * Initializes the message with parameters and tests whether agentID was set correctly.
	 */
	@Test
	public void testAgentID3param() {
		message = new ApplicationModuleMessage(moduleID, agentID, moduleConfig);
		assertEquals(agentID, message.getAgentID());
	}

	/**
	 * Initializes the message with parameters and tests whether moduleConfig was set correctly.
	 */
	@Test
	public void testmoduleID3param() {
		message = new ApplicationModuleMessage(moduleID, agentID, moduleConfig);
		assertEquals(moduleConfig, message.getModuleConfig());
	}

	/**
	 * Initializes the message without parameters and tests whether parameters are null.
	 */
	@Test
	public void testAgentID0param() {
		message = new ApplicationModuleMessage();
		assertNull(message.getModuleID());
		assertNull(message.getAgentID());
		assertNull(message.getModuleConfig());
	}

	/**
	 * Initializes the message with a correct tuple and tests whether agentID was set correctly.
	 */
	@Test
	public void testAgentIDtuple() {
		message = new ApplicationModuleMessage(tuple);
		assertEquals(agentID, message.getAgentID());
	}

	/**
	 * Initializes the message with a correct tuple and tests whether moduleID was set correctly.
	 */
	@Test
	public void testmoduleIDtuple() {
		message = new ApplicationModuleMessage(tuple);
		assertEquals(moduleID, message.getModuleID());
	}

	/**
	 * Initializes a message with a tuple and tests whether getTuple returns the correct values.
	 */
	@Test
	public void tupleTesttupleInit() {
		message = new ApplicationModuleMessage(tuple);
		assertEquals(MESSAGE_IDENTIFIER, message.getTuple().get(0).getValue());
		assertEquals(agentID, message.getTuple().get(AGENTID_INDEX).getValue());
		assertEquals(moduleID, message.getTuple().get(MODULEID_INDEX).getValue());
		assertEquals(moduleConfig, message.getTuple().get(MODULECONFIG_INDEX).getValue());
	}

	/**
	 * Initializes a message with 3 parameters and tests whether getTuple returns the correct values.
	 */
	@Test
	public void tupleTest3paramInit() {
		message = new ApplicationModuleMessage(moduleID, agentID, moduleConfig);
		assertEquals(MESSAGE_IDENTIFIER, message.getTuple().get(0).getValue());
		assertEquals(agentID, message.getTuple().get(AGENTID_INDEX).getValue());
		assertEquals(moduleID, message.getTuple().get(MODULEID_INDEX).getValue());
		assertEquals(moduleConfig, message.getTuple().get(MODULECONFIG_INDEX).getValue());
	}

	/**
	 * Initializes a message without parameters and tests if getTuple causes an IllegalArgumentException,
	 * since both agentID and moduleID are null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void tupleTest0paramInit() {
		message = new ApplicationModuleMessage();
		message.getTuple();
	}

	/**
	 * Tests whether getTemplate() returns a template matching to the tuple.
	 */
	@Test
	public void templateToTupleTest() {
		message = new ApplicationModuleMessage();
		assertTrue(message.getTemplate().matches(tuple));
	}

	/**
	 * Tests what happens if a message is initialized with a template tuple without existing values.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void templateInitializationTest() {
		message = new ApplicationModuleMessage();
		ITuple tuple2 = message.getTemplate();
		message = new ApplicationModuleMessage(tuple2);
	}

	/**
	 * Tests the exception in setTuple.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void exceptionTest() {
		message = new ApplicationModuleMessage();
		tuple.addActual("too many args");
		message.setTuple(tuple);
	}

}