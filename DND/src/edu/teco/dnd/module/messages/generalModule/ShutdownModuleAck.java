package edu.teco.dnd.module.messages.generalModule;

import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

public class ShutdownModuleAck extends Response {
	public static String MESSAGE_TYPE = "shutdown module ack";

	public ShutdownModuleAck(UUID sourceUUID) {
		super(sourceUUID);
	}

	@SuppressWarnings("unused")
	// used by gson
	private ShutdownModuleAck() {

	}
}
