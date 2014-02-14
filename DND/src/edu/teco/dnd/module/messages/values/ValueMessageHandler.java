package edu.teco.dnd.module.messages.values;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.module.NonExistentFunctionblockException;
import edu.teco.dnd.module.NonExistentInputException;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

/**
 * triggers sending the value to the appropriate Application.FunctionBlock.
 * 
 * @author Marvin Marx
 * 
 */
public class ValueMessageHandler implements MessageHandler<ValueMessage> {
	/**
	 * The application the FunctionBlock to retrieve the message is running on.
	 */
	private final Application associatedApp;

	/**
	 * 
	 * @param associatedApp
	 *            The application the FunctionBlock to retrieve the message is running on.
	 */
	public ValueMessageHandler(Application associatedApp) {
		this.associatedApp = associatedApp;

	}

	@Override
	public Response handleMessage(ModuleID remoteID, ValueMessage message) {
		Response returnMsg = null;
		try {
			associatedApp.receiveValue(message.blockId, message.input, message.value);
		} catch (NonExistentFunctionblockException e) {
			returnMsg =
					new ValueNak(ValueNak.ErrorType.WRONG_MODULE, message.blockId,
							message.input);
		} catch (NonExistentInputException e) {
			returnMsg =
					new ValueNak(ValueNak.ErrorType.INVALID_INPUT, message.blockId,
							message.input);
		} catch (Exception e) {
			returnMsg =
					new ValueNak(ValueNak.ErrorType.OTHER, message.blockId, message.input);
		}

		if (returnMsg == null) {
			returnMsg = new ValueAck();
		}
		return returnMsg;
	}
}
