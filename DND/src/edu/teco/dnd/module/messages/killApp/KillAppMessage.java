package edu.teco.dnd.module.messages.killApp;

import java.util.UUID;

import edu.teco.dnd.network.messages.ApplicationSpecificMessage;

/**
 * send when a new Application is supposed to be stopped.
 * 
 * @author Marvin Marx
 * 
 */

public class KillAppMessage extends ApplicationSpecificMessage {

	public static String MESSAGE_TYPE = "kill";

	public KillAppMessage(UUID appId) {
		super(appId);
	}

	/* for gson */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		} else {
			return true; // KillAppMsg = every other KillAppMsg
		}
	}

}
