package edu.teco.dnd.module.messages.generalModule;

import java.util.UUID;

import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Message;
import edu.teco.dnd.network.messages.Response;

/**
 * Replies with a NAK if a message was received for an application, that does not reside on this module.
 * 
 * @author Marvin Marx
 * 
 */
public class MissingApplicationHandler implements MessageHandler<Message> {

	@Override
	public Response handleMessage(UUID remoteUUID, Message message) {
		return new MissingApplicationNak(remoteUUID);
	}

}
