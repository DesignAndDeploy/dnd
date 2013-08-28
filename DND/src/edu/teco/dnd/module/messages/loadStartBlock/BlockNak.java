package edu.teco.dnd.module.messages.loadStartBlock;

import edu.teco.dnd.network.messages.Response;

/**
 * 
 * Send, when starting of block failed.
 */
public class BlockNak extends Response {
	// TODO: say which block failed.
	public static String MESSAGE_TYPE = "block nak";
}
