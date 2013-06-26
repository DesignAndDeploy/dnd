package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;

public class BlockMessageHandler implements MessageHandler<BlockMessage> {
	final ModuleApplicationManager appManager;

	public BlockMessageHandler(ModuleApplicationManager appManager) {
		this.appManager = appManager;
	}

	@Override
	public void handleMessage(ConnectionManager connMan, UUID remoteUUID, BlockMessage message) {
		//TODO whatever deserializes this message probably wants the apps classloader!
		if(appManager.scheduleBlock(message.getApplicationID(), message.block)) {
			connMan.sendMessage(remoteUUID, new BlockAck(message.className, message.appId));
		} else {
			connMan.sendMessage(remoteUUID, new BlockNak(message.className, message.appId));
		}

	}

}
