package edu.teco.dnd.module.messages.values;

import edu.teco.dnd.network.messages.Response;

/**
 * send when a value was successfully received.
 * 
 * @author Marvin Marx
 * 
 */
public class ValueAck extends Response {

	public static final String MESSAGE_TYPE = "value ack";

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
