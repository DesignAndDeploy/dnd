package edu.teco.dnd.module.messages.loadStartBlock;

import edu.teco.dnd.network.messages.Response;

/**
 * 
 * Send, when starting of block failed.
 */
public class BlockNak extends Response {
	public static final String MESSAGE_TYPE = "block nak";
}
