package edu.teco.dnd.module.messages.infoReq;


import java.util.UUID;

import edu.teco.dnd.module.Application;
import edu.teco.dnd.network.messages.Message;

public class ApplicationInfoMessage implements Message {
	public static final String MESSAGE_TYPE = "ApplicationInformation";
	
	public final String appName;
	public final UUID appId;
	
	public ApplicationInfoMessage(Application app){
		appName = app.name;
		appId = app.ownAppId;
	}

}
