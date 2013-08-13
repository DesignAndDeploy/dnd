package edu.teco.dnd.module.messages.generalModule;

import java.util.UUID;

import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Message;
import edu.teco.dnd.network.messages.Response;

public class MissingApplicationHandler implements MessageHandler<Message> {

	@Override
	public Response handleMessage(ConnectionManager connectionManager, UUID remoteUUID, Message message) {
		return new MissingApplicationNak(remoteUUID);
	}

}
