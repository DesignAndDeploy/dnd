package edu.teco.dnd.meeting;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.UUID;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.graphiti.BlockType;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This FunctionBlock is used to put a String on a display.
 * 
 */
@BlockType("Meeting")
public class DisplayActorBlock extends FunctionBlock {
	/**
	 * Used for serialization.
	 */
	private static final long serialVersionUID = 8940850363134194731L;

	/**
	 * File name to write to.
	 */
	private static final String FILENAME = "display";

	/**
	 * Logger for this class.
	 */
	private static final Logger LOGGER = LogManager.getLogger(DisplayActorBlock.class);

	/**
	 * Text to show on display.
	 */
	@Input
	private String text;

	/**
	 * Creates new DisplayActorBlock.
	 * 
	 * @param blockID
	 *            ID of new DisplayActorBlock
	 */
	public DisplayActorBlock(final UUID blockID) {
		super(blockID, "DisplayActorBlock1");
	}

	/**
	 * Returns type of this FunctionBlock.
	 * 
	 * @return type of this FunctionBlock
	 */
	@Override
	public String getType() {
		return "actorDisplay";
	}

	/**
	 * Initializes DisplayActorBlock.
	 */
	@Override
	public void init() {
	}

	/**
	 * Puts the input string on a display.
	 */
	@Override
	protected void update() {
		if (text == null || LOGGER == null) {
			return;
		}
		try {
			PrintWriter printWriter = new PrintWriter(new FileOutputStream(FILENAME));
			printWriter.println(text);
			printWriter.close();
		} catch (FileNotFoundException e) {
			LOGGER.catching(Level.WARN, e);
		}
	}

}
