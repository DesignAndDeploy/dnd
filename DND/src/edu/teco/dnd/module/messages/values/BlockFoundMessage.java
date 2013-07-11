package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

public class BlockFoundMessage extends Response {

	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "block found";

	public final UUID block;
	public final UUID moduleId;

	public BlockFoundMessage(UUID appId, UUID moduleId, UUID block) {
		this.moduleId = moduleId;
		this.block = block;
	}

}
