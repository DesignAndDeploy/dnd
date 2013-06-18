package edu.teco.dnd.module.messages.loadStartClass;

import java.util.UUID;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.network.messages.ApplicationSpecificMessage;


public class AppStartClassMessage implements ApplicationSpecificMessage {

	public static final String MESSAGE_TYPE = "appStartClass";

	public String className;
	public UUID appId;
	private FunctionBlock funBlock;

	public AppStartClassMessage(String className, UUID appId, FunctionBlock funBlock) {
		this.className = className;
		this.appId = appId;
		this.funBlock = funBlock;
	}

	@Override
	public UUID getApplicationID() {
		return appId;
	}

	public FunctionBlock getFunctionBlock() {
		return funBlock;
	}
}
