package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.UUID;

import edu.teco.dnd.meeting.OutletSensorBlock;
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
			Thread.sleep(5000 + ((int) Math.random() * 5000));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (appManager.scheduleBlock(message.getApplicationID(), message.blockClass, message.blockUUID,
				message.options, message.outputs, message.scheduleToId)) {
			return new BlockAck();
		} else {
			return new BlockNak();
		}
	}
}
