package edu.teco.dnd.module.messages.loadStartClass;

import java.util.UUID;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.network.messages.ApplicationSpecificMessage;


public class AppStartClassMessage extends ApplicationSpecificMessage {

	public static final String MESSAGE_TYPE = "appStartClass";

	public String className;
	private FunctionBlock funBlock;

	public AppStartClassMessage(String className, UUID appId, FunctionBlock funBlock) {
		super(appId);
		this.className = className;
		this.funBlock = funBlock;
	}

	public FunctionBlock getFunctionBlock() {
		return funBlock;
	}
}
