package edu.teco.dnd.module.messages.loadStartBlock;

import edu.teco.dnd.network.messages.Response;

/**
 * Acknowledge receipt & successful processing of a BlockMessage.
 * 
 */
public class BlockAck extends Response {
	public static String MESSAGE_TYPE = "block ack";
}
