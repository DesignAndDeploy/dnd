package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.NonExistentFunctionblockException;
import edu.teco.dnd.module.NonExistentInputException;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;

public class AppValueMessageHandler implements MessageHandler<AppValueMessage> {
	final Application associatedApp;

	public AppValueMessageHandler(Application associatedApp) {
		this.associatedApp = associatedApp;

	}

	@Override
	public void handleMessage(ConnectionManager connMan, UUID remoteUUID, AppValueMessage message) {
		try {
			associatedApp.receiveValue(message.functionBlock, message.input, message.value);
		} catch (NonExistentFunctionblockException e) {
			connMan.sendMessage(remoteUUID, new AppValueNak(message.appId, AppValueNak.ErrorType.WRONG_MODULE));
			return;
		} catch (NonExistentInputException e) {
			connMan.sendMessage(remoteUUID, new AppValueNak(message.appId, AppValueNak.ErrorType.INVALID_INPUT));
		} catch (Exception e) {
			connMan.sendMessage(remoteUUID, new AppValueNak(message.appId, AppValueNak.ErrorType.OTHER));
		}
		connMan.sendMessage(remoteUUID, new AppValueAck(message.appId));
	}

}
