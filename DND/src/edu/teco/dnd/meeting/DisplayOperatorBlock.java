package edu.teco.dnd.meeting;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.graphiti.BlockType;

/**
 * This operating FunctionBlock controls the messages that are to be shown on a display.
 * 
 */
@BlockType("Meeting")
public class DisplayOperatorBlock extends FunctionBlock {
	/**
	 * 
	 */
	private static final long serialVersionUID = 87890179892296397L;

	/**
	 * Type of this block.
	 */
	public static final String BLOCK_TYPE = "operator";

	/**
	 * this variable tells whether there is a meeting (true) or not (false).
	 */
	private Input<Boolean> meeting;

	/**
	 * Text to show on display.
	 */
	private Output<String> text;

	/**
	 * Initializes DisplayOperatorBlock.
	 */
	@Override
	public void init() {
	}

	/**
	 * Shutdown Block.
	 */
	@Override
	public void shutdown() {
	}

	/**
	 * Checks if there is a meeting and accordingly sets the text to be shown on the display ("frei" oder "besetzt").
	 */
	@Override
	public void update() {
		if (meeting == null) {
			return;
		}
		Boolean meetingState = null;
		while (meeting.hasMoreValues()) {
			Boolean state = meeting.popValue();
			if (state != null) {
				meetingState = state;
			}
		}
		if (meetingState != null) {
			if (meetingState) {
				text.setValue("besetzt");
			} else {
				text.setValue("frei");
			}
		}
	}

}
