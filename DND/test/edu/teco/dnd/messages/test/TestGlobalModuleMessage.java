package edu.teco.dnd.messages.test;

import static edu.teco.dnd.messages.GlobalModuleMessage.AGENTID_INDEX;
import static edu.teco.dnd.messages.GlobalModuleMessage.MODULEID_INDEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lights.adapters.Tuple;
import lights.adapters.TupleSpaceFactory;
import lights.interfaces.ITuple;
import lime.AgentID;
import lime.LimeServerID;

import edu.teco.dnd.messages.GlobalModuleMessage;
import edu.teco.dnd.module.Module;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class is for testing the GlobalModuleMessage.
 */
public class TestGlobalModuleMessage {
	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "Module";

	/**
	 * The ModuleConfig used to describe the module.
	 */
	private Module module;

	/**
	 * ID of CommunicationAgent.
	 */
	private AgentID agentID;

	/**
	 * ID of the module.
	 */
	private long moduleID;

	/**
	 * message used for testing.
	 */
	private GlobalModuleMessage message;

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
	 * @throws UnknownHostException
	 *             if Host unknown
	 */
	@Before
	public void init() throws UnknownHostException {
		module = new Module();
		agentID = new AgentID(new LimeServerID(InetAddress.getLocalHost()), 2);
		moduleID = module.getID();

		tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addActual(module);
		tuple.addActual(agentID);
		tuple.addActual(moduleID);
	}

	/**
	 * Initializes the message with parameters and tests whether module was set correctly.
	 */
	@Test
	public void testModule3param() {
		message = new GlobalModuleMessage(module, agentID, moduleID);
		assertEquals(module, message.getModule());
	}

	/**
	 * Initializes the message with parameters and tests whether agentID was set correctly.
	 */
	@Test
	public void testAgentID3param() {
		message = new GlobalModuleMessage(module, agentID, moduleID);
		assertEquals(agentID, message.getAgentID());
	}

	/**
	 * Initializes the message with parameters and tests whether moduleID was set correctly.
	 */
	@Test
	public void testModuleID3param() {
		message = new GlobalModuleMessage(module, agentID, moduleID);
		assertEquals(moduleID, message.getModuleID());
	}

	/**
	 * Initializes the message with two parameters and tests whether moduleID was set correctly.
	 */
	@Test
	public void testModule2param() {
		message = new GlobalModuleMessage(module, agentID);
		assertEquals(moduleID, message.getModuleID());
	}

	/**
	 * Initializes the message with two parameters and tests whether moduleID was set correctly.
	 */
	@Test
	public void testAgentID2param() {
		message = new GlobalModuleMessage(module, agentID);
		assertEquals(moduleID, message.getModuleID());
	}

	/**
	 * Initializes the message with two parameters and tests whether moduleID was set correctly.
	 */
	@Test
	public void testModuleID2param() {
		message = new GlobalModuleMessage(module, agentID);
		assertEquals(moduleID, message.getModuleID());
	}

	/**
	 * Initializes the message with moduleID and tests whether moduleID was set correctly.
	 */
	@Test
	public void testModuleID1param() {
		message = new GlobalModuleMessage(moduleID);
		assertEquals(moduleID, message.getModuleID());
	}

	/**
	 * Initializes the message with moduleID and tests whether module was set correctly.
	 */
	@Test
	public void testModule1param() {
		message = new GlobalModuleMessage(moduleID);
		assertNull(message.getModule());
	}

	/**
	 * Initializes the message with moduleID and tests whether agentID was set correctly.
	 */
	@Test
	public void testAgentID1param() {
		message = new GlobalModuleMessage(moduleID);
		assertNull(message.getAgentID());
	}

	/**
	 * Initializes the message without parameters and tests whether module and agentID are null and ModuleID
	 * is 0.
	 */
	@Test
	public void testAgentID0param() {
		message = new GlobalModuleMessage();
		assertEquals(0L, message.getModuleID());
		assertNull(message.getAgentID());
		assertNull(message.getModule());
	}

	/**
	 * Initializes the message with a correct tuple and tests whether agentID was set correctly.
	 */
	@Test
	public void testAgentIDtuple() {
		message = new GlobalModuleMessage(tuple);
		assertEquals(agentID, message.getAgentID());
	}

	/**
	 * Initializes the message with a correct tuple and tests whether moduleID was set correctly.
	 */
	@Test
	public void testmoduleIDtuple() {
		message = new GlobalModuleMessage(tuple);
		assertEquals(moduleID, message.getModuleID());
	}

	/**
	 * Initializes a message with a tuple and tests whether getTuple returns the correct values.
	 */
	@Test
	public void tupleTesttupleInit() {
		message = new GlobalModuleMessage(tuple);
		assertEquals(MESSAGE_IDENTIFIER, message.getTuple().get(0).getValue());
		assertEquals(agentID, message.getTuple().get(AGENTID_INDEX).getValue());
		assertEquals(moduleID, message.getTuple().get(MODULEID_INDEX).getValue());
	}

	/**
	 * Initializes a message with two parameters and tests whether getTuple returns the correct values.
	 */
	@Test
	public void tupleTest3paramInit() {
		message = new GlobalModuleMessage(module, agentID, moduleID);
		assertEquals(MESSAGE_IDENTIFIER, message.getTuple().get(0).getValue());
		assertEquals(agentID, message.getTuple().get(AGENTID_INDEX).getValue());
		assertEquals(moduleID, message.getTuple().get(MODULEID_INDEX).getValue());
	}

	/**
	 * Initializes a message with one parameter and tests if getTuple causes an IllegalArgumentException,
	 * since agentID should be null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void tupleTest1paramInit() {
		message = new GlobalModuleMessage(moduleID);
		message.getTuple();
	}

	/**
	 * Initializes a message without parameters and tests if getTuple causes an IllegalArgumentException,
	 * since both agentID and moduleID are null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void tupleTest0paramInit() {
		message = new GlobalModuleMessage();
		message.getTuple();
	}

	/**
	 * Tests whether getTemplate() returns a template matching to the tuple.
	 */
	@Test
	public void templateToTupleTest() {
		message = new GlobalModuleMessage();
		assertTrue(message.getTemplate().matches(tuple));
	}

	/**
	 * Tests what happens if a message is initialized with a template tuple without existing values.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void templateInitializationTest() {
		message = new GlobalModuleMessage();
		ITuple tuple2 = message.getTemplate();
		message = new GlobalModuleMessage(tuple2);
	}

	/**
	 * Tests the exception in setTuple.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void exceptionTest() {
		message = new GlobalModuleMessage();
		tuple.addActual("too many args");
		message.setTuple(tuple);
	}

}
