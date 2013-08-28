package edu.teco.dnd.module.messages.generalModule;

import edu.teco.dnd.network.messages.Response;

/**
 * send to acknowledge the successful shutdown of a module.
 * 
 * @author Marvin Marx
 * 
 */
public class ShutdownModuleAck extends Response {
	public static String MESSAGE_TYPE = "shutdown module ack";

	public ShutdownModuleAck() {

	}
}
