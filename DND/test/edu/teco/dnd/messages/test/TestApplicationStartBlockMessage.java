package edu.teco.dnd.messages.test;

import static edu.teco.dnd.messages.ApplicationStartBlockMessage.HOLDERID_INDEX;
import static edu.teco.dnd.messages.ApplicationStartBlockMessage.UID_INDEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.UUID;

import lights.adapters.Tuple;
import lights.adapters.TupleSpaceFactory;
import lights.interfaces.ITuple;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.messages.ApplicationStartBlockMessage;
import edu.teco.dnd.messages.ApplicationStartBlockMessage.FunctionBlockHolder;
import edu.teco.dnd.temperature.TemperatureSensorBlock;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class is for testing the ApplicationStartStartBlockMessage.
 */
public class TestApplicationStartBlockMessage {
	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "StartBlock";
	/**
	 * The FunctionBlock to execute.
	 */
	private FunctionBlock functionBlock;

	/**
	 * UID used for testing.
	 */
	private long uid;

	/**
	 * message used for testing.
	 */
	private ApplicationStartBlockMessage message;

	/**
	 * tuple used for testing and initializing message.
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
	 *             if IO problems occur with the functionBlock
	 */
	@Before
	public void init() throws IOException {
		functionBlock = new TemperatureSensorBlock("interestingID");
		uid = UUID.randomUUID().getLeastSignificantBits();

		tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
		tuple.addActual(new ApplicationStartBlockMessage.FunctionBlockHolder(functionBlock));
		tuple.addActual(uid);
	}

	/**
	 * Initializes the message with one parameter and tests whether functionBlock was set correctly.
	 */
	@Test
	public void testFunctionblock1param() {
		message = new ApplicationStartBlockMessage(functionBlock);
		assertEquals(functionBlock, message.getFunctionBlock());
	}

	/**
	 * Initializes the message without parameters and tests whether parameters are null.
	 */
	@Test
	public void testFunctionblock0param() {
		message = new ApplicationStartBlockMessage();
		assertNull(message.getFunctionBlock());
	}

	/**
	 * Initializes the message with a correct tuple and tests whether FunctionBlock was set correctly.
	 */
	@Test
	public void testFunctionblocktuple() {
		message = new ApplicationStartBlockMessage(tuple);
		assertEquals(functionBlock.getID(), message.getFunctionBlock().getID());
		assertEquals(functionBlock.getClass(), message.getFunctionBlock().getClass());
	}

	/**
	 * Initializes a message with a tuple and tests whether getTuple returns the correct values.
	 */
	@Test
	public void tupleTesttupleInit() {
		message = new ApplicationStartBlockMessage(tuple);
		assertEquals(MESSAGE_IDENTIFIER, message.getTuple().get(0).getValue());
		assertEquals(functionBlock.getID(), (new ApplicationStartBlockMessage(message.getTuple()))
				.getFunctionBlock().getID());
		assertEquals(uid, message.getTuple().get(UID_INDEX).getValue());
	}

	/**
	 * Initializes a message with 1 parameter and tests whether getTuple returns the correct values.
	 */
	@Test
	public void tupleTest2paramInit() {
		message = new ApplicationStartBlockMessage(functionBlock);
		assertEquals(MESSAGE_IDENTIFIER, message.getTuple().get(0).getValue());
		try {
			assertEquals(functionBlock.getID(), ((FunctionBlockHolder) message.getTuple().get(HOLDERID_INDEX)
					.getValue()).getFunctionBlock().getID());
			assertEquals(functionBlock.getClass(),
					((FunctionBlockHolder) message.getTuple().get(HOLDERID_INDEX).getValue())
							.getFunctionBlock().getClass());
		} catch (
				ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests whether getTemplate() returns a template matching to the tuple.
	 */
	@Test
	public void templateToTupleTest() {
		message = new ApplicationStartBlockMessage();
		assertTrue(message.getTemplate().matches(tuple));
	}

	/**
	 * Tests what happens if a message is initialized with a template tuple without existing values.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void templateInitializationTest() {
		message = new ApplicationStartBlockMessage();
		ITuple tuple2 = message.getTemplate();
		message = new ApplicationStartBlockMessage(tuple2);
	}

	/**
	 * Tests the exception in setTuple.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void exceptionTest() {
		message = new ApplicationStartBlockMessage();
		tuple.addActual("too many args");
		message.setTuple(tuple);
	}
}
