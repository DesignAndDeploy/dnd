package edu.teco.dnd.module.messages.killApp;

import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

/**
 * send when a new Application is supposed to be started.
 * 
 * @author Marvin Marx
 * 
 */
public class KillAppNak extends Response {

	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "startAppNak";

	public UUID appId;

	public KillAppNak(UUID appId) {
		this.appId = appId;
	}

	public KillAppNak(KillAppMessage msg) {
		this.appId = msg.getApplicationID();
	}

	@SuppressWarnings("unused")
	/* for gson */
	private KillAppNak() {
		appId = null;
	}
}
