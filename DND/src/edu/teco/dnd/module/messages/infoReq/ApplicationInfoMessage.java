package edu.teco.dnd.module.messages.infoReq;


import java.util.UUID;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.network.messages.Response;


public class ApplicationInfoMessage extends Response {
	public static final String MESSAGE_TYPE = "application info";
	
	public final String appName;
	public final UUID appId;
	
	public ApplicationInfoMessage(Application app){
		appName = app.getName();
		appId = app.getOwnAppId();
	}

}
