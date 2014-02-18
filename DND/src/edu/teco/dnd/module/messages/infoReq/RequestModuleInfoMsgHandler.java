package edu.teco.dnd.module.messages.infoReq;

import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.module.ModuleInfo;
import edu.teco.dnd.module.config.ModuleConfig;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

/**
 * Handler for ModuleInfoMessages. Replies with the appropriate ModuleInfoMessage.
 * 
 * @author Marvin Marx
 * 
 */
public class RequestModuleInfoMsgHandler implements MessageHandler<RequestModuleInfoMessage> {
	private final ModuleConfig conf;

	/**
	 * set up a new handler.
	 * 
	 * @param conf
	 *            the configurationReader used to extract the information about the module.
	 */
	public RequestModuleInfoMsgHandler(ModuleConfig conf) {
		this.conf = conf;
	}

	@Override
	public Response handleMessage(ModuleID remoteID, RequestModuleInfoMessage message) {
		return new ModuleInfoMessage(new ModuleInfo(conf.getModuleID(), conf.getName(), conf.getLocation(),
				conf.getBlockRoot()));
	}

}
