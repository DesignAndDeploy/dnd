package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class WhoHasBlockMessage extends ApplicationSpecificMessage {
	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "who has block";
	public final UUID blockId;

	public WhoHasBlockMessage(UUID appId, UUID blockId) {
		super(appId);
		this.blockId = blockId;
	}
}
