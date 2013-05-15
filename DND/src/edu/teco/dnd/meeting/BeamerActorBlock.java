package edu.teco.dnd.meeting;

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
	 * tells BeamerActorBlock to turn on (true) or switch off (false) the beamer.
	 */
	@Input(newOnly = true)
	private Boolean beamer;

	/**
	 * BeamerControl to control the beamer.
	 */
	private BeamerControl control;

	/**
	 * Creates new BeamerActorBlock.
	 * 
	 * @param blockID
	 *            ID of new BeamerActorBlock
	 */
	public BeamerActorBlock(final String blockID) {
		super(blockID);
	}

	/**
	 * Returns type of this FunctionBlock.
	 * 
	 * @return type of this FunctionBlock
	 */
	@Override
	public String getType() {
		return "actorBeamer";
	}

	/**
	 * Initializes BeamerActorBlock.
	 */
	@Override
	public void init() {
		control = new BeamerControl();
	}

	/**
	 * Turns the beamer on or off.
	 */
	@Override
	protected void update() {
		if (beamer == null) {
			return;
		}
		if (beamer) {
			control.activateBeamer();
		} else {
			control.deactivateBeamer();
		}

	}

}
