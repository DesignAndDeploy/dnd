package edu.teco.dnd.module.messages.values;

import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

/**
 * send when a value was successfully received.
 * 
 * @author Marvin Marx
 * 
 */
public class ValueAck extends Response {

	public static String MESSAGE_TYPE = "value ack";

	/**
	 * ID of the application this is send by.
	 */
	public final UUID appId;

	/**
	 * 
	 * @param appId
	 *            ID of the application this is send by.
	 */
	public ValueAck(UUID appId) {
		this.appId = appId;
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
