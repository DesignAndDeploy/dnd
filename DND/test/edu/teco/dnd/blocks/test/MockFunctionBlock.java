package edu.teco.dnd.blocks.test;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.Option;
import edu.teco.dnd.blocks.Output;

/**
 * A FunctionBlock used only for testing.
 * 
 * @author philipp
 */
public class MockFunctionBlock extends FunctionBlock {
	/**
	 * Used for serialization (as this class is only for testing this is unused).
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * An input of type String.
	 */
	@Input
	private String stringInput = "foobar";

	/**
	 * An input of type Integer.
	 */
	@Input
	private Integer integerInput = 0;

	/**
	 * An unqueued input.
	 */
	@Input(false)
	private Boolean unqueuedInput;

	/**
	 * A queued input.
	 */
	@Input(true)
	private Boolean queuedInput = false;

	/**
	 * An unqueued input that only updates on changed values.
	 */
	@Input(value = false, newOnly = true)
	private Boolean unqueuedNewOnlyInput;

	/**
	 * An queued input that only updates on changed values.
	 */
	@Input(value = true, newOnly = true)
	private Boolean queuedNewOnlyInput;

	/**
	 * An output of type String.
	 */
	@SuppressWarnings("unused")
	private Output<String> stringOutput;

	/**
	 * An output of type Integer.
	 */
	@SuppressWarnings("unused")
	private Output<Integer> integerOutput;

	/**
	 * An output of type Boolean.
	 */
	@SuppressWarnings("unused")
	private Output<Boolean> booleanOutput;

	/**
	 * An option of type Integer.
	 */
	@Option
	private Integer integerOption = 0;

	/**
	 * An option of type String.
	 */
	@Option
	private String stringOption = "foobar";

	/**
	 * A field that is not serializable.
	 */
	@SuppressWarnings("unused")
	private Object object;

	/**
	 * Initializes a new MockFunctionBlock.
	 * 
	 * @param id
	 *            the id of the FunctionBlock
	 */
	public MockFunctionBlock(final String id) {
		super(id);
	}

	/**
	 * Initializes a new MockFunctionBlock with a default id.
	 */
	public MockFunctionBlock() {
		this("id");
	}

	/**
	 * Returns the value of integerInput.
	 * 
	 * @return the value of integerInput
	 */
	public final int getIntegerInputValue() {
		return this.integerInput;
	}

	/**
	 * Returns the value of stringInput.
	 * 
	 * @return the value of stringInput
	 */
	public final String getStringInputValue() {
		return this.stringInput;
	}

	/**
	 * Returns the value of queuedInput.
	 * 
	 * @return the value of queuedInput
	 */
	public final Boolean getQueuedInputValue() {
		return this.queuedInput;
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
