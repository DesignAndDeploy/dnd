package edu.teco.dnd.module.messages.loadStartClass;

import java.util.UUID;

import edu.teco.dnd.network.messages.Message;

public class AppLoadClassNak implements Message {
	public static final String MESSAGE_TYPE = "appLoadClassNak";

	public String name;
	public UUID appId;
	
	public AppLoadClassNak(String name, UUID appId) {
		this.name = name;
		this.appId = appId;
	}

	
	@SuppressWarnings("unused")/*for gson*/
	private AppLoadClassNak() {
		name = null;
		appId = null;
	}

}
