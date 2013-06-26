package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.UUID;

import edu.teco.dnd.network.messages.Message;

public class LoadClassNak extends Message {
	public static final String MESSAGE_TYPE = "load class nak";

	public UUID appId;
	public String name;

	public LoadClassNak(String name, UUID appId) {
		this.name = name;
		this.appId = appId;
	}

	@SuppressWarnings("unused")
	/* for gson */
	private LoadClassNak() {
		name = null;
		appId = null;
	}

}
