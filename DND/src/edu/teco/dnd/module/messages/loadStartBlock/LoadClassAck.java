package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.UUID;

import edu.teco.dnd.network.messages.Message;


public class LoadClassAck extends Message {

	public static final String MESSAGE_TYPE = "load class ack";

	public UUID appId;
	public String className;

	public LoadClassAck(String className, UUID appId) {
		this.className = className;
		this.appId = appId;
	}

	@SuppressWarnings("unused")
	/* for gson */
	private LoadClassAck() {
		className = null;
		appId = null;
	}

}
