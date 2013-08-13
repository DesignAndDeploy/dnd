package edu.teco.dnd.meeting;

import java.util.UUID;

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
	 * this variable tells whether there is a meeting (true) or not (false).
	 */
	@Input
	private Boolean meeting;

	/**
	 * Text to show on display.
	 */
	private Output<String> text;

	/**
	 * Creates new DisplayOperatorBlock.
	 * 
	 * @param blockID
	 *            ID of new DisplayOperatorBlock
	 */
	public DisplayOperatorBlock(final UUID blockID) {
		super(blockID, "DisplayOperatorBlock1");
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
	 * Initializes DisplayOperatorBlock.
	 */
	@Override
	public void init() {
	}

	/**
	 * Checks if there is a meeting and accordingly sets the text to be shown on the display ("frei" oder "besetzt").
	 */
	@Override
	protected void update() {
		if (meeting == null) {
			return;
		}
		if (meeting) {
			text.setValue("besetzt");
		} else {
			text.setValue("frei");
		}
	}

}
