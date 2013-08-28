package edu.teco.dnd.module.messages.generalModule;

import java.util.UUID;

import edu.teco.dnd.network.messages.Response;

/**
 * Message being send if a message was received for an application that does not reside on this module.
 * 
 * @author Marvin Marx
 * 
 */
public class MissingApplicationNak extends Response {

	/**
	 * Message Type.
	 */
	public static String MESSAGE_TYPE = "missing app nak";

	/**
	 * UUID that the original message was for.
	 */
	public UUID appId;

	/**
	 * @param appId
	 *            UUID that the original message was for.
	 */
	public MissingApplicationNak(UUID appId) {
		this.appId = appId;
	}

	/**
	 * private constructor for gson.
	 */
	@SuppressWarnings("unused")
	/* for gson */
	private MissingApplicationNak() {
		appId = null;
	}
}
