package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.UUID;

import edu.teco.dnd.network.messages.Message;

public class BlockNak extends Message {
	public static final String MESSAGE_TYPE = "block nak";

	public UUID appId;
	public String className;

	public BlockNak(String className, UUID appId) {
		this.className = className;
		this.appId = appId;
	}
	
	@SuppressWarnings("unused")
	/* for gson */
	private BlockNak() {
		className = null;
		appId = null;
	}

}
