package edu.teco.dnd.messages.test;

import static edu.teco.dnd.messages.ApplicationBlockMessage.AGENTID_INDEX;
import static edu.teco.dnd.messages.ApplicationBlockMessage.BLOCKID_INDEX;
import static edu.teco.dnd.messages.ApplicationBlockMessage.UID_INDEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import lights.adapters.Tuple;
import lights.adapters.TupleSpaceFactory;
import lights.interfaces.ITuple;
import lime.AgentID;
import lime.LimeServerID;

import edu.teco.dnd.messages.ApplicationBlockMessage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class is for testing the ApplicationBlockMessage.
 */
public class TestApplicationBlockMessage {

	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "Block";

	/**
	 * AgentID used for Testing.
	 */
	private AgentID agentID;

	/**
	 * blockID used for Testing.
	 */
	private String blockID;

	/**
	 * uid used for Testing.
	 */
	private Long uid;

	/**
	 * message used for testing.
	 */
	private ApplicationBlockMessage message;

	/**
	 * tuple used for testing and initializing the message.
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
	 * Initializes the tuple, agentID, blockID and uid.
	 * 
	 * @throws UnknownHostException
	 *             if Host unknown
	 */
	@Before
	public void init() throws UnknownHostException {
		agentID = new AgentID(new LimeServerID(InetAddress.getLocalHost()), 0);
		blockID = "ID 1337";
		uid = UUID.randomUUID().getLeastSignificantBits();

		tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addActual(agentID);
		tuple.addActual(blockID);
		tuple.addActual(uid);
	}

	/**
	 * Initializes the message with parameters and tests whether agentID was set correctly.
	 */
	@Test
	public void testAgentID2param() {
		message = new ApplicationBlockMessage(agentID, blockID);
		assertEquals(agentID, message.getAgentID());
	}

	/**
	 * Initializes the message with parameters and tests whether blockID was set correctly.
	 */
	@Test
	public void testBlockID2param() {
		message = new ApplicationBlockMessage(agentID, blockID);
		assertEquals(blockID, message.getBlockID());
	}

	/**
	 * Initializes the message with one parameter and tests whether agentID was set correctly.
	 */
	@Test
	public void testAgentID1param() {
		message = new ApplicationBlockMessage(blockID);
		assertNull(message.getAgentID());
	}

	/**
	 * Initializes the message with one parameter and tests whether blockID was set correctly.
	 */
	@Test
	public void testBlockID1param() {
		message = new ApplicationBlockMessage(blockID);
		assertEquals(blockID, message.getBlockID());
	}

	/**
	 * Initializes the message without parameters and tests whether parameters are null.
	 */
	@Test
	public void testAgentID0param() {
		message = new ApplicationBlockMessage();
		assertNull(message.getBlockID());
		assertNull(message.getAgentID());
	}

	/**
	 * Initializes the message with a correct tuple and tests whether agentID was set correctly.
	 */
	@Test
	public void testAgentIDtuple() {
		message = new ApplicationBlockMessage(tuple);
		assertEquals(agentID, message.getAgentID());
	}

	/**
	 * Initializes the message with a correct tuple and tests whether blockID was set correctly.
	 */
	@Test
	public void testBlockIDtuple() {
		message = new ApplicationBlockMessage(tuple);
		assertEquals(blockID, message.getBlockID());
	}

	/**
	 * Initializes a message with a tuple and tests whether getTuple returns the correct values.
	 */
	@Test
	public void tupleTesttupleInit() {
		message = new ApplicationBlockMessage(tuple);
		assertEquals(MESSAGE_IDENTIFIER, message.getTuple().get(0).getValue());
		assertEquals(agentID, message.getTuple().get(AGENTID_INDEX).getValue());
		assertEquals(blockID, message.getTuple().get(BLOCKID_INDEX).getValue());
		assertEquals(uid, message.getTuple().get(UID_INDEX).getValue());
	}

	/**
	 * Initializes a message with two parameters and tests whether getTuple returns the correct values.
	 */
	@Test
	public void tupleTest2paramInit() {
		message = new ApplicationBlockMessage(agentID, blockID);
		assertEquals(MESSAGE_IDENTIFIER, message.getTuple().get(0).getValue());
		assertEquals(agentID, message.getTuple().get(AGENTID_INDEX).getValue());
		assertEquals(blockID, message.getTuple().get(BLOCKID_INDEX).getValue());
	}

	/**
	 * Initializes a message with one parameter and tests if getTuple causes an IllegalArgumentException,
	 * since agentID should be null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void tupleTest1paramInit() {
		message = new ApplicationBlockMessage(blockID);
		message.getTuple();
	}

	/**
	 * Initializes a message without parameters and tests if getTuple causes an IllegalArgumentException,
	 * since both agentID and blockID are null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void tupleTest0paramInit() {
		message = new ApplicationBlockMessage();
		message.getTuple();
	}

	/**
	 * Tests whether getTemplate() returns a template matching to the tuple.
	 */
	@Test
	public void templateToTupleTest() {
		message = new ApplicationBlockMessage();
		assertTrue(message.getTemplate().matches(tuple));
	}

	/**
	 * Tests what happens if a message is initialized with a template tuple without existing values.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void templateInitializationTest() {
		message = new ApplicationBlockMessage();
		ITuple tuple2 = message.getTemplate();
		message = new ApplicationBlockMessage(tuple2);
	}

	/**
	 * Tests the exception in setTuple.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void exceptionTest() {
		message = new ApplicationBlockMessage();
		tuple.addActual("too many args");
		message.setTuple(tuple);
	}

}
