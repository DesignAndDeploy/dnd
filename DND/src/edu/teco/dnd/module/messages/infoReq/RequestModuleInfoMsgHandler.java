package edu.teco.dnd.module.messages.infoReq;

import java.util.UUID;

import edu.teco.dnd.module.ModuleInfo;
import edu.teco.dnd.module.config.ConfigReader;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

/**
 * Handler for ModuleInfoMessages. Replies with the appropriate ModuleInfoMessage.
 * 
 * @author Marvin Marx
 * 
 */
public class RequestModuleInfoMsgHandler implements MessageHandler<RequestModuleInfoMessage> {
	private final ConfigReader conf;

	/**
	 * set up a new handler.
	 * 
	 * @param conf
	 *            the configurationReader used to extract the information about the module.
	 */
	public RequestModuleInfoMsgHandler(ConfigReader conf) {
		this.conf = conf;
	}

	@Override
	public Response handleMessage(UUID remoteUUID, RequestModuleInfoMessage message) {
		return new ModuleInfoMessage(new ModuleInfo(conf.getUuid(), conf.getName(), conf.getBlockRoot()));
	}

}
