package edu.teco.dnd.module.messages.killApp;

import edu.teco.dnd.module.ApplicationID;
import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

/**
 * send when a new Application is supposed to be stopped.
 * 
 * @author Marvin Marx
 * 
 */

public class KillAppMessage extends ApplicationSpecificMessage {

	public static final String MESSAGE_TYPE = "kill";

	/**
	 * 
	 * @param applicationID
	 *            UUID of the app supposed to be stopped. Note it does not need to be stored in the message, because the
	 *            message is simply SEND to the appropriate App, which is enough.
	 */
	public KillAppMessage(ApplicationID applicationID) {
		super(applicationID);
	}

	/* for gson. */
	private KillAppMessage() {
		super(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "KillAppMessage [" + super.toString() + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		return getClass() == obj.getClass(); // KillAppMsg = every other KillAppMsg

	}

	@Override
	public int hashCode() {
		return 72347826; // does not matter. All KillAppMsgs are equal.
	}

}
