package edu.teco.dnd.module.messages.loadStartClass;

import java.util.UUID;

import edu.teco.dnd.network.messages.Message;

public class AppLoadClassAck extends Message {

	public static final String MESSAGE_TYPE = "AppLoadClassAck";

	public String className;
	public UUID appId;

	public AppLoadClassAck(String className, UUID appId) {
		this.className = className;
		this.appId = appId;
	}

	@SuppressWarnings("unused")
	/* for gson */
	private AppLoadClassAck() {
		className = null;
		appId = null;
	}

}
