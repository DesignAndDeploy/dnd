package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.NonExistentFunctionblockException;
import edu.teco.dnd.module.NonExistentInputException;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Message;

public class AppValueMessageHandler implements MessageHandler<AppValueMessage> {
	final Application associatedApp;

	public AppValueMessageHandler(Application associatedApp) {
		this.associatedApp = associatedApp;

	}

	//TODO register handlers for replies.
	@Override
	public void handleMessage(ConnectionManager connMan, UUID remoteUUID, AppValueMessage message) {
		Message returnMsg = null;
		try {
			associatedApp.receiveValue(message.functionBlock, message.input, message.value);
		} catch (NonExistentFunctionblockException e) {
			returnMsg = new AppValueNak(message.getApplicationID(), AppValueNak.ErrorType.WRONG_MODULE, message.functionBlock, message.input);
		} catch (NonExistentInputException e) {
			returnMsg =  new AppValueNak(message.getApplicationID(), AppValueNak.ErrorType.INVALID_INPUT, message.functionBlock, message.input);
		} catch (Exception e) {
			returnMsg = new AppValueNak(message.getApplicationID(), AppValueNak.ErrorType.OTHER, message.functionBlock, message.input);
		}
		
		if (returnMsg == null) {
			returnMsg = new AppValueAck(message.getApplicationID());
		}
		connMan.sendMessage(remoteUUID, returnMsg);
	}
}
