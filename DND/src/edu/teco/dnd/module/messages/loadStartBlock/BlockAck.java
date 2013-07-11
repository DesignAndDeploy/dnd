package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

public class BlockAck extends Response {

	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "block ack";

	public UUID appId;
	public String className;

	public BlockAck(String className, UUID appId) {
		this.className = className;
		this.appId = appId;
	}

	@SuppressWarnings("unused")
	/* for gson */
	private BlockAck() {
		className = null;
		appId = null;
	}

}
