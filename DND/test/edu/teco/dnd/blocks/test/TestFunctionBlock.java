package edu.teco.dnd.blocks.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import edu.teco.dnd.blocks.AssignmentException;
import edu.teco.dnd.blocks.FunctionBlock;

import org.junit.Test;

/**
 * Tests FunctionBlock.
 * 
 * @author philipp
 */
public class TestFunctionBlock {
	/**
	 * A FunctionBlock used for testing {@link FunctionBlock#doUpdate()}.
	 * 
	 * @author philipp
	 */
	private static class UpdateFunctionBlock extends FunctionBlock {
		/**
		 * Used for serialization (as this is a test class it is unused).
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Used to check if {@link #update()} has been called.
		 */
		private boolean hasRun = false;

		/**
		 * Initializes a new UpdateFunctionBlock.
		 * 
		 * @param id
		 *            the id of the FunctionBlock.
		 */
		public UpdateFunctionBlock(final String id) {
			super(id);
		}

		@Override
		public String getType() {
			return null;
		}

		@Override
		public void init() {
		}

		@Override
		protected void update() {
			hasRun = true;
		}
	}

	/**
	 * Tests that the ID is saved correctly.
	 */
	@Test
	public final void testID() {
		assertEquals("foobar", new EmptyFunctionBlock("foobar").getID());
	}

	/**
	 * Tests that null is accepted as an ID.
	 */
	@Test
	public final void testIDNull() {
		assertNull(new EmptyFunctionBlock(null).getID());
	}

	/**
	 * Tests that an empty String is accepted as an ID.
	 */
	@Test
	public final void testIDEmtpy() {
		assertEquals("", new EmptyFunctionBlock("").getID());
	}

	/**
	 * Tests that the default position is null.
	 */
	@Test
	public final void testPositionDefault() {
		assertNull(new EmptyFunctionBlock(null).getPosition());
	}

	/**
	 * Tests that the position is saved correctly.
	 */
	@Test
	public final void testPosition() {
		FunctionBlock block = new EmptyFunctionBlock(null);
		block.setPosition("position");
		assertEquals("position", block.getPosition());
	}

	/**
	 * Tests that overriding a position works correctly.
	 */
	@Test
	public final void testPositionOverride() {
		FunctionBlock block = new EmptyFunctionBlock(null);
		block.setPosition("position1");
		block.setPosition("position2");
		assertEquals("position2", block.getPosition());
	}

	/**
	 * Tests that doUpdate calls update.
	 * 
	 * @throws AssignmentException
	 *             if assignment fails
	 */
	@Test
	public final void testUpdate() throws AssignmentException {
		UpdateFunctionBlock updateFunctionBlock = new UpdateFunctionBlock(null);
		updateFunctionBlock.doUpdate();
		assertTrue(updateFunctionBlock.hasRun);
	}
}
