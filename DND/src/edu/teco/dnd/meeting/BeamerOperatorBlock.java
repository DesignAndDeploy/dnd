package edu.teco.dnd.meeting;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.blocks.Output;
import edu.teco.dnd.graphiti.BlockType;

/**
 * This operating FunctionBlock is used to control the Beamer.
 * 
 */
@BlockType("Meeting")
public class BeamerOperatorBlock extends FunctionBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = -889186768492376346L;

	/**
	 * The measured energy at the outlet.
	 */
	private Input<Integer> outlet;

	/**
	 * tells whether there is a meeting (true) or not (false).
	 */
	private Input<Boolean> meeting;

	/**
	 * used to tell BeamerActorBlock to turn on (true) or switch off (false) the beamer.
	 */
	private Output<Boolean> beamer;

	/**
	 * Initializes BeamerOperatorBlock.
	 */
	@Override
	public void init() {
	}

	/**
	 * Checks whether there is a meeting and if the outlet is used to control a BeamerActorBlock. If both is
	 * true: beamer-power activate!
	 */
	@Override
	public void update() {
		if (outlet == null || meeting == null) {
			return;
		}
		Integer outletValue = outlet.popValue();
		Boolean meetingState = meeting.popValue();
		if (outletValue == null || meetingState == null) {
			return;
		}
		beamer.setValue(outletValue > 0 && meetingState);
	}
}
