package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class BlockFoundMessage extends ApplicationSpecificMessage {
	public static final String MESSAGE_TYPE = "block found";
	public final UUID block;
	public final UUID moduleId;
	
	
	public BlockFoundMessage(UUID appId, UUID moduleId, UUID block) {
		super(appId);
		this.moduleId = moduleId;
		this.block = block;
	}


}
