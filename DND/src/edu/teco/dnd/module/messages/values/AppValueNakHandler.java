package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;

public class AppValueNakHandler implements MessageHandler<AppValueNak> {

	private static transient final Logger LOGGER = LogManager.getLogger(AppValueMessageHandler.class);

	private final Application associatedApp;

	public AppValueNakHandler(Application associatedApp) {
		this.associatedApp = associatedApp;
	}

	@Override
	public void handleMessage(ConnectionManager connectionManager, UUID remoteUUID, AppValueNak message) {
		switch (message.errorType) {
		case WRONG_MODULE:
			associatedApp.invalidateBlockModulePair(message.funcBlockId, remoteUUID);
			break;
		case INVALID_INPUT:
			LOGGER.info("input: {} on Block: {} is invalid. Value was ignored.", message.inputId, message.funcBlockId);
			break;
		case OTHER:
			LOGGER.info("Can not deliver Msg to {}-{}", message.funcBlockId, message.inputId);
			break;
		default:
			assert false : "unhandled error type returned by AppValueNak";
			break;
		}

	}

}
