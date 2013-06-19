package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class AppBlockIdFoundMessage implements ApplicationSpecificMessage {
	public final UUID appId;
	public final UUID modId;
	public final UUID funcBlock;
	
	
	public AppBlockIdFoundMessage(UUID appId, UUID modId, UUID funcBlock) {
		this.appId = appId;
		this.modId = modId;
		this.funcBlock = funcBlock;
	}

	@Override
	public UUID getApplicationID() {
		return appId;
	}

}
