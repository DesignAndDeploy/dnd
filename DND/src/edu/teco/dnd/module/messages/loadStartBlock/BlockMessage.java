package edu.teco.dnd.module.messages.loadStartBlock;

import java.util.UUID;

import edu.teco.dnd.blocks.FunctionBlock;
import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

public class BlockMessage extends ApplicationSpecificMessage {

	@SuppressWarnings("unused")
	// used by Gson
	private static String MESSAGE_TYPE = "block";

	public String className;
	public FunctionBlock block;

	public BlockMessage(String className, UUID appId, FunctionBlock funBlock) {
		super(appId);
		this.className = className;
		this.block = funBlock;
	}

}
