package edu.teco.dnd.blocks.test;

import edu.teco.dnd.blocks.FunctionBlock;

/**
 * A FunctionBlock that doesn't define any inputs, outputs or options.
 * 
 * @author philipp
 */
public class EmptyFunctionBlock extends FunctionBlock {
	/**
	 * Used for serialization (as this is a test class it is unused).
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Initializes a new EmptyFunctionBlock.
	 * 
	 * @param id
	 *            the id of the FunctionBlock
	 */
	public EmptyFunctionBlock(final String id) {
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
	}
}
