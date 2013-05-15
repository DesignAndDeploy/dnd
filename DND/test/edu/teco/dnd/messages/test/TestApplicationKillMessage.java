package edu.teco.dnd.messages.test;

import static org.junit.Assert.assertTrue;
import lights.adapters.Tuple;
import lights.adapters.TupleSpaceFactory;
import lights.interfaces.ITuple;

import edu.teco.dnd.messages.ApplicationKillMessage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class is for testing the ApplicationKillMessage.
 */
public class TestApplicationKillMessage {

	/**
	 * This is used as the first field in the tuple to identify this type of message.
	 */
	public static final String MESSAGE_IDENTIFIER = "Kill";

	/**
	 * message used for testing.
	 */
	private ApplicationKillMessage message;

	/**
	 * tuple used for testing.
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
	 */
	@Before
	public void init() {
		tuple = new Tuple();
		tuple.addActual(MESSAGE_IDENTIFIER);
	}

	/**
	 * Tests if the correct tuple is returned.
	 */
	@Test
	public void tupleTest() {
		message = new ApplicationKillMessage(tuple);
		assertTrue(tuple.matches(message.getTuple()));
	}

	/**
	 * Tests if a correct template is returned.
	 */
	@Test
	public void templateTest() {
		message = new ApplicationKillMessage();
		assertTrue(message.getTemplate().matches(tuple));
	}

	/**
	 * Tests the exception in setTuple.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void exceptionTest() {
		message = new ApplicationKillMessage();
		tuple.addActual("too many args");
		message.setTuple(tuple);
	}

}
