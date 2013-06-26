package edu.teco.dnd.module.messages.joinStartApp;

import java.util.UUID;

import edu.teco.dnd.network.messages.Message;

/**
 * send when a new Application is supposed to be started.
 * @author Marvin Marx
 *
 */

public class JoinApplicationNak extends Message {
	
	
	public static final String MESSAGE_TYPE = "startAppNak";
	
	public String name;
	public UUID appId;
	
	public JoinApplicationNak(String name, UUID appId) {
		this.name = name;
		this.appId = appId;
	}
	
	public JoinApplicationNak(JoinApplicationMessage msg) {
		this.name = msg.name;
		this.appId = msg.appId;
	}
	
	@SuppressWarnings("unused")/*for gson*/
	private JoinApplicationNak() {
		name = null;
		appId = null;
	}
}
