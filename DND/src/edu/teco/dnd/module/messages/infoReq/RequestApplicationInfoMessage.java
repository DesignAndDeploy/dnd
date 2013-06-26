package edu.teco.dnd.module.messages.infoReq;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class RequestApplicationInfoMessage extends ApplicationSpecificMessage{
	public static final String MESSAGE_TYPE = "request application info";
	
	public RequestApplicationInfoMessage(UUID appId) {
		super(appId);
	}

}
