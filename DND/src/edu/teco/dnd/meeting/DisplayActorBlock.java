package edu.teco.dnd.meeting;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.blocks.Input;
import edu.teco.dnd.graphiti.BlockType;

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
	 * Type of this block.
	 */
	public static final String BLOCK_TYPE = "actorDisplay";

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
	private Input<String> text;

	/**
	 * Initializes DisplayActorBlock.
	 */
	@Override
	public void init(final Map<String, String> options) {
	}

	/**
	 * Shutdown Block.
	 */
	@Override
	public void shutdown() {
	}

	/**
	 * Puts the input string on a display.
	 */
	@Override
	public void update() {
		if (text == null) {
			return;
		}
		try {
			PrintWriter printWriter = new PrintWriter(new FileOutputStream(FILENAME));
			printWriter.println(text.popValue());
			printWriter.close();
		} catch (FileNotFoundException e) {
			LOGGER.catching(Level.WARN, e);
		}
	}

}
