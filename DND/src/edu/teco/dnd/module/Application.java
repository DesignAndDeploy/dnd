package edu.teco.dnd.module;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import edu.teco.dnd.blocks.FunctionBlock;

public class Application {

	private UUID ownAppId;
	public final String name;

	public Application(UUID appId, UUID deployingAgentId, String name) {
		ownAppId = appId;
		this.name = name;
		// TODO is started now and should run until shutdown called. Do App stuff.
	}
	
	
	public boolean loadClass(Set<String> classnames, String mainclassname) {
		return false;
	}
	
	public boolean startBlock(FunctionBlock block){
		return false;
	}
	
	public boolean receiveValue(String funcBlockId, String input, Serializable value) {
		return false;
	}
	public boolean shutdown() {
		//TODO
		return false;
	}
}
