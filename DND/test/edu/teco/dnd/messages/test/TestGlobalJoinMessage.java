package edu.teco.dnd.messages.test;

import static edu.teco.dnd.messages.GlobalJoinMessage.APPLICATIONID_INDEX;
import static edu.teco.dnd.messages.GlobalJoinMessage.DEPLOYID_INDEX;
import static edu.teco.dnd.messages.GlobalJoinMessage.UID_INDEX;
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

import edu.teco.dnd.messages.GlobalJoinMessage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class is for testing the GlobalJoinMessage.
 */
public class TestGlobalJoinMessage {

	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "Join";

	/**
	 * ID of application to run on a module.
	 */
	private Integer applicationID;

	/**
	 * ID of the DeploymentAgent.
	 */
	private AgentID agentID;

	/**
	 * uid used for testing.
	 */
	private Long uid;

	/**
	 * message used for testing.
	 */
	private GlobalJoinMessage message;

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
	 *             if Host is unknown
	 */
	@Before
	public void init() throws UnknownHostException {
		applicationID = 2;
		agentID = new AgentID(new LimeServerID(InetAddress.getByName("127.0.0.1")), 0);
		uid = UUID.randomUUID().getLeastSignificantBits();

		tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addActual(applicationID);
		tuple.addActual(agentID);
		tuple.addActual(uid);
	}

	/**
	 * Initializes the message with parameters and tests whether agentID was set correctly.
	 */
	@Test
	public void testAgentID2param() {
		message = new GlobalJoinMessage(applicationID, agentID);
		assertEquals(agentID, message.getDeploymentAgentID());
	}

	/**
	 * Initializes the message with parameters and tests whether applicationID was set correctly.
	 */
	@Test
	public void testapplicationID2param() {
		message = new GlobalJoinMessage(applicationID, agentID);
		assertEquals(applicationID, message.getApplicationID());
	}

	/**
	 * Initializes the message without parameters and tests whether parameters are null.
	 */
	@Test
	public void testAgentID0param() {
		message = new GlobalJoinMessage();
		assertNull(message.getApplicationID());
		assertNull(message.getDeploymentAgentID());
	}

	/**
	 * Initializes the message with a correct tuple and tests whether agentID was set correctly.
	 */
	@Test
	public void testAgentIDtuple() {
		message = new GlobalJoinMessage(tuple);
		assertEquals(agentID, message.getDeploymentAgentID());
	}

	/**
	 * Initializes the message with a correct tuple and tests whether applicationID was set correctly.
	 */
	@Test
	public void testapplicationIDtuple() {
		message = new GlobalJoinMessage(tuple);
		assertEquals(applicationID, message.getApplicationID());
	}

	/**
	 * Initializes a message with a tuple and tests whether getTuple returns the correct values.
	 */
	@Test
	public void tupleTesttupleInit() {
		message = new GlobalJoinMessage(tuple);
		assertEquals(MESSAGE_IDENTIFIER, message.getTuple().get(0).getValue());
		assertEquals(agentID, message.getTuple().get(DEPLOYID_INDEX).getValue());
		assertEquals(applicationID, message.getTuple().get(APPLICATIONID_INDEX).getValue());
		assertEquals(uid, message.getTuple().get(UID_INDEX).getValue());
	}

	/**
	 * Initializes a message with two parameters and tests whether getTuple returns the correct values.
	 */
	@Test
	public void tupleTest2paramInit() {
		message = new GlobalJoinMessage(applicationID, agentID);
		assertEquals(MESSAGE_IDENTIFIER, message.getTuple().get(0).getValue());
		assertEquals(agentID, message.getTuple().get(DEPLOYID_INDEX).getValue());
		assertEquals(applicationID, message.getTuple().get(APPLICATIONID_INDEX).getValue());
	}

	/**
	 * Initializes a message without parameters and tests if getTuple causes an IllegalArgumentException,
	 * since both agentID and applicationID are null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void tupleTest0paramInit() {
		message = new GlobalJoinMessage();
		message.getTuple();
	}

	/**
	 * Tests whether getTemplate() returns a template matching to the tuple.
	 */
	@Test
	public void templateToTupleTest() {
		message = new GlobalJoinMessage();
		assertTrue(message.getTemplate().matches(tuple));
	}

	/**
	 * Tests what happens if a message is initialized with a template tuple without existing values.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void templateInitializationTest() {
		message = new GlobalJoinMessage();
		ITuple tuple2 = message.getTemplate();
		message = new GlobalJoinMessage(tuple2);
	}

	/**
	 * Tests the exception in setTuple.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void exceptionTest() {
		message = new GlobalJoinMessage();
		tuple.addActual("too many args");
		message.setTuple(tuple);
	}

}
