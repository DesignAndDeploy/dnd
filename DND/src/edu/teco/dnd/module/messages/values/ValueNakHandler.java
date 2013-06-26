package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.network.ConnectionManager;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

public class ValueNakHandler implements MessageHandler<ValueNak> {

	private static transient final Logger LOGGER = LogManager.getLogger(ValueMessageHandler.class);

	private final Application associatedApp;

	public ValueNakHandler(Application associatedApp) {
		this.associatedApp = associatedApp;
	}

	@Override
	public Response handleMessage(ConnectionManager connectionManager, UUID remoteUUID, ValueNak message) {
		switch (message.errorType) {
		case WRONG_MODULE:
			associatedApp.invalidateBlockModulePair(message.blockId, remoteUUID);
			break;
		case INVALID_INPUT:
			LOGGER.info("input: {} on Block: {} is invalid. Value was ignored.", message.input, message.blockId);
			break;
		case OTHER:
			LOGGER.info("Can not deliver Msg to {}-{}", message.blockId, message.input);
			break;
		default:
			assert false : "unhandled error type returned by AppValueNak";
			break;
		}
		
		return null;

	}

}
