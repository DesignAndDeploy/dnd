package edu.teco.dnd.meeting;

import java.util.Map;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.Output;

/**
 * This operating {@link FunctionBlock} detects if the room is occupied and informs other operating blocks.
 * 
 */
public class MeetingOperatorBlock extends FunctionBlock {
	private static final long serialVersionUID = -2809039301791126409L;

	/**
	 * Type of this block.
	 */
	public static final String BLOCK_TYPE = "operator";

	/**
	 * Threshold to decide whether the light is bright enough to have a meeting or if it is to dark.
	 */
	public static final String OPTION_THRESHOLD = null;

	/**
	 * indicates brightness.
	 */
	private Input<Integer> lights;

	/**
	 * this variable tells whether there is a meeting (true) or not (false).
	 */
	private Output<Boolean> meeting;

	private int threshold;

	/**
	 * Initializes MeetingOperatorBlock.
	 */
	@Override
	public void init(final Map<String, String> options) {
		try {
			threshold = Integer.parseInt(options.get("THRESHOLD"));
		} catch (NumberFormatException e) {
			threshold = Integer.MIN_VALUE;
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
		meeting.setValue(lightsValue > threshold);
	}

}
