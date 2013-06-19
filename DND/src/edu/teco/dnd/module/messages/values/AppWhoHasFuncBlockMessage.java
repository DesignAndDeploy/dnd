package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class AppWhoHasFuncBlockMessage implements ApplicationSpecificMessage {
	public final UUID appId;
	public final UUID funcBlock;
	
	public AppWhoHasFuncBlockMessage(UUID appId, UUID funcBlock) {
		this.appId = appId;
		this.funcBlock = funcBlock;
	}

	@Override
	public UUID getApplicationID() {
		return appId;
	}

}
