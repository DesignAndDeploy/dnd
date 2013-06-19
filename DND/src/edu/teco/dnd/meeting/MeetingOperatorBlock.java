package edu.teco.dnd.meeting;

import java.util.UUID;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.Option;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.graphiti.BlockType;

/**
 * This operating {@link FunctionBlock} detects if the room is occupied and informs other operating blocks.
 * 
 */
@BlockType("Meeting")
public class MeetingOperatorBlock extends FunctionBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2809039301791126409L;

	/**
	 * indicates brightness.
	 */
	@Input(newOnly = true)
	private Integer lights;

	/**
	 * this variable tells whether there is a meeting (true) or not (false).
	 */
	private Output<Boolean> meeting;

	/**
	 * Threshold to decide whether the light is bright enough to have a meeting or if it is to dark.
	 */
	@Option
	private Integer threshold;

	/**
	 * Creates new MeetingOperatorBlock.
	 * 
	 * @param blockID
	 *            ID of new MeetingOperatorBlock
	 */
	public MeetingOperatorBlock(final UUID blockID) {
		super(blockID);
	}

	/**
	 * Returns type of this FunctionBlock.
	 * 
	 * @return type of this FunctionBlock
	 */
	@Override
	public String getType() {
		return "operator";
	}

	/**
	 * Initializes MeetingOperatorBlock.
	 */
	@Override
	public void init() {
	}

	/**
	 * Checks the lights. If it is bright enough, there is a meeting, and a BeamerOperatingBlock and
	 * DisplayOperatingBlock can be informed thusly.
	 */
	@Override
	protected void update() {
		if (lights == null || threshold == null) {
			return;
		}
		meeting.setValue(lights > threshold);
	}

}
