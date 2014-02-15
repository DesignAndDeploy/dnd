package edu.teco.dnd.module.messages.infoReq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import edu.teco.dnd.module.ModuleID;
import edu.teco.dnd.network.messages.Response;
import edu.teco.dnd.server.ApplicationInformation;

/**
 * Contains ApplicationInformation about all Applications running on the sending Module.
 */
public class ApplicationInformationResponse extends Response {
	public static final String MESSAGE_TYPE = "application information";

	final Collection<ApplicationInformation> applications;

	private final ModuleID moduleID;

	public ApplicationInformationResponse(final ModuleID moduleID, final Collection<ApplicationInformation> applications) {
		this.moduleID = moduleID;
		this.applications = Collections.unmodifiableCollection(new ArrayList<ApplicationInformation>(applications));
	}

	public ModuleID getModuleID() {
		return moduleID;
	}

	public Collection<ApplicationInformation> getApplications() {
		return applications;
	}

	@Override
	public String toString() {
		return "ApplicationInformationResponse[applications=" + applications + ",moduleID=" + moduleID + "]";
	}
}
