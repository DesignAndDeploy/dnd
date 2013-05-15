package edu.teco.dnd.blocks.test;

import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.Option;
import edu.teco.dnd.blocks.Output;

/**
 * A FunctionBlock adding inputs, options and outputs to another FunctionBlock.
 * 
 * @author philipp
 */
class DerivedFunctionBlock extends MockFunctionBlock {
	/**
	 * Used for serialization (as this is a test class, it is unused).
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * An input.
	 */
	@Input
	private Integer input;

	/**
	 * An output.
	 */
	@SuppressWarnings("unused")
	private Output<Long> output;

	/**
	 * An option.
	 */
	@Option
	private Boolean option;
}
