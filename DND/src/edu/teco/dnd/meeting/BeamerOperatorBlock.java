package edu.teco.dnd.meeting;

import java.util.UUID;

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
	@Input
	private Integer outlet;

	/**
	 * tells whether there is a meeting (true) or not (false).
	 */
	@Input
	private Boolean meeting;

	/**
	 * used to tell BeamerActorBlock to turn on (true) or switch off (false) the beamer.
	 */
	private Output<Boolean> beamer;

	/**
	 * Creates new BeamerOperatorBlock.
	 * 
	 * @param blockID
	 *            ID of new BeamerOperatorBlock
	 */
	public BeamerOperatorBlock(final UUID blockID) {
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
	protected void update() {
		if (outlet == null || meeting == null) {
			return;
		}
		beamer.setValue(outlet > 0 && meeting);
	}

}
