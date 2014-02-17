package edu.teco.dnd.module.messages.infoReq;

import java.util.ArrayList;
import java.util.Collection;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.module.FunctionBlockSecurityDecorator;
import edu.teco.dnd.module.Module;
import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.network.MessageHandler;
import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.server.ApplicationInformation;
import edu.teco.dnd.server.BlockInformation;

/**
 * Handles {@link RequestApplicationInformationMessage} by sending an {@link ApplicationInformationResponse}.
 * 
 * @author Philipp Adolf
 */
public class RequestApplicationInformationMessageHandler implements
		MessageHandler<RequestApplicationInformationMessage> {
	private final ModuleID moduleID;
	private final Module module;

	public RequestApplicationInformationMessageHandler(final ModuleID moduleID, final Module module) {
		this.moduleID = moduleID;
		this.module = module;
	}

	@Override
	public Response handleMessage(final ModuleID remoteID, final RequestApplicationInformationMessage message) {
		final Collection<ApplicationInformation> applications = new ArrayList<ApplicationInformation>();
		for (final Application application : module.getApplications().values()) {
			final Collection<BlockInformation> blocks = new ArrayList<BlockInformation>();
			for (final FunctionBlockSecurityDecorator block : application.getFunctionBlocksById().values()) {
				blocks.add(new BlockInformation(block.getBlockID(), block.getBlockName(), block.getBlockType(),
						moduleID));
			}
			applications.add(new ApplicationInformation(application.getApplicationID(), application.getName(), blocks));
		}
		return new ApplicationInformationResponse(moduleID, applications);
	}
}
