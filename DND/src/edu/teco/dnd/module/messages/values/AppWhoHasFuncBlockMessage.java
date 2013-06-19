package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class AppWhoHasFuncBlockMessage extends ApplicationSpecificMessage {
	public final UUID funcBlock;
	
	public AppWhoHasFuncBlockMessage(UUID appId, UUID funcBlock) {
		super(appId);
		this.funcBlock = funcBlock;
	}
}
