package edu.teco.dnd.module.messages.infoReq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import edu.teco.dnd.discover.ApplicationInformation;
import edu.teco.dnd.discover.BlockInformation;
import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.FunctionBlockSecurityDecorator;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;

/**
 * Handles {@link RequestApplicationInformationMessage} by sending an {@link ApplicationInformationResponse}.
 * 
 * @author Philipp Adolf
 */
public class RequestApplicationInformationMessageHandler implements
		MessageHandler<RequestApplicationInformationMessage> {
	private final UUID moduleUUID;
	private final Module module;

	public RequestApplicationInformationMessageHandler(final UUID moduleUUID, final Module module) {
		this.moduleUUID = moduleUUID;
		this.module = module;
	}

	@Override
	public Response handleMessage(final UUID remoteUUID, final RequestApplicationInformationMessage message) {
		final Collection<ApplicationInformation> applications = new ArrayList<ApplicationInformation>();
		for (final Application application : module.getRunningApps().values()) {
			final Collection<BlockInformation> blocks = new ArrayList<BlockInformation>();
			for (final FunctionBlockSecurityDecorator block : application.getFunctionBlocksById().values()) {
				blocks.add(new BlockInformation(block.getBlockUUID(), block.getBlockName(), block.getBlockType(),
						moduleUUID));
			}
			applications.add(new ApplicationInformation(application.getApplicationID(), application.getName(), blocks));
		}
		return new ApplicationInformationResponse(moduleUUID, applications);
	}
}
