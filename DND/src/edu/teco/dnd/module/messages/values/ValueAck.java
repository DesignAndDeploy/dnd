package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

public class ValueAck extends Response {

	public static String MESSAGE_TYPE = "value ack";

	public ValueAck(UUID appId) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ValueAck [getSourceUUID()=" + getSourceUUID() + ", getUUID()=" + getUUID() + "]";
	}

}
