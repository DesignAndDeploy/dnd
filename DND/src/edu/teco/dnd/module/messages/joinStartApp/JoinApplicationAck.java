package edu.teco.dnd.module.messages.joinStartApp;

import java.util.UUID;

import edu.teco.dnd.module.messages.joinStartApp.StartAppMessage.JoinApplicationMessage;
import edu.teco.dnd.network.messages.Message;

/**
 * send when a new Application is supposed to be started.
 * @author Marvin Marx
 *
 */
public class JoinApplicationAck extends Message {
	
	
	public static final String MESSAGE_TYPE = "join application ack";
	
	public UUID appId;
	public String name;
	
	public JoinApplicationAck(String name, UUID appId) {
		this.name = name;
		this.appId = appId;
	}
	
	public JoinApplicationAck(JoinApplicationMessage msg) {
		this.name = msg.name;
		this.appId = msg.appId;
	}
	
	@SuppressWarnings("unused")/*for gson*/
	private JoinApplicationAck() {
		name = null;
		appId = null;
	}
}
