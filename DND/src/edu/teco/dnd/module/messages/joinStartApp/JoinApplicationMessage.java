package edu.teco.dnd.module.messages.joinStartApp;

import java.util.UUID;

import edu.teco.dnd.network.messages.Message;

/**
 * send when a new Application is supposed to be started.
 * @author Marvin Marx
 *
 */

public class JoinApplicationMessage extends Message {
	
	
	public static final String MESSAGE_TYPE = "join application";
	
	public UUID appId;
	public String name;
	
	
	public JoinApplicationMessage(String name, UUID appId) {
		this.name = name;
		this.appId = appId;
	}
	
	@SuppressWarnings("unused")/*for gson*/
	private JoinApplicationMessage() {
		name = null;
		appId = null;
	}
}
