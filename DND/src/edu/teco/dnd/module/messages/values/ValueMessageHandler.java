package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.NonExistentFunctionblockException;
import edu.teco.dnd.module.NonExistentInputException;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

public class ValueMessageHandler implements MessageHandler<ValueMessage> {
	final Application associatedApp;

	public ValueMessageHandler(Application associatedApp) {
		this.associatedApp = associatedApp;

	}

	@Override
	public Response handleMessage(ConnectionManager connMan, UUID remoteUUID, ValueMessage message) {
		Response returnMsg = null;
		try {
			associatedApp.receiveValue(message.blockId, message.input, message.value);
		} catch (NonExistentFunctionblockException e) {
			returnMsg =
					new ValueNak(message.getApplicationID(), ValueNak.ErrorType.WRONG_MODULE, message.blockId,
							message.input);
		} catch (NonExistentInputException e) {
			returnMsg =
					new ValueNak(message.getApplicationID(), ValueNak.ErrorType.INVALID_INPUT, message.blockId,
							message.input);
		} catch (Exception e) {
			returnMsg =
					new ValueNak(message.getApplicationID(), ValueNak.ErrorType.OTHER, message.blockId, message.input);
		}

		if (returnMsg == null) {
			returnMsg = new ValueAck(message.getApplicationID());
		}
		return returnMsg;
	}
}
