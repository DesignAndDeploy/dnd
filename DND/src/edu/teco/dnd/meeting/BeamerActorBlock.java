package edu.teco.dnd.meeting;

import java.util.Map;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.graphiti.BlockType;
import edu.teco.dnd.uPart.BeamerControl;

/**
 * This FunctionBlock serves to control the Beamer.
 * 
 */
@BlockType("Meeting")
public class BeamerActorBlock extends FunctionBlock {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8839932451597237481L;
	
	/**
	 * Type of this block.
	 */
	public static final String BLOCK_TYPE = "actorBeamer";

	/**
	 * tells BeamerActorBlock to turn on (true) or switch off (false) the beamer.
	 */
	private Input<Boolean> beamer;

	/**
	 * BeamerControl to control the beamer.
	 */
	private BeamerControl control;

	/**
	 * Initializes BeamerActorBlock.
	 */
	@Override
	public void init(final Map<String, String> options) {
		control = new BeamerControl();
	}

	/**
	 * Shutdown Block.
	 */
	@Override
	public void shutdown() {
	}

	/**
	 * Turns the beamer on or off.
	 */
	@Override
	public void update() {
		if (beamer == null) {
			return;
		}

		// only act on most recent value
		Boolean beamerState = null;
		while (beamer.hasMoreValues()) {
			Boolean state = beamer.popValue();
			if (state != null) {
				beamerState = state;
			}
		}
		if (beamerState != null) {
			if (beamerState) {
				control.activateBeamer();
			} else {
				control.deactivateBeamer();
			}
		}
	}
}
