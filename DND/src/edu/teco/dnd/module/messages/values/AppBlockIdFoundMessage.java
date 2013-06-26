package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class AppBlockIdFoundMessage extends ApplicationSpecificMessage {
	public final UUID modId;
	public final UUID funcBlock;
	
	public AppBlockIdFoundMessage(UUID appId, UUID modId, UUID funcBlock) {
		super(appId);
		this.modId = modId;
		this.funcBlock = funcBlock;
	}
}