package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

public class BlockNak extends Response {

	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "block nak";

	public String className;

	public BlockNak(String className, UUID appId) {
		this.className = className;
	}

	@SuppressWarnings("unused")
	/* for gson */
	private BlockNak() {
		className = null;
	}

}
