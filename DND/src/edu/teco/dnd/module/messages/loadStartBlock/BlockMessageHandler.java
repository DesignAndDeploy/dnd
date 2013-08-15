package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.UUID;

import edu.teco.dnd.module.ModuleApplicationManager;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

public class BlockMessageHandler implements MessageHandler<BlockMessage> {
	final ModuleApplicationManager appManager;

	public BlockMessageHandler(ModuleApplicationManager appManager) {
		this.appManager = appManager;
	}

	@Override
	public Response handleMessage(ConnectionManager connMan, UUID remoteUUID, BlockMessage message) {
		try {
			appManager.scheduleBlock(message.getApplicationID(), message.block, message.scheduleToId);
		} catch (Exception e) {
			return new BlockNak();
		}
		return new BlockAck();
	}
}
