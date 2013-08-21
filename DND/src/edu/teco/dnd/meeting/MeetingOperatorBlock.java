package edu.teco.dnd.meeting;

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
	private Input<Integer> lights;

	/**
	 * this variable tells whether there is a meeting (true) or not (false).
	 */
	private Output<Boolean> meeting;

	/**
	 * Threshold to decide whether the light is bright enough to have a meeting or if it is to dark.
	 */
	private Option threshold;

	private int _threshold;

	/**
	 * Initializes MeetingOperatorBlock.
	 */
	@Override
	public void init() {
		try {
			_threshold = Integer.parseInt(threshold.getValue());
		} catch (NumberFormatException e) {
			_threshold = Integer.MIN_VALUE;
		}
	}

	/**
	 * Shutdown Block.
	 */
	@Override
	public void shutdown() {
	}

	/**
	 * Checks the lights. If it is bright enough, there is a meeting, and a BeamerOperatingBlock and
	 * DisplayOperatingBlock can be informed thusly.
	 */
	@Override
	public void update() {
		if (lights == null) {
			return;
		}
		Integer lightsValue = lights.popValue();
		if (lightsValue == null) {
			return;
		}
		meeting.setValue(lightsValue > _threshold);
	}

}
